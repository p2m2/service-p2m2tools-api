package fr.inrae.metabolomics.p2m2.api

import java.nio.file.{Files, Paths}

object MetabolomicsData {
  val gcms: String =
    """
      |[Header]
      |Data File Name	C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd
      |Output Date	23/08/2021
      |Output Time	14:09:36
      |
      |[MS Quantitative Results]
      |ID#	Name	Type	ISTD Group#	Mass	Ret.Time	Start Time	End Time	A/H	Area	Height	Conc.	Mode	Peak#	Std.Ret.Time	Calibration Curve	3rd	2nd	1st	Constant	Ref.Ion Area	Ref.Ion Height	Ref.Ion Set Ratio	Ref.Ion Ratio	Recovery	SI	Ref.Ion1 m/z	Ref.Ion1 Area	Ref.Ion1 Height	Ref.Ion1 Set Ratio	Ref.Ion1 Ratio	Ref.Ion2 m/z	Ref.Ion2 Area	Ref.Ion2 Height	Ref.Ion2 Set Ratio	Ref.Ion2 Ratio	Ref.Ion3 m/z	Ref.Ion3 Area	Ref.Ion3 Height	Ref.Ion3 Set Ratio	Ref.Ion3 Ratio	Ref.Ion4 m/z	Ref.Ion4 Area	Ref.Ion4 Height	Ref.Ion4 Set Ratio	Ref.Ion4 Ratio	Ref.Ion5 m/z	Ref.Ion5 Area	Ref.Ion5 Height	Ref.Ion5 Set Ratio	Ref.Ion5 Ratio	Ret. Index	S/N	Unit	Description	Threshold
      |1	Glyoxylate (1MEOX) (1TMS )m0	Target	1	160.00	6.405	6.393	6.423	1.080	14	13	0.00029	Auto	3	6.400	Default	0	0	0	0	0	0	84.50	0.00	0.00	18	73.00	0	0	84.50	0.00	59.00	142	129	49.59	1014.29	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	1218	4.87	mg/L		0.00000
      |""".stripMargin
  val openlabcds: String =
    """
      |Data File C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\Std 500.D
      |Sample Name: Std 500
      |MPSr-GCFID 10/19/2021 2:39:33 PM SYSTEM
      |
      |=====================================================================
      |Acq. Operator   : SYSTEM                         Seq. Line :   8
      |Sample Operator : SYSTEM
      |Acq. Instrument : MPSr-GCFID                      Location :   6  (F)
      |Injection Date  : 10/11/2021 11:03:47 PM               Inj :   1
      |                                                Inj Volume : 1 µl
      |Acq. Method     : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
      |                  Glucides_prep_12-110.M
      |Last changed    : 2/25/2021 3:02:59 PM by SYSTEM
      |Analysis Method : C:\Chemstation\1\Data\211011_Corentin-Younes 2021-10-11 15-56-48\190314_
      |                  Glucides_retraitement.M
      |Last changed    : 10/19/2021 2:39:30 PM by SYSTEM
      |                  (modified after loading)
      |Additional Info : Peak(s) manually integrated
      |=====================================================================
      |                      Internal Standard Report
      |=====================================================================
      |
      |Sorted By             :      Signal
      |Calib. Data Modified  :      10/15/2021 5:02:32 PM
      |Multiplier            :      1.0000
      |Dilution              :      1.0000
      |Sample Amount:        :    100.00000  [ng/ul]   (not used in calc.)
      |Use Multiplier & Dilution Factor with ISTDs
      |Sample ISTD Information:
      |ISTD  ISTD Amount   Name
      |  #    [ng/ul]
      |----|-------------|-------------------------
      |  1    100.00000   Adonitol
      |
      |
      |Signal 1: FID1 A,
      |
      |RetTime  Type  ISTD    Area     Amt/Area    Amount   Grp   Name
      | [min]         used  [pA*s]      ratio     [ng/ul]
      |-------|------|----|----------|----------|----------|--|------------------
      |  8.335 MF        1   10.97505    5.42183  531.23458    Glyoxylate
      |  9.925 FM        1   19.37573    3.36334  581.78554    Glycolate
      | 13.462 BB        1   22.58038    2.65118  534.44585    Succinate
      | 13.765 BB        1   27.60959    2.21601  546.21713    Glycerate
      |Totals without ISTD(s) :                  1.95262e4
      |
      |1 Warnings or Errors :
      |
      |Warning : Calibration warnings (see calibration table listing)
      |
      |=====================================================================
      |                          *** End of Report ***
      |""".stripMargin

  val xcalibur: Array[Byte] = Files.readAllBytes(Paths.get(getClass.getResource("/xcalibur.xls").getPath))

  val masslynx: Array[Byte] = Files.readAllBytes(Paths.get(getClass.getResource("/masslynx.txt").getPath))
}