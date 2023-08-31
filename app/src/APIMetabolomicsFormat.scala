package fr.inrae.metabolomics.p2m2.api

import cask.main.Main
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, MassSpectrometryResultSet, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, Xcalibur}
import fr.inrae.metabolomics.p2m2.parser.{GCMSParser, OpenLabCDSParser, ParserManager, QuantifySummaryReportMassLynxParser, XcaliburXlsParser}
import org.slf4j.LoggerFactory
import upickle.default._

import scala.util.{Failure, Success, Try}

object APIMetabolomicsFormat extends cask.MainRoutes {
    private val logger = LoggerFactory.getLogger(APIMetabolomicsFormat.getClass)

    //@cask.decorators.compress
    @cask.post("/p2m2tools/api/format/sniffer")
    def sniffer(request: cask.Request): ujson.Obj = {
        val format = request.bytes match {
            case contentFile if GCMSParser.sniffByteArray(contentFile) => "gcms"
            case contentFile if OpenLabCDSParser.sniffByteArray(contentFile) => "openlabcds"
            case contentFile if QuantifySummaryReportMassLynxParser.sniffByteArray(contentFile) => "masslynx"
            case contentFile if XcaliburXlsParser.sniffByteArray(contentFile) => "xcalibur"
            case _ => logger.warn("Unknown format");"unknown"
        }
        ujson.Obj(
            "format" -> format
        )
    }

    private def genericP2M2FormatToJson(obj : GenericP2M2) : ujson.Arr = {
        obj.samples.map (sample => ujson.Obj.from(sample.map { case (k,v) => k.toString -> ujson.Str(v) }))
    }

    @cask.post("/p2m2tools/api/format/parse")
    def parser(request: cask.Request) : ujson.Value = {
        ParserManager.buildMassSpectrometryObject(request.bytes) match {
            case Some(obj) => genericP2M2FormatToJson(obj.toGenericP2M2)
            case None => ujson.Obj()
        }
    }

    /**
     * Get Generic format of Metabolomics File GCSM format
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/gcms")
    def gcms(request: cask.Request) : ujson.Value = {
        Try(GCMSParser.parseByteArray(request.bytes)) match {
            case Success(obj : MassSpectrometryResultSet) => genericP2M2FormatToJson(obj.toGenericP2M2)
            case Failure(e) => System.err.println(e.toString);cask.Abort(401);ujson.Obj()
        }
    }

    /**
     * Get Generic format of Metabolomics File OpenLabCDS format
     *
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/openlabcds")
    def openlabcds(request: cask.Request) : ujson.Value = {
        Try(OpenLabCDSParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => genericP2M2FormatToJson(obj.toGenericP2M2)
            case Failure(e) => System.err.println(e.toString()); cask.Abort(401); ujson.Obj()
        }
    }

    /**
     * Get Generic format of Metabolomics File MassLynx format
     *
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/masslynx")
    def masslynx(request: cask.Request) : ujson.Value = {
        Try(QuantifySummaryReportMassLynxParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => genericP2M2FormatToJson(obj.toGenericP2M2)
            case Failure(e) => System.err.println(e.toString()); cask.Abort(401); ujson.Obj()
        }
    }

    /**
     * Get Generic format of Metabolomics File Xcalibur format
     *
     * @param request
     * @return
     */

    @cask.post("/p2m2tools/api/format/parse/xcalibur")
    def xcalibur(request: cask.Request) : ujson.Value = {
        Try(XcaliburXlsParser.parseByteArray(request.bytes)) match {
            case Success(obj: MassSpectrometryResultSet) => genericP2M2FormatToJson(obj.toGenericP2M2)
            case Failure(e) => System.err.println(e.toString()); cask.Abort(401); ujson.Obj()
        }
    }

    initialize()

}