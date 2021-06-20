import play.sbt.PlayScala

name := "shop"
organization := "pl.edu.uj"
version := "1.1"
maintainer := "patrycja.dembinska@student.uj.edu.pl"

scalaVersion := "2.12.13"

lazy val plugins = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.jcenterRepo
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  ehcache, guice, specs2 % Test, ws,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.30.1",
  "com.iheart" %% "ficus" % "1.4.7",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-totp" % "7.0.0",
  "net.codingwell" %% "scala-guice" % "5.0.1"
)
