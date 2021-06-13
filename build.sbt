name := """shop"""
organization := "pl.edu.uj"

version := "1.0-SNAPSHOT"

resolvers += Resolver.jcenterRepo

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(ehcache, ws, specs2 % Test, guice)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.34.0"
)

libraryDependencies ++= Seq(
  "com.iheart" %% "ficus" % "1.4.7",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-totp" % "7.0.0",
  "net.codingwell" %% "scala-guice" % "5.0.1"
)

libraryDependencies += ehcache

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/",
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases",
  "Atlassian's Maven Public Repository" at "https://packages.atlassian.com/maven-public/"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "pl.edu.uj.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "pl.edu.uj.binders._"
