name := "green"

version := "20210724"

scalaVersion := "2.13.6"

organization := "se.chimps.green"

publishArtifact in (Compile, packageDoc) := false

libraryDependencies ++= Seq(
	"io.vertx" % "vertx-web" % "4.1.1",
	"org.json4s" %% "json4s-native" % "4.0.1"
)
