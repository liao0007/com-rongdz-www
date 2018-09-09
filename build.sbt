name := "com.rongdz.www"

organization := "com.rongdz"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

updateOptions := updateOptions.value.withCachedResolution(true)

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  TwirlKeys.templateImports += "com.github.aselab.activerecord.views.dsl._"
)

routesGenerator := InjectedRoutesGenerator

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("scalaz", "releases"),

  //for Silhouette
  "Atlassian Releases" at "https://maven.atlassian.com/public/",

  Resolver.jcenterRepo,
  "sedis-fix" at "https://dl.bintray.com/graingert/maven/" //for redis
)

libraryDependencies ++= Seq(
  guice,
  ws,
  filters,
  specs2,
  cacheApi,

  // "org.specs2" %% "specs2-matcher-extra" % "3.6.6",

  // -- coomon libs --
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  jdbc,
  "mysql" % "mysql-connector-java" % "8.0.12",

  // -- database --
  jdbc,
  "com.github.aselab" %% "scala-activerecord" % "0.4.0",
  "com.github.aselab" %% "scala-activerecord-play2" % "0.4.0",
  // "com.typesafe.play" %% "anorm" % "2.5.0",

  // -- weixin api --
  "com.github.binarywang" % "weixin-java-mp" % "2.9.0",
  "com.github.binarywang" % "weixin-java-pay" % "2.9.0",
  "com.github.binarywang" % "weixin-java-open" % "2.9.0",

  // -- auth --
  "com.mohiva" %% "play-silhouette" % "5.0.5",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.5",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.5",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.5",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.5" % "test",

  //redis cache
  "com.github.karelcemus" %% "play-redis" % "2.2.0",

  // -- Bootstrap --
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",

  // -- html compressor --
  "com.mohiva" %% "play-html-compressor" % "0.7.1",
)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node