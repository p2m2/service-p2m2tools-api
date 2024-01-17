package fr.inrae.metabolomics.p2m2.issues

import fr.inrae.metabolomics.p2m2.api.APIMetabolomicsFormatTest.withServer
import fr.inrae.metabolomics.p2m2.api.{APIMetabolomicsFormat, MetabolomicsData}
import utest.{TestSuite, Tests, test}

import java.nio.file.{Files, Paths}


object BigNumber_10 extends TestSuite {

  val xcalibur: Array[Byte] = Files.readAllBytes(Paths.get(getClass.getResource("/test_big_number_10.XLS").getPath))

  def tests: Tests = Tests {

    test("sniffer gcms") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse", data = xcalibur)
        assert(response.statusCode == 200)
        println(ujson.read(response.text()))
        val data : ujson.Obj = ujson.read(response.text()).arr

        println("-------")
        println(data.obj("header").arr(0),data.obj("header").arr(1),data.obj("header").arr(3))
        data("samples").arr.indices.filter(i => {
          data("samples").arr(i)(1).str == "EC-PLG" //&&
            // area should be 10004026.9287222
           // (data.value("samples").arr(i)(3).num - 10004026 < 1.0)
        })
          .foreach(
          i => println(data("samples").arr(i)(0),data("samples").arr(i)(1),data("samples").arr(i)(3))
        )
    }
  }
}
