name := "com.rongdz.www"

organization := "com.rongdz"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  TwirlKeys.templateImports += "com.github.aselab.activerecord.views.dsl._"
)

routesGenerator := InjectedRoutesGenerator

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("scalaz", "releases"),
  "Atlassian Releases" at "https://maven.atlassian.com/public/",
  Resolver.jcenterRepo,
  "sedis-fix" at "https://dl.bintray.com/graingert/maven/" //for redis
)

libraryDependencies ++= Seq(
  cache,
  ws,
  filters,
  specs2,
  "org.specs2" %% "specs2-matcher-extra" % "3.6.6",

  // --u8openapi dependency --
  "log4j" % "log4j" % "1.2.17",
  "commons-lang" % "commons-lang" % "2.6",

  // -- database --
  jdbc,
  "com.github.aselab" %% "scala-activerecord" % "0.4.0-SNAPSHOT",
  "com.github.aselab" %% "scala-activerecord-play2" % "0.4.0-SNAPSHOT",
  "mysql" % "mysql-connector-java" % "5.1.40",
  "com.typesafe.play" %% "anorm" % "2.5.0",

  // -- weixin api --
  "com.github.binarywang" % "weixin-java-mp" % "2.4.0",

  // -- auth --
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",

  // -- Excel Processor --
  "com.norbitltd" % "spoiwo_2.11" % "1.1.1",

  // -- Bootstrap --
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",

  // -- Redis --
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.5.0",

  // -- html compressor --
  "com.mohiva" %% "play-html-compressor" % "0.6.3",

  // -- configuration parser --
  "com.iheart" %% "ficus" % "1.4.0"

  // -- WEBJAR --

  // -- util --
  // "com.typesafe.play" %% "play-mailer" % "3.0.1",

  //pdf printer
  // "io.github.cloudify" %% "spdf" % "1.3.1"
)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node