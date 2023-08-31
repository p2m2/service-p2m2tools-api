package fr.inrae.metabolomics.p2m2.api

import utest.{TestSuite, Tests, test}
import io.undertow.Undertow
import java.nio.file.Files

import scala.concurrent.Future

object APIMetabolomicsFormatTest extends TestSuite {

  def withServer[T](example: cask.main.Main)(f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8081, "localhost")
      .setHandler(example.defaultHandler)
      .build
    server.start()
    val res =
      try f("http://localhost:8081")
      finally server.stop()
    res
  }

  def tests: Tests = Tests {

    test("sniffer gcms") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/sniffer", data = MetabolomicsData.gcms)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()) == ujson.Obj("format" -> "gcms"))
    }

    test("sniffer openlabcds") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/sniffer", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()) == ujson.Obj("format" -> "openlabcds"))
    }

    test("sniffer xcalibur") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/sniffer", data = MetabolomicsData.xcalibur)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()) == ujson.Obj("format" -> "xcalibur"))
    }


    test("gcms") - withServer(APIMetabolomicsFormat){
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/gcms",data=MetabolomicsData.gcms)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 1)
    }

    test("openlabcds") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/openlabcds", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 4)
    }

    test("masslynx") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/masslynx", data = MetabolomicsData.masslynx)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 8313)
    }
    test("xcalibur") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/xcalibur", data = MetabolomicsData.xcalibur)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 468)
    }
    test("Empty close connexion")
  }

}