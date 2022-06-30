package trace4cats.opentelemetry.otlp

import cats.effect.kernel.{Async, Resource}
import fs2.Chunk
import org.http4s.client.Client
import org.http4s.{Header, Uri}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import trace4cats.kernel.SpanCompleter
import trace4cats.model.TraceProcess
import trace4cats.{CompleterConfig, QueuedSpanCompleter}

object OpenTelemetryOtlpHttpSpanCompleter {

  def apply[F[_]: Async](
    client: Client[F],
    process: TraceProcess,
    host: String = "localhost",
    port: Int = 4318,
    config: CompleterConfig = CompleterConfig(),
    protocol: String = "http",
    staticHeaders: List[Header.ToRaw] = List.empty
  ): Resource[F, SpanCompleter[F]] =
    Resource.eval(Slf4jLogger.create[F]).flatMap { implicit logger: Logger[F] =>
      Resource
        .eval(OpenTelemetryOtlpHttpSpanExporter[F, Chunk](client, host, port, protocol, staticHeaders))
        .flatMap(QueuedSpanCompleter[F](process, _, config))
    }

  def fromUri[F[_]: Async](
    client: Client[F],
    process: TraceProcess,
    uri: Uri,
    config: CompleterConfig,
    staticHeaders: List[Header.ToRaw] = List.empty
  ): Resource[F, SpanCompleter[F]] =
    Resource.eval(Slf4jLogger.create[F]).flatMap { implicit logger: Logger[F] =>
      QueuedSpanCompleter[F](
        process,
        OpenTelemetryOtlpHttpSpanExporter.fromUri[F, Chunk](client, uri, staticHeaders),
        config
      )
    }

  @deprecated("Use fromUri", since = "0.13.1")
  def apply[F[_]: Async](
    client: Client[F],
    process: TraceProcess,
    uri: Uri,
    config: CompleterConfig
  ): Resource[F, SpanCompleter[F]] =
    fromUri[F](client, process, uri, config)
}
