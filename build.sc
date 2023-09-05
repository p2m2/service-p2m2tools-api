import mill._, scalalib._
import scala.sys.process._
import java.io.File
import $ivy.`com.lihaoyi::mill-contrib-scoverage:0.11.2`
import mill.contrib.scoverage.ScoverageModule

object app extends ScalaModule with ScoverageModule {
    def scalaVersion = "2.13.11"
    def version_build : String = "1.0.0"
    def scoverageVersion = "2.0.10"
    def ivyDeps = Agg(
        ivy"com.lihaoyi::cask:0.9.1",
        ivy"com.github.p2m2::p2m2tools:0.2.2",
        ivy"org.apache.logging.log4j:log4j-to-slf4j:2.20.0",
        ivy"org.slf4j:slf4j-api:2.0.7",
        ivy"org.slf4j:slf4j-simple:2.0.7"
    )

    /*
        ./mill app.scoverage.compile
        ./mill app.test
        ./mill app.scoverage.htmlReport
    */

    object test extends ScoverageTests with ScalaTests {
        def testFramework = "utest.runner.Framework"

        def ivyDeps = Agg(
        ivy"com.lihaoyi::utest::0.8.1",
        ivy"com.lihaoyi::requests::0.8.0"
        ) 
    }

    
}
