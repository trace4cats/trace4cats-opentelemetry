import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.16"
    val scala213 = "2.13.8"
    val scala3 = "3.1.3"

    val trace4cats = "0.14.0"
    val trace4catsExporterHttp = "0.14.0"
    val trace4catsJaegerIntegrationTest = "0.14.0"

    val autoValue = "1.9"
    val circe = "0.14.2"
    val collectionCompat = "2.8.1"
    val grpc = "1.48.1"
    val http4s = "0.23.16"
    val openTelemetry = "1.16.0"

    val kindProjector = "0.13.2"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsCore = "io.janstenpickle"         %% "trace4cats-core"          % Versions.trace4cats
  lazy val trace4catsExporterHttp = "io.janstenpickle" %% "trace4cats-exporter-http" % Versions.trace4catsExporterHttp
  lazy val trace4catsJaegerIntegrationTest =
    "io.janstenpickle" %% "trace4cats-jaeger-integration-test" % Versions.trace4catsJaegerIntegrationTest

  lazy val autoValueAnnotation = "com.google.auto.value"  % "auto-value-annotations"      % Versions.autoValue
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
