import com.banno.license.Plugin.LicenseKeys._
import com.banno.license.Licenses._

licenseSettings

license := apache2("Copyright 2014 nidkil")

removeExistingHeaderBlock := true
 
name := "ScalaDownloader"

organization := "nidkil.com"

version := "0.6.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
	"ch.qos.logback" % "logback-classic" % "1.1.2",
	"org.apache.httpcomponents" % "httpclient" % "4.3.5",
	"commons-io" % "commons-io" % "2.4",
	"org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)