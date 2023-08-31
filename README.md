# service-parser-p2m2tools

Service to parse the formats of the metabolomics data acquisition devices of the P2M2 platform

./mill app.test
./mill app --help
./mill app.assembly

java -jar ./out/app/assembly.dest/out.jar

./mill -w app.runBackground

curl -X POST --data hello http://localhost:8080/parse
curl -d @path/to/data.json https://reqbin.com/echo/post/json

## devel / memo


`./mill -j 0 mill.idea.GenIdea/idea`