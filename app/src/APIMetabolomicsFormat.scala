package fr.inrae.metabolomics.p2m2.api

import cask.decorators.compress
import io.undertow.Undertow
import io.undertow.server.handlers.BlockingHandler
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, MassSpectrometryResultSet, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, QuantifySummaryReportMassLynx, Xcalibur}
import fr.inrae.metabolomics.p2m2.parser.{GCMSParser, OpenLabCDSParser, ParserManager, ParserUtils, QuantifySummaryReportMassLynxParser, QuantitativeDataProcessingMassLynxParser, XcaliburXlsParser}
import org.slf4j.LoggerFactory
import upickle.default._

import scala.util.{Failure, Success, Try}

object APIMetabolomicsFormat extends cask.MainRoutes {
    private val logger = LoggerFactory.getLogger(APIMetabolomicsFormat.getClass)
    override def decorators: Seq[compress] = Seq(new cask.decorators.compress())
    
    var _server : Option[Undertow] = None

    override def defaultHandler: BlockingHandler =
        new BlockingHandler( CorsHandler(dispatchTrie,
        mainDecorators,
        debugMode = false,
        handleNotFound,
        handleMethodNotAllowed,
        handleEndpointError) )

    override def port: Int = 8080
    override def host: String = "0.0.0.0"

    override def main(args: Array[String]) : Unit = {
        logger.info(s"PORT:${port}")
        logger.info(s"HOST:${host}")
        
        val server: Undertow = Undertow.builder
          .addHttpListener(port, host)
          .setHandler(defaultHandler)
          .build

        _server = Some(server)
        server.start()

        logger.info(s" == start service ${this.getClass.getSimpleName} == ")
        @volatile var keepRunning = true

        Runtime.getRuntime().addShutdownHook(new Thread {
          override def run = {
            println("* catch signal / stop service *")
            server.stop()
            keepRunning = false
          }
        })

        //if (config.background) while (keepRunning) {}
       
    }

    def closeService() : Unit =  {
        _server match {
        case Some(s) => s.stop()
        case _ =>
        }
        this.executionContext.shutdown()
    }

    //@cask.decorators.compress
    @cask.post("/p2m2tools/api/format/sniffer")
    def sniffer(request: cask.Request): ujson.Obj = {
        val format = request.bytes match {
            case contentFile if GCMSParser.sniffByteArray(contentFile) => "gcms"
            case contentFile if OpenLabCDSParser.sniffByteArray(contentFile) => "openlabcds"
            case contentFile if QuantifySummaryReportMassLynxParser.sniffByteArray(contentFile) => "masslynx"
            // TODO : conversion XMLQuantitativeDataProcessingMassLynx to GenericP2M2 is not implemented.
            // case contentFile if QuantitativeDataProcessingMassLynxParser.sniffByteArray(contentFile) => "masslynx-xml"
            case contentFile if XcaliburXlsParser.sniffByteArray(contentFile) => "xcalibur"
            case _ => logger.warn("Unknown format");"unknown"
        }
        ujson.Obj(
            "format" -> format
        )
    }

