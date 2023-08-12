package trace4cats.opentelemetry.jaeger

import cats.effect.kernel.{Async, Resource}
import fs2.Chunk
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import trace4cats.kernel.SpanCompleter
import trace4cats.model.TraceProcess
import trace4cats.{CompleterConfig, QueuedSpanCompleter}

object OpenTelemetryJaegerSpanCompleter {
  def apply[F[_]: Async](
    process: TraceProcess,
    host: String = "localhost",
    port: Int = 14250,
    config: CompleterConfig = CompleterConfig(),
    protocol: String = "http"
  ): Resource[F, SpanCompleter[F]] =
    Resource.eval(Slf4jLogger.create[F]).flatMap { implicit logger: Logger[F] =>
      OpenTelemetryJaegerSpanExporter[F, Chunk](host, port, protocol, process.attributes)
        .flatMap(QueuedSpanCompleter[F](TraceProcess(process.serviceName), _, config))
    }
}
