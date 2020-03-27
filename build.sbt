name := "diditweetthat"
organization := "com.jmlizano"
maintainer := "jmlizlac@gmail.com"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += guice
libraryDependencies ++= Seq(
    "com.danielasfregola" %% "twitter4s" % "6.2",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
)

// Avoid dependency collision between Play and twitter4s
dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.11"
