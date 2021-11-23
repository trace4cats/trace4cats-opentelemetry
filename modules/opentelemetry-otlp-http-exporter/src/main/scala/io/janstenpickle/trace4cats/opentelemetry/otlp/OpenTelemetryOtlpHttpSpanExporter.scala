package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.Temporal
import cats.syntax.either._
import cats.syntax.functor._
import io.janstenpickle.trace4cats.`export`.HttpSpanExporter
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.model.Batch
import io.janstenpickle.trace4cats.opentelemetry.otlp.json.ResourceSpansBatch
import org.http4s.Uri
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client

object OpenTelemetryOtlpHttpSpanExporter {

  def apply[F[_]: Temporal, G[_]: Foldable](
    client: Client[F],
    host: String = "localhost",
    port: Int = 4318,
    protocol: String = "http"
  ): F[SpanExporter[F, G]] = Uri.fromString(s"$protocol://$host:$port/v1/traces").liftTo[F].map { uri =>
    HttpSpanExporter[F, G, ResourceSpansBatch](client, uri, (batch: Batch[G]) => ResourceSpansBatch.from(batch))
  }

  def apply[F[_]: Temporal, G[_]: Foldable](client: Client[F], uri: Uri): SpanExporter[F, G] =
    HttpSpanExporter[F, G, ResourceSpansBatch](client, uri, (batch: Batch[G]) => ResourceSpansBatch.from(batch))
}
