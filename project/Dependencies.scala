import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.15"
    val scala213 = "2.13.8"
    val scala3 = "3.1.1"

    val trace4cats = "0.12.0"
    val trace4catsExporterHttp = "0.12.0+41-8ce63144"
    val trace4catsJaegerIntegrationTest = "0.12.0+42-5e4d5380"

    val circe = "0.14.1"
    val collectionCompat = "2.6.0"
    val grpc = "1.44.1"
    val http4s = "0.23.11"
    val openTelemetry = "1.11.0"

    val kindProjector = "0.13.2"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsExporterCommon = "io.janstenpickle" %% "trace4cats-exporter-common" % Versions.trace4cats
  lazy val trace4catsKernel = "io.janstenpickle"         %% "trace4cats-kernel"          % Versions.trace4cats
  lazy val trace4catsModel = "io.janstenpickle"          %% "trace4cats-model"           % Versions.trace4cats
  lazy val trace4catsExporterHttp = "io.janstenpickle" %% "trace4cats-exporter-http" % Versions.trace4catsExporterHttp
  lazy val trace4catsJaegerIntegrationTest =
    "io.janstenpickle" %% "trace4cats-jaeger-integration-test" % Versions.trace4catsJaegerIntegrationTest

  lazy val circeGeneric = "io.circe"                     %% "circe-generic"               % Versions.circe
  lazy val collectionCompat = "org.scala-lang.modules"   %% "scala-collection-compat"     % Versions.collectionCompat
  lazy val grpcApi = "io.grpc"                            % "grpc-api"                    % Versions.grpc
  lazy val grpcOkHttp = "io.grpc"                         % "grpc-okhttp"                 % Versions.grpc
  lazy val grpcStub = "io.grpc"                           % "grpc-stub"                   % Versions.grpc
  lazy val http4sCirce = "org.http4s"                    %% "http4s-circe"                % Versions.http4s
  lazy val openTelemetrySdk = "io.opentelemetry"          % "opentelemetry-sdk"           % Versions.openTelemetry
  lazy val openTelemetryOtlpExporter = "io.opentelemetry" % "opentelemetry-exporter-otlp" % Versions.openTelemetry
  lazy val openTelemetryJaegerExporter = "io.opentelemetry" % "opentelemetry-exporter-jaeger" % Versions.openTelemetry

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
