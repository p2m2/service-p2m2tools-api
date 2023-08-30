package fr.inrae.metabolomics.p2m2.api

import cask.main.Main

import fr.inrae.metabolomics.p2m2.format.MassSpectrometryResultSetFactory
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, MassSpectrometryResultSet, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, Xcalibur}
import fr.inrae.metabolomics.p2m2.parser.{GCMSParser, OpenLabCDSParser, ParserManager, QuantifySummaryReportMassLynxParser, XcaliburXlsParser}
import fr.inrae.metabolomics.p2m2.stream.ExportData

import scala.util.{Try,Success,Failure}

object APIMetabolomicsFormat extends cask.MainRoutes {

    def isGCMS(lines: List[String]) = Try(GCMSParser.parseHeader(lines)) match {
        case Success(m) if m.nonEmpty => true
        case _ => false
    }

    def isOpenLabCDS(lines: List[String]) = Try(OpenLabCDSParser.parseHeader(lines)) match {
        case Success(m) if m.nonEmpty => true
        case _ => false
    }

    def isXcalibur(is : InputStream): Boolean = {
            try {
                val workbook: HSSFWorkbook = new HSSFWorkbook(is)

                0.until(workbook.getNumberOfSheets)
                  .map(workbook.getSheetAt)
                  .exists(sheet => {
                      XLSParserUtil.getRowCellIndexesFromTerm(sheet, "Filename").headOption match {
                          case Some(_) => true
                          case None => false
                      }
                  })
            } catch {
                case _: Throwable => false
            }
        }
    }

    @cask.post("/api/p2m2tools/parsemetabolomicsfile")
    def parser(request: cask.Request) = {
        val lines = request.text().split("\n").slice(0,20).toList
        if (isGCMS(lines)) {
            cask.Redirect("/api/p2m2tools/parsemetabolomicsfile/gcms")
        } else if (isOpenLabCDS(lines)) {
            cask.Redirect("/api/p2m2tools/parsemetabolomicsfile/openlabcds")
        }
        else {
            println("HERROR................")
            cask.Abort(401);
        }
    }

    /**
     * Get Generic format of Metabolomics File GCSM format
     * @param request
     * @return
     */

    @cask.post("/api/p2m2tools/parsemetabolomicsfile/gcms")
    def gcms(request: cask.Request) = {
        Try(GCMSParser.get(filename="*p2m2tools/gcms*",toParse=request.text().split("\n").toList)) match {
            case Success(obj : MassSpectrometryResultSet) => println("OK gcms");obj.toGenericP2M2.toString()
            case Failure(e) => System.err.println(e.toString());cask.Abort(401);""
        }
    }

    /**
     * Get Generic format of Metabolomics File OpenLabCDS format
     *
     * @param request
     * @return
     */

    @cask.post("/api/p2m2tools/parsemetabolomicsfile/openlabcds")
    def openlabcds(request: cask.Request) = {
        Try(OpenLabCDSParser.get(filename = "*p2m2tools/openlabcds*", toParse = request.text().split("\n").toList)) match {
            case Success(obj: MassSpectrometryResultSet) => println("OK openlabcds");obj.toGenericP2M2.toString()
            case Failure(e) => System.err.println(e.toString()); cask.Abort(401); ""
        }
    }

    initialize()

}