    private def MapValuesToJson[T<: Enumeration](mapValues : Map[T#Value,String]) : ujson.Obj =
        ujson.Obj.from(mapValues.map { case (k,v) => ParserUtils.toString(k) -> ujson.Str(v) })

    private def SeqMapValuesToJson[T<: Enumeration](listValues : Seq[Map[T#Value,String]]) : ujson.Arr = {
        listValues.map (sample => MapValuesToJson(sample))
    }

    @cask.post("/p2m2tools/api/format/parse")
    def parser(request: cask.Request) : ujson.Value = {
        ParserManager.buildMassSpectrometryObject(request.bytes) match {
            case Some(obj) => SeqMapValuesToJson(obj.toGenericP2M2.samples)
            case None => ujson.Obj()
        }
    }


    @cask.post("/p2m2tools/api/format/parse/gcms")
    def gcms(request: cask.Request) : ujson.Value = {
        Try(GCMSParser.parseByteArray(request.bytes)) match {
            case Success(obj : GCMS) =>
                ujson.Obj(
                    "class" -> "GCMS",
                    "format" -> "gcms",
                    "origin" -> obj.origin,
                    "header" -> MapValuesToJson(obj.header),
                    "msQuantitativeResults" -> SeqMapValuesToJson(obj.msQuantitativeResults),
                    "request" -> ujson.Obj("size:"->request.bytes.length)
                )
            case Failure(e) =>
                System.err.println(e.getMessage);
                cask.Abort(401);ujson.Obj("error" -> e.getMessage)
        }
    }

    @cask.post("/p2m2tools/api/format/parse/gcms/generic")
    def gcmsToGenericP2M2(request: cask.Request): ujson.Value = {
        Try(GCMSParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => SeqMapValuesToJson(obj.toGenericP2M2.samples)
            case Failure(e) => System.err.println(e.getMessage); cask.Abort(401); ujson.Obj("error" -> e.getMessage)
        }
    }

    /**
     * Get Generic format of Metabolomics File OpenLabCDS format
     *
     * @param request
     * @return
     */
    @cask.post("/p2m2tools/api/format/parse/openlabcds")
    def openlabcds(request: cask.Request): ujson.Value = {
        Try(OpenLabCDSParser.parseByteArray(request.bytes)) match {
            case Success(obj: OpenLabCDS) => ujson.Obj(
                "class" -> "OpenLabCDS",
                "format" -> "openlabcds",
                "origin" -> obj.origin,
                "header" -> MapValuesToJson(obj.header),
                "results" -> SeqMapValuesToJson(obj.results),
                "request" -> ujson.Obj("size:"->request.bytes.length)
            )
            case Failure(e) => System.err.println(e.getMessage); cask.Abort(401); ujson.Obj("error" -> e.getMessage)
        }
    }

    @cask.post("/p2m2tools/api/format/parse/openlabcds/generic")
    def openlabcdsToGenericP2M2(request: cask.Request) : ujson.Value = {
        Try(OpenLabCDSParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => SeqMapValuesToJson(obj.toGenericP2M2.samples)
            case Failure(e) => System.err.println(e.getMessage); cask.Abort(401); ujson.Obj("error" -> e.getMessage)
        }
    }


    /**
     * Get Generic format of Metabolomics File MassLynx format
     *
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/masslynx")
    def masslynxTxt(request: cask.Request) : ujson.Value = {
        Try(QuantifySummaryReportMassLynxParser.parseByteArray(request.bytes)) match {
            case Success(obj: QuantifyCompoundSummaryReportMassLynx) => ujson.Obj(
                "class" -> "QuantifyCompoundSummaryReportMassLynx",
                "format" -> "masslynx",
                "origin" -> obj.origin,
                "dateStr" -> ujson.Str(obj.header.dateStr.getOrElse("")),
                "results" -> ujson.Obj.from(obj.resultsByCompound.map{ case (key, v) =>  key -> SeqMapValuesToJson(v)}),
                "request" -> ujson.Obj("size:"->request.bytes.length)

            )
            case Success(obj: QuantifySampleSummaryReportMassLynx) => ujson.Obj(
                "class" -> "QuantifySampleSummaryReportMassLynx",
                "format" -> "masslynx",
                "origin" -> obj.origin,
                "dateStr" -> ujson.Str(obj.header.dateStr.getOrElse("")),
                "results" -> ujson.Obj.from(obj.resultsBySample.map { case (key, v) => key -> SeqMapValuesToJson(v) }),
                "request" -> ujson.Obj("size:"->request.bytes.length)
            )
            case Success(obj) => ujson.Obj( "error" -> obj.toString )
            case Failure(e) =>
                cask.Abort(401);
                ujson.Obj("error" -> e.getMessage)
        }
    }

    @cask.post("/p2m2tools/api/format/parse/masslynx/generic")
    def masslynxTxtToGenericP2M2(request: cask.Request): ujson.Value = {
        Try(QuantifySummaryReportMassLynxParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => SeqMapValuesToJson(obj.toGenericP2M2.samples)
            case Failure(e) =>
                cask.Abort(401);
                ujson.Obj("error" -> e.getMessage)
        }
    }
/*
    @cask.post("/p2m2tools/api/format/parse/masslynx/xml")
    def masslynxXml(request: cask.Request): ujson.Value = {
        Try(QuantitativeDataProcessingMassLynxParser.parseByteArray(request.bytes)) match {
            case Success(obj: XMLQuantitativeDataProcessingMassLynx) => SeqMapValuesToJson(obj.toGenericP2M2)
            case Failure(e) =>
                cask.Abort(401);
                ujson.Obj("error" -> e.getMessage)
        }
    }
*/
    /**
     * Get Generic format of Metabolomics File Xcalibur format
     *
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/xcalibur")
    def xcalibur(request: cask.Request) : ujson.Value = {
        Try(XcaliburXlsParser.parseByteArray(request.bytes)) match {
            case Success(obj: Xcalibur) => ujson.Obj(
                "class" -> "Xcalibur",
                "format" -> "xcalibur",
                "origin" -> obj.origin,
                "results" -> obj.results.map( r => ujson.Obj(
                    "compoundInformationHeader" -> MapValuesToJson(r.compoundInformationHeader),
                    "compoundByInjection" -> SeqMapValuesToJson(r.compoundByInjection)
                )),
                "request" -> ujson.Obj("size:"->request.bytes.length)
            )
            case Failure(e) => System.err.println(e.getMessage); cask.Abort(401); ujson.Obj("error" -> e.getMessage)
        }
    }

    @cask.post("/p2m2tools/api/format/parse/xcalibur/generic")
    def xcaliburToGenericP2M2(request: cask.Request): ujson.Value = {
        Try(XcaliburXlsParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => SeqMapValuesToJson(obj.toGenericP2M2.samples)
            case Failure(e) => System.err.println(e.getMessage); cask.Abort(401); ujson.Obj("error" -> e.getMessage)
        }
    }

    initialize()

}