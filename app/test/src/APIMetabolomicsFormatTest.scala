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

    test("gcms") - withServer(APIMetabolomicsFormat){
      host =>
        val response = requests.post(s"$host/api/p2m2tools/parsemetabolomicsfile",data=MetabolomicsData.gcms)
        assert(response.statusCode == 200)
        println(response.statusCode,response.text())
    }

    test("openlabcds") - withServer(APIMetabolomicsFormat) {
      host =>
        val response = requests.post(s"$host/api/p2m2tools/parsemetabolomicsfile", data = MetabolomicsData.openlabcds)
        assert(response.statusCode == 200)
        println(response.statusCode, response.text())
    }

    test("test") - withServer(APIMetabolomicsFormat) {
      host =>
        //getClass.getResource("/xcalibur.xls").getPath)
        val path="/media/ofilangi/hdd-local/workspace/INRAE/P2M2/service-parser-p2m2tools/app/test/resources/xcalibur.xls"
        val file: java.nio.file.Path = java.nio.file.Paths.get(path)
        val byteArray = Files.readAllBytes(file)
        println(byteArray)
        val response = requests.post(s"$host/api/p2m2tools/parsemetabolomicsfile", data = byteArray) //java.nio.file.Paths.get(path)
        assert(response.statusCode == 200)
        println(response.statusCode, response.text())
    }

    test("Empty close connexion")
  }

}