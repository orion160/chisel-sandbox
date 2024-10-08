import mill._, scalalib._

object scala_sandbox extends RootModule with ScalaModule {
  override def scalaVersion = "2.13.14"
  override def scalacOptions = Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit"
  )
  override def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:6.5.0"
  )
  override def scalacPluginIvyDeps = Agg(
    ivy"org.chipsalliance:::chisel-plugin:6.5.0"
  )
  object test extends ScalaTests with TestModule.ScalaTest {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.2.19"
    )
  }
}
