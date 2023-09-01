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

/*
    Example 2 - Convert MassLynx to Generic format P2M2
*/


(async() => {

    var contentFile = fs.readFileSync('app/test/resources/masslynx.txt')
    var response = await fetch('http://localhost:8080/p2m2tools/api/format/parse', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: contentFile
    })

    const data = await response.json();
    console.log(data);

})();
