import sbt._

class PrecompilerProject(info: ProjectInfo) extends DefaultProject(info) {

  val ScalateVersion = "1.4.1"
  val scalate_core = "org.fusesource.scalate"  % "scalate-core"  % ScalateVersion
  val scalate_util = "org.fusesource.scalate"  % "scalate-util"  % ScalateVersion
  val slf4j        = "org.slf4j"               % "slf4j-jdk14"   % "1.6.1"

  override def mainClass = Some("pragmagica.scalate.Precompiler")
  override def managedStyle = ManagedStyle.Maven
  val publishTo = Resolver.sftp("repobum", "repobum", "/home/public/%s".format(
    if (projectVersion.value.toString.endsWith("-SNAPSHOT")) "snapshots"
    else "releases"
  )) as("repobum_repobum", new java.io.File(Path.userHome + "/.ssh/repobum"))
}
