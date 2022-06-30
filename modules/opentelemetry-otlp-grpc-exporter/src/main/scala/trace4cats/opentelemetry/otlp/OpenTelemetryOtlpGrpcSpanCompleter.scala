package trace4cats.opentelemetry.otlp

import cats.effect.kernel.{Async, Resource}
import fs2.Chunk
import org.typelevel.ci.CIString
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import trace4cats.kernel.SpanCompleter
import trace4cats.model.TraceProcess
import trace4cats.{CompleterConfig, QueuedSpanCompleter}

object OpenTelemetryOtlpGrpcSpanCompleter {
  def apply[F[_]: Async](
    process: TraceProcess,
    host: String = "localhost",
    port: Int = 4317,
    config: CompleterConfig = CompleterConfig(),
    protocol: String = "http",
    staticHeaders: List[(CIString, String)] = List.empty
  ): Resource[F, SpanCompleter[F]] =
    Resource.eval(Slf4jLogger.create[F]).flatMap { implicit logger: Logger[F] =>
      OpenTelemetryOtlpGrpcSpanExporter[F, Chunk](host, port, protocol, staticHeaders)
        .flatMap(QueuedSpanCompleter[F](process, _, config))
    }
}
