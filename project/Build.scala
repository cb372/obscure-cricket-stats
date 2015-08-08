import sbt._
import Keys._
import play.sbt._
import play.sbt.Play.autoImport._
import play.sbt.routes.RoutesKeys._
import com.typesafe.sbt.SbtScalariform._

object ObscureCricketStats extends Build {

  lazy val project = Project(id = "obscure-cricket-stats", base = file("."))
    .enablePlugins(PlayScala)
    .settings(scalariformSettings)
    .settings(
      scalaVersion := "2.11.7",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        ws,
        "org.jsoup" % "jsoup" % "1.8.3",
        "org.scalactic" %% "scalactic" % "2.2.5",
        "org.twitter4j" % "twitter4j-core" % "4.0.4",
        "org.scalatest" %% "scalatest" % "2.2.5" % "test"
      ),
      routesGenerator := InjectedRoutesGenerator
    )

}
