package trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.Temporal
import cats.syntax.all._
import io.opentelemetry.proto.collector.trace.v1.trace_service.TraceService
import org.http4s.client.Client
import org.http4s.{Header, Headers, Uri}
import trace4cats.kernel.SpanExporter
import trace4cats.model.Batch
import trace4cats.opentelemetry.otlp.proto.ExportSpansRequest
import trace4cats.model.AttributeValue

object OpenTelemetryOtlpHttp4sGrpcSpanExporter {

  /** Construct a OTLP/gRPC exporter derived using http4s-grpc
    *
    * @param client
    *   Must allow HTTP/2 (e.g. withHttp2 on EmberClientBuilder) for gRPC to function
    * @param host
    * @param port
    * @param protocol
    * @param staticHeaders
    * @return
    *   OTLP/gRPC exporter
    */
  def apply[F[_]: Temporal, G[_]: Foldable](
    client: Client[F],
    host: String = "localhost",
    port: Int = 4317,
    protocol: String = "http",
    staticHeaders: List[Header.ToRaw] = List.empty,
    resourceAttributes: Map[String, AttributeValue] = Map.empty
  ): F[SpanExporter[F, G]] =
    Uri
      .fromString(s"$protocol://$host:$port")
      .liftTo[F]
      .map(uri => fromUri(client, uri, staticHeaders, resourceAttributes))

  /** Construct a OTLP/gRPC exporter derived using http4s-grpc
    *
    * @param client
    *   Must allow HTTP/2 (e.g. withHttp2 on EmberClientBuilder) for gRPC to function
    * @param uri
    * @param staticHeaders
    * @return
    *   OTLP/gRPC exporter
    */
  def fromUri[F[_]: Temporal, G[_]: Foldable](
    client: Client[F],
    uri: Uri,
    staticHeaders: List[Header.ToRaw] = List.empty,
    resourceAttributes: Map[String, AttributeValue] = Map.empty
  ): SpanExporter[F, G] = new SpanExporter[F, G] {
    private val service = TraceService.fromClient[F](client, uri)
    def exportBatch(batch: Batch[G]): F[Unit] = service
      .export(ExportSpansRequest.from(batch, resourceAttributes), Headers(staticHeaders))
      .void
  }

}
