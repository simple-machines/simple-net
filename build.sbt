name := "simple-net"

organization := "au.com.simplemachines"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.2" % "test",
  "org.specs2" %% "specs2-mock" % "3.6.2" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions ++= Seq(
  "-deprecation",
  "-Xfatal-warnings",
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