package fr.inrae.metabolomics.p2m2.api

import cask.main.Main

import fr.inrae.metabolomics.p2m2.format.MassSpectrometryResultSetFactory
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, MassSpectrometryResultSet, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, Xcalibur}
import fr.inrae.metabolomics.p2m2.parser.{GCMSParser, OpenLabCDSParser, ParserManager, QuantifySummaryReportMassLynxParser, XcaliburXlsParser}
import fr.inrae.metabolomics.p2m2.stream.ExportData

object APIMetabolomicsFormat extends cask.MainRoutes {
    
    @cask.get("/")
    def hello() = {
        "Hello World!"
    }

    @cask.post("/parse/")
    def parseFile(request: cask.Request) = {
        MassSpectrometryResultSetFactory.build(new String(request.readAllBytes())).toString
    }

    initialize()

}