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

object OpenTelemetryOtlpHttp4sGrpcSpanCompleter {

  /** Construct a OTLP/gRPC completer derived using http4s-grpc
    *
    * @param client
    *   Must allow HTTP/2 (e.g. withHttp2 on EmberClientBuilder) for gRPC to function
    * @param process
    * @param uri
    * @param config
    * @param staticHeaders
    * @return
    *   OTLP/gRPC completer
    */
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
        OpenTelemetryOtlpHttp4sGrpcSpanExporter.fromUri[F, Chunk](client, uri, staticHeaders, process.attributes),
        config
      )
    }
}
