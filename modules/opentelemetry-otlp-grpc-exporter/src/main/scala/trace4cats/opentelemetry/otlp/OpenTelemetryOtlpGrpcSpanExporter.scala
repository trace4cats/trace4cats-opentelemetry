package trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.{Async, Resource}
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import org.typelevel.ci.CIString
import trace4cats.kernel.SpanExporter
import trace4cats.opentelemetry.common.{Endpoint, OpenTelemetryGrpcSpanExporter}

object OpenTelemetryOtlpGrpcSpanExporter {
  def apply[F[_]: Async, G[_]: Foldable](
    host: String = "localhost",
    port: Int = 4317,
    protocol: String = "http",
    staticHeaders: List[(CIString, String)] = List.empty
  ): Resource[F, SpanExporter[F, G]] =
    OpenTelemetryGrpcSpanExporter(
      endpoint = Endpoint(protocol, host, port),
      makeExporter = endpoint =>
        staticHeaders
          .foldLeft(OtlpGrpcSpanExporter.builder().setEndpoint(endpoint.render)) { case (builder, (key, value)) =>
            builder.addHeader(key.toString, value)
          }
          .build(),
    )
}
