name := "stock-ticker"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard"
)

libraryDependencies ++= {
  val http4sVersion = "0.15.3a"
  Seq(
    "org.typelevel" %% "cats"                        % "0.9.0",
    "org.http4s"    %% "http4s-client"               % http4sVersion,
    "com.itv"       %% "scalapact-scalatest"         % "2.1.2" % Test,
    "org.scalatest" %% "scalatest"                   % "3.0.1" % "test,it",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test,
    "org.http4s"    %% "http4s-blaze-client"         % http4sVersion % "test,it"
  )
}

configs(IntegrationTest)
Defaults.itSettings
