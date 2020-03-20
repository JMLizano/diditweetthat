name := """diditweetthat"""
organization := "com.jmlizano"
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
//lazy val root = (project in file("."))

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += guice
libraryDependencies ++= Seq(
    "com.danielasfregola" %% "twitter4s" % "6.2",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
)
// Avoid dependency collision between Play and twitter4s
dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.11"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.jmlizano.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.jmlizano.binders._"
