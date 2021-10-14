package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.{Async, Resource}
import io.grpc.Metadata
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.opentelemetry.common.OpenTelemetryGrpcSpanExporter
import io.janstenpickle.trace4cats.opentelemetry.common.model.GrpcTransportType
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter

object OpenTelemetryOtlpGrpcSpanExporter {
  def apply[F[_]: Async, G[_]: Foldable](
    host: String = "localhost",
    port: Int = 4317,
    transportType: GrpcTransportType = GrpcTransportType.Default,
    staticHeaders: Metadata = new Metadata(),
  ): Resource[F, SpanExporter[F, G]] =
    OpenTelemetryGrpcSpanExporter(
      host = host,
      port = port,
      makeExporter = channel => OtlpGrpcSpanExporter.builder().setChannel(channel).build(),
      transportType = transportType,
      staticHeaders = staticHeaders,
    )
}
