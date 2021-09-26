package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.effect.kernel.{Async, Resource, Temporal}
import cats.syntax.applicative._
import io.janstenpickle.trace4cats.`export`.HttpSpanExporter
import io.janstenpickle.trace4cats.kernel.SpanExporter
import io.janstenpickle.trace4cats.model.Batch
import io.janstenpickle.trace4cats.opentelemetry.otlp.json.ResourceSpansBatch
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.blaze.client.BlazeClientBuilder

import scala.concurrent.ExecutionContext

object OpenTelemetryOtlpHttpSpanExporter {
  def blazeClient[F[_]: Async, G[_]: Foldable](
    host: String = "localhost",
    port: Int = 4318,
    ec: Option[ExecutionContext] = None
  ): Resource[F, SpanExporter[F, G]] = for {
    ec <- Resource.eval(ec.fold(Async[F].executionContext)(_.pure))
    client <- BlazeClientBuilder[F](ec).resource
    exporter <- Resource.eval(apply[F, G](client, host, port))
  } yield exporter

  def apply[F[_]: Temporal, G[_]: Foldable](
    client: Client[F],
    host: String = "localhost",
    port: Int = 4318
  ): F[SpanExporter[F, G]] =
    HttpSpanExporter[F, G, ResourceSpansBatch](
      client,
      s"http://$host:$port/v1/trace",
      (batch: Batch[G]) => ResourceSpansBatch.from(batch)
    )
}
