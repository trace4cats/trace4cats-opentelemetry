package io.janstenpickle.trace4cats.opentelemetry.otlp

import java.time.Instant

import cats.effect.IO
import fs2.Chunk
import io.janstenpickle.trace4cats.`export`.SemanticTags
import io.janstenpickle.trace4cats.model.{Batch, TraceProcess}
import io.janstenpickle.trace4cats.test.jaeger.BaseJaegerSpec

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

    testExporter(
      OpenTelemetryOtlpHttpSpanExporter.blazeClient[IO, Chunk]("localhost", 4318),
      updatedBatch,
      batchToJaegerResponse(updatedBatch, process, SemanticTags.kindTags, statusTags, processTags, additionalTags)
    )
  }
}
