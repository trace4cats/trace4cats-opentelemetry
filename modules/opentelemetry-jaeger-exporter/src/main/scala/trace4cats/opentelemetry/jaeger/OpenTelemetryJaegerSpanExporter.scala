package trace4cats.opentelemetry.jaeger

import cats.Foldable
import cats.effect.kernel.{Async, Resource}
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter
import trace4cats.AttributeValue
import trace4cats.kernel.SpanExporter
import trace4cats.opentelemetry.common.{Endpoint, OpenTelemetryGrpcSpanExporter}

object OpenTelemetryJaegerSpanExporter {
  def apply[F[_]: Async, G[_]: Foldable](
    host: String = "localhost",
    port: Int = 14250,
    protocol: String = "http",
    resourceAttributes: Map[String, AttributeValue] = Map.empty
  ): Resource[F, SpanExporter[F, G]] =
    OpenTelemetryGrpcSpanExporter(
      Endpoint(protocol, host, port),
      resourceAttributes,
      ep => JaegerGrpcSpanExporter.builder().setEndpoint(ep.render).build()
    )
}
