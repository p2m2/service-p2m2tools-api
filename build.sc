import mill._, scalalib._
import scala.sys.process._
import java.io.File

object app extends ScalaModule {
    def scalaVersion = "2.13.12"
    def version_build : String = "1.0.3"
    def ivyDeps = Agg(
        ivy"com.lihaoyi::cask:0.9.2",
        ivy"com.github.p2m2::p2m2tools:0.2.3c",
        ivy"org.apache.logging.log4j:log4j-to-slf4j:2.22.1",
        ivy"org.slf4j:slf4j-api:2.0.11",
        ivy"org.slf4j:slf4j-simple:2.0.11"
    )

    object test extends ScalaTests {
        def testFramework = "utest.runner.Framework"

        def ivyDeps = Agg(
        ivy"com.lihaoyi::utest::0.8.2",
        ivy"com.lihaoyi::requests::0.8.0",
        )
    }
}
