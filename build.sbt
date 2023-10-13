ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-sandbox",
    idePackagePrefix := Some("com.vdumitrescu")
  )

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-http" % "3.0.0-RC2+114-e195aa43-SNAPSHOT"
)