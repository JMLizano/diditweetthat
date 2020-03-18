name := """diditweetthat"""
organization := "com.jmlizano"

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)
lazy val root = (project in file("."))
//conflictManager := ConflictManager.latestRevision

scalaVersion := "2.13.1"

resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies ++= Seq(
    "com.danielasfregola" %% "twitter4s" % "6.2",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
)

//dependencyOverrides += "com.typesafe.akka" %% "akka-actor" % "2.5.26"
//dependencyOverrides += "com.typesafe.akka" %% "akka-stream" % "2.5.26"
//dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.10"
//dependencyOverrides += "com.typesafe.akka" %% "akka-parsing" % "10.1.10"
//dependencyOverrides += "com.typesafe.akka" %% "akka-http-core" % "10.1.10"
//dependencyOverrides += "com.typesafe.akka" %% "akka-http-json4s" % "1.31.0"
//dependencyOverrides += "com.typesafe" % "config" %"1.3.3"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.jmlizano.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.jmlizano.binders._"
