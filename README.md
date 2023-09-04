# service-p2m2tools-api

![Scala CI](https://github.com/p2m2/service-p2m2tools-api/actions/workflows/scala.yml/badge.svg)

API Service to parse the formats of the metabolomics data acquisition devices of the P2M2 platform

## API

| Path                                           | Description                                                                     | Return values                                            | Return values                                   |
|:-----------------------------------------------|:--------------------------------------------------------------------------------|:---------------------------------------------------------|:------------------------------------------------|
| /p2m2tools/api/format/sniffer                  | returns the file type                                                           | `gcms, openlabcds, masslynx-txt, masslynx-xml, xcalibur` | { format : `value`}                             |
| /p2m2tools/api/format/parse                    | parse a file from a metabolomics analysis by guessing its format                | compound                                                 | [ { `GenerricP2M2HeaderFile`  : `value` } {} ]  |
| /p2m2tools/api/format/parse/gcms               | parse a GCMS metabolomics analysis file                                         | compound list                                            |                                                 |
| /p2m2tools/api/format/parse/gcms/generic       | parse a GCMS metabolomics analysis file. Convert to Generic format P2M2         | header, format, class, compound list                     | [ { `GenerricP2M2HeaderFile`  : `value` } {} ]  |
| /p2m2tools/api/format/parse/openlabcds         | parse a openlab CDS metabolomics analysis file                                  | compound list                                            |                                                 |
| /p2m2tools/api/format/parse/openlabcds/generic | parse a openlab CDS metabolomics analysis file. Convert to Generic format P2M2  | header, format, class, compound list                     | [ { `GenerricP2M2HeaderFile`  : `value` } {} ]  |
| /p2m2tools/api/format/parse/masslynx           | parse a MassLynx metabolomics analysis file (Quantitative Summary File)         | compound list                                            |                                                 |
| /p2m2tools/api/format/parse/masslynx/generic   | parse a MassLynx metabolomics analysis file. Convert to Generic format P2M2     | header, format, class, compound list                     | [ { `GenerricP2M2HeaderFile`  : `value` } {} ]  |
| /p2m2tools/api/format/parse/xcalibur           | parse a Xcalibur metabolomics analysis file                                     | compound list                                            |                                                 |
| /p2m2tools/api/format/parse/xcalibur/generic   | parse a Xcalibur metabolomics analysis file. Convert to Generic format P2M2     | header, format, class, compound list                     | [ { `GenerricP2M2HeaderFile`  : `value` } {} ]  |


To see the possible values of GenerricP2M2HeaderFile :
- sample
- metabolite
- retTime
- area
- height
- injectedVolume
- vial
- acquisitionDate
- exportDate

## prerequisites

``` 
curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.2/0.11.2 > mill && chmod +x mill
```
## run tests

```bash
./mill app.test
```

## run service

```bash
./mill app.assembly
java -jar ./out/app/assembly.dest/out.jar
```

or with mill 
```bash
./mill -w app.runBackground
```

## Curl examples

### With String content

```bash
curl -X POST http://localhost:8080/p2m2tools/api/format/sniffer --data-raw '
[Header]
Data File Name	C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT2.qgd
Output Date	23/08/2021
Output Time	14:09:36

[MS Quantitative Results]
ID#	Name	Type	ISTD Group#	Mass	Ret.Time	Start Time	End Time	A/H	Area	Height	Conc.	Mode	Peak#	Std.Ret.Time	Calibration Curve	3rd	2nd	1st	Constant	Ref.Ion Area	Ref.Ion Height	Ref.Ion Set Ratio	Ref.Ion Ratio	Recovery	SI	Ref.Ion1 m/z	Ref.Ion1 Area	Ref.Ion1 Height	Ref.Ion1 Set Ratio	Ref.Ion1 Ratio	Ref.Ion2 m/z	Ref.Ion2 Area	Ref.Ion2 Height	Ref.Ion2 Set Ratio	Ref.Ion2 Ratio	Ref.Ion3 m/z	Ref.Ion3 Area	Ref.Ion3 Height	Ref.Ion3 Set Ratio	Ref.Ion3 Ratio	Ref.Ion4 m/z	Ref.Ion4 Area	Ref.Ion4 Height	Ref.Ion4 Set Ratio	Ref.Ion4 Ratio	Ref.Ion5 m/z	Ref.Ion5 Area	Ref.Ion5 Height	Ref.Ion5 Set Ratio	Ref.Ion5 Ratio	Ret. Index	S/N	Unit	Description	Threshold
1	Glyoxylate (1MEOX) (1TMS )m0	Target	1	160.00	6.405	6.393	6.423	1.080	14	13	0.00029	Auto	3	6.400	Default	0	0	0	0	0	0	84.50	0.00	0.00	18	73.00	0	0	84.50	0.00	59.00	142	129	49.59	1014.29	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	1218	4.87	mg/L		0.00000
'
```
### With file

```bash
curl -X POST http://localhost:8080/p2m2tools/api/format/sniffer --data-binary @app/test/resources/xcalibur.xls 
```

## Javascript examples

```bash
node example-requests-file.js
```

```javascript
var fs = require('fs');

/*
    Example 1 - Obtain the information contained in the file in Xcalibur format
*/

(async() => {

    var contentFile = fs.readFileSync('app/test/resources/xcalibur.xls')
    var response = await fetch('http://localhost:8080/p2m2tools/api/format/parse/xcalibur', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: contentFile
    })

    const data = await response.json();
    console.log(data);

})();
```

## devel / memo


### setting up IntelliJ IDEA
```bash
./mill -j 0 mill.idea.GenIdea/idea
```
