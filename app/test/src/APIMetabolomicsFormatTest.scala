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

    test("sniffer unknown format") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/sniffer", data = "blablabla...")
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()) == ujson.Obj("format" -> "unknown"))
    }

    test("gcms") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/gcms", data = MetabolomicsData.gcms)
        println(response.text())
        assert(response.statusCode == 200)
    }


    test("gcms/generic") - withServer(APIMetabolomicsFormat){
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/gcms/generic",data=MetabolomicsData.gcms)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 1)
    }

    test("gcms generic parsing") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse", data = MetabolomicsData.gcms)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 1)
    }

    test("openlabcds") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/openlabcds", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
    }

    test("openlabcds/generic") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/openlabcds/generic", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 4)
    }

    test("openlabcds generic parsing") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 4)
    }

    test("masslynx") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/masslynx", data = MetabolomicsData.masslynx)
        assert(response.statusCode == 200)
        println(response.text())
    }

    test("masslynx/generic") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/masslynx/generic", data = MetabolomicsData.masslynx)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 8313)
    }

    test("masslynx generic parsing") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse", data = MetabolomicsData.masslynx)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 8313)
    }

    test("xcalibur") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/xcalibur", data = MetabolomicsData.xcalibur)
        assert(response.statusCode == 200)
        println(response.text())
    }

    test("xcalibur/generic") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse/xcalibur/generic", data = MetabolomicsData.xcalibur)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 468)
    }

    test("xcalibur generic parsing") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/p2m2tools/api/format/parse", data = MetabolomicsData.xcalibur)
        assert(response.statusCode == 200)
        assert(ujson.read(response.text()).arr.length == 468)
    }

    test("Empty close connexion")
  }

}