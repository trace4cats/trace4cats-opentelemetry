package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.effect.{IO, Resource}
import fs2.Chunk
import io.janstenpickle.trace4cats.`export`.SemanticTags
import io.janstenpickle.trace4cats.model.{Batch, TraceProcess}
import io.janstenpickle.trace4cats.test.jaeger.BaseJaegerSpec
import org.http4s.blaze.client.BlazeClientBuilder

import java.time.Instant
import scala.concurrent.ExecutionContext.global

class OpenTelemetryOtlpHttpSpanExporterSpec extends BaseJaegerSpec {
  it should "Send a batch of spans to jaeger" in forAll { (batch: Batch[Chunk], process: TraceProcess) =>
    val updatedBatch =
      Batch(
        batch.spans.map(span =>
          span.copy(
            serviceName = process.serviceName,
            attributes = (process.attributes ++ span.attributes)
              .filterNot { case (key, _) =>
                excludedTagKeys.contains(key)
              },
            start = Instant.now(),
            end = Instant.now()
          )
        )
      )
    val exporter = BlazeClientBuilder[IO](global).resource.flatMap { client =>
      Resource.eval(OpenTelemetryOtlpHttpSpanExporter[IO, Chunk](client, "localhost", 4318))
    }

    testExporter(
      exporter,
      updatedBatch,
      batchToJaegerResponse(updatedBatch, process, SemanticTags.kindTags, statusTags, processTags, additionalTags)
    )
  }
}
