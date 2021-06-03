import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.14"
    val scala213 = "2.13.6"

    val trace4cats = "0.12.0-RC1+146-d193db1e"

    val circe = "0.14.1"
    val grpc = "1.38.0"
    val http4s = "0.23.0-RC1"
    val openTelemetry = "1.2.0"
    val scalapb = "0.11.0"
  }

  lazy val trace4catsExporterCommon = "io.janstenpickle" %% "trace4cats-exporter-common" % Versions.trace4cats
  lazy val trace4catsExporterHttp = "io.janstenpickle"   %% "trace4cats-exporter-http"   % Versions.trace4cats
  lazy val trace4catsKernel = "io.janstenpickle"         %% "trace4cats-kernel"          % Versions.trace4cats
  lazy val trace4catsJaegerIntegrationTest =
    "io.janstenpickle"                          %% "trace4cats-jaeger-integration-test" % Versions.trace4cats
  lazy val trace4catsModel = "io.janstenpickle" %% "trace4cats-model"                   % Versions.trace4cats

  lazy val circeGeneric = "io.circe"                       %% "circe-generic-extras"          % Versions.circe
  lazy val grpcApi = "io.grpc"                              % "grpc-api"                      % Versions.grpc
  lazy val grpcOkHttp = "io.grpc"                           % "grpc-okhttp"                   % Versions.grpc
  lazy val http4sBlazeClient = "org.http4s"                %% "http4s-blaze-client"           % Versions.http4s
  lazy val openTelemetrySdk = "io.opentelemetry"            % "opentelemetry-sdk"             % Versions.openTelemetry
  lazy val openTelemetryOtlpExporter = "io.opentelemetry"   % "opentelemetry-exporter-otlp"   % Versions.openTelemetry
  lazy val openTelemetryJaegerExporter = "io.opentelemetry" % "opentelemetry-exporter-jaeger" % Versions.openTelemetry
  lazy val openTelemetryProto = "io.opentelemetry"          % "opentelemetry-proto"           % Versions.openTelemetry.concat("-alpha")
  lazy val scalapbJson = "com.thesamet.scalapb"            %% "scalapb-json4s"                % Versions.scalapb
}
