addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.0")

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
   url("http://dl.bintray.com/banno/oss"))(
       Resolver.ivyStylePatterns)

addSbtPlugin("com.banno" % "sbt-license-plugin" % "0.1.0")