sbtPlugin := true

organization := "com.novus"

name := "sbt-scalate-precompiler"

version := "0.1.5"

resolvers ++= Seq(
  "Novus Snapshots Repository" at "http://repo.novus.com/snapshots/",
  "Novus Snapshots Releases" at "http://repo.novus.com/releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-jdk14" % "1.6.1",
  "org.fusesource.scalate" %% "scalate-core" % "1.6.1"
)

publishTo <<= (version) { version: String =>
  val sfx =
    if(version.trim.endsWith("SNAPSHOT"))
      "snapshots"
    else
      "releases"
    val nexus = "https://nexus.novus.com:65443/nexus/content/repositories/"
    Some("Novus " + sfx at nexus + sfx + "/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".novus_nexus")
