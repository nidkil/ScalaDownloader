name := "ScalaDownloader"

organization := "nidkil.com"

version := "0.5.0"

scalaVersion := "2.11.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
 
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.3.5"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"