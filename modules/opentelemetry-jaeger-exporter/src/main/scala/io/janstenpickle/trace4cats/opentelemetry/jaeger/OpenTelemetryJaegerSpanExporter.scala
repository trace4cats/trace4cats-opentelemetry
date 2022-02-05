package io.janstenpickle.trace4cats.opentelemetry.jaeger

import cats.Foldable
import cats.effect.kernel.{Async, Resource}
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.opentelemetry.common.{Endpoint, OpenTelemetryGrpcSpanExporter}
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter

object OpenTelemetryJaegerSpanExporter {
  def apply[F[_]: Async, G[_]: Foldable](
    host: String = "localhost",
    port: Int = 14250,
    protocol: String = "http"
  ): Resource[F, SpanExporter[F, G]] =
    OpenTelemetryGrpcSpanExporter(
      Endpoint(protocol, host, port),
      ep => JaegerGrpcSpanExporter.builder().setEndpoint(ep.render).build()
    )
}
