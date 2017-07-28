name := "simple-net"

organization := "au.com.simplemachines"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.12.2")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.8.9" % "test",
  "org.specs2" %% "specs2-mock" % "3.8.9" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

publishMavenStyle := true

publishTo := Some {
  "simplemachines repo" at {
    "http://nexus.simplemachines.com.au/content/repositories/" + {
      if (version.value.trim.endsWith("SNAPSHOT")) "snapshots/" else "releases/"
    }
  }
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")