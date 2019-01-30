name := "concurrency-examples"
version := "1.0"
scalaVersion := "2.11.1"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.scala-lang.modules" %% "scala-async" % "0.9.1",
  "org.scalaz" %% "scalaz-concurrent" % "7.0.6",
  "com.storm-enroute" %% "scalameter-core" % "0.6"
)

// Run in the same jvm as sbt
fork := false

