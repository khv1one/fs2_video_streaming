import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val server = project.in(file("server"))
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .enablePlugins(WebScalaJSBundlerPlugin)
  .settings(options)
  .settings(
    name := "streaming_server",
    version := "0.1",
    libraryDependencies ++= serverJwnDependencies,
    Universal / packageName := "server",
  ).dependsOn(shared.jvm)

lazy val client = project.in(file("client"))
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= clientJwnDependencies,
    Compile / npmDependencies ++= clientNpmDependencies,
    Universal / packageName := "client",
  )
  .dependsOn(shared.js, shared.jvm)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure).in(file("shared"))
  .settings(libraryDependencies ++= sharedJwnDependencies)
  .jsConfigure { project => project.enablePlugins(ScalaJSBundlerPlugin) }

val options = scalacOptions ++= Seq(
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:existentials",
  "-language:postfixOps",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Yrangepos",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding", "utf8",
  "-language:higherKinds"
  //"-Xfatal-warnings"
)

lazy val serverJwnDependencies = Seq(
  logback,
  logCatsSlf4,
  http4sServer,
  http4sEndpointsServer,
  http4sCirce,
  mockitoCore % Test
)

lazy val clientJwnDependencies = Seq()

lazy val sharedJwnDependencies = Seq(
  cats,
  catsEffect,
  fs2,
  circeGeneric,
  circeGenericExtras,
  scalaTest % Test,
)

lazy val clientNpmDependencies = Seq()

onLoad in Global := (
  "project client" ::
    "project server" ::
    (_: State)
  ) compose (onLoad in Global).value