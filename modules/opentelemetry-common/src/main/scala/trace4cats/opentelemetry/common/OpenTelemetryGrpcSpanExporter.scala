package trace4cats.opentelemetry.common

import cats.Foldable
import cats.effect.kernel.{Async, Resource, Sync}
import cats.syntax.flatMap._
import cats.syntax.foldable._
import cats.syntax.functor._
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.trace.`export`.{SpanExporter => OTSpanExporter}
import io.opentelemetry.sdk.trace.data.SpanData
import trace4cats.kernel.SpanExporter
import trace4cats.model.{Batch, CompletedSpan}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object OpenTelemetryGrpcSpanExporter {
  final case class ShutdownFailure(endpoint: Endpoint) extends RuntimeException {
    override def getMessage: String = s"Failed to shutdown OpenTelemetry span exporter for ${endpoint.render}"
  }
  final case class ExportFailure(endpoint: Endpoint) extends RuntimeException {
    override def getMessage: String = s"Failed to export OpenTelemetry span batch to ${endpoint.render}"
  }

  def apply[F[_]: Async, G[_]: Foldable](
    endpoint: Endpoint,
    makeExporter: Endpoint => OTSpanExporter,
  ): Resource[F, SpanExporter[F, G]] = {
    def liftCompletableResultCode(fa: F[CompletableResultCode])(onFailure: => Throwable): F[Unit] =
      fa.flatMap { result =>
        Async[F].async_[Unit] { cb =>
          val _ = result.whenComplete { () =>
            if (result.isSuccess) cb(Right(()))
            else cb(Left(onFailure))
          }
        }
      }

    def write(exporter: OTSpanExporter, spans: G[CompletedSpan]): F[Unit] =
      for {
        spans <- Sync[F].delay(
          spans
            .foldLeft(ListBuffer.empty[SpanData]) { (buf, span) =>
              buf += Trace4CatsSpanData(Trace4CatsResource(span.serviceName), span)
            }
            .asJavaCollection
        )
        _ <- liftCompletableResultCode(Sync[F].delay(exporter.`export`(spans)))(ExportFailure(endpoint))
      } yield ()

    Resource
      .make(Sync[F].delay(makeExporter(endpoint)))(exporter =>
        liftCompletableResultCode(Sync[F].delay(exporter.shutdown()))(ShutdownFailure(endpoint))
      )
      .map(exporter => (batch: Batch[G]) => write(exporter, batch.spans))
  }
}
