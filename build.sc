import mill._, scalalib._
import scala.sys.process._
import java.io.File

object app extends ScalaModule {
    def scalaVersion = "2.13.11"
    def version_build : String = "1.0.0"
    def ivyDeps = Agg(
        ivy"com.lihaoyi::cask:0.9.1",
        ivy"com.github.p2m2::p2m2tools:0.2.2",
        ivy"org.apache.logging.log4j:log4j-to-slf4j:2.20.0",
        ivy"org.slf4j:slf4j-api:2.0.7",
        ivy"org.slf4j:slf4j-simple:2.0.7"
    )

    object test extends ScalaTests {
        def testFramework = "utest.runner.Framework"

        def ivyDeps = Agg(
        ivy"com.lihaoyi::utest::0.8.1",
        ivy"com.lihaoyi::requests::0.8.0",
        )
    }
}
