import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.14"
    val scala213 = "2.13.6"
    val scala3 = "3.0.1"

    val trace4cats = "0.12.0-RC2+18-2de6b68e"
    val trace4catsExporterHttp = "0.12.0-RC2+4-69bfcea6"
    val trace4catsJaegerIntegrationTest = "0.12.0-RC2+7-e4062471"

    val circe = "0.14.1"
    val collectionCompat = "2.5.0"
    val grpc = "1.39.0"
    val http4s = "0.23.0-RC1"
    val openTelemetry = "1.4.1"
    val scalapb = "0.12.0"
    val json4sNative = "4.0.3"

    val kindProjector = "0.13.0"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsExporterCommon = "io.janstenpickle" %% "trace4cats-exporter-common" % Versions.trace4cats
  lazy val trace4catsKernel = "io.janstenpickle"         %% "trace4cats-kernel"          % Versions.trace4cats
  lazy val trace4catsModel = "io.janstenpickle"          %% "trace4cats-model"           % Versions.trace4cats
  lazy val trace4catsExporterHttp = "io.janstenpickle"   %% "trace4cats-exporter-http"   % Versions.trace4catsExporterHttp
  lazy val trace4catsJaegerIntegrationTest =
    "io.janstenpickle" %% "trace4cats-jaeger-integration-test" % Versions.trace4catsJaegerIntegrationTest

  lazy val collectionCompat = "org.scala-lang.modules"     %% "scala-collection-compat"       % Versions.collectionCompat
  lazy val grpcApi = "io.grpc"                              % "grpc-api"                      % Versions.grpc
  lazy val grpcOkHttp = "io.grpc"                           % "grpc-okhttp"                   % Versions.grpc
  lazy val http4sBlazeClient = "org.http4s"                %% "http4s-blaze-client"           % Versions.http4s
  lazy val openTelemetrySdk = "io.opentelemetry"            % "opentelemetry-sdk"             % Versions.openTelemetry
  lazy val openTelemetryOtlpExporter = "io.opentelemetry"   % "opentelemetry-exporter-otlp"   % Versions.openTelemetry
  lazy val openTelemetryJaegerExporter = "io.opentelemetry" % "opentelemetry-exporter-jaeger" % Versions.openTelemetry
  lazy val openTelemetryProto = "io.opentelemetry"          % "opentelemetry-proto"           % Versions.openTelemetry.concat("-alpha")
  lazy val scalapbJson = "com.thesamet.scalapb"            %% "scalapb-json4s"                % Versions.scalapb
  lazy val json4sNative = "org.json4s"                     %% "json4s-native"                 % Versions.json4sNative

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
