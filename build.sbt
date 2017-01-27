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

libraryDependencies ++= Seq(
  "org.http4s"    %% "http4s-client"               % "0.15.3a",
  "com.itv"       %% "scalapact-scalatest"         % "2.1.2" % Test,
  "org.scalatest" %% "scalatest"                   % "3.0.1" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test
)
