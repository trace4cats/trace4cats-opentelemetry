package trace4cats.opentelemetry.jaeger

import java.time.Instant

import cats.effect.IO
import fs2.Chunk
import trace4cats.model.{Batch, TraceProcess}
import trace4cats.test.jaeger.BaseJaegerSpec

class OpenTelemetryJaegerSpanExporterSpec extends BaseJaegerSpec {
  it should "Send a batch of spans to jaeger" in forAll { (batch: Batch[Chunk], process: TraceProcess) =>
    val updatedBatch =
      Batch(
        batch.spans.map(span =>
          span.copy(
            serviceName = process.serviceName,
            attributes = span.allAttributes
              .filterNot { case (key, _) =>
                excludedTagKeys.contains(key)
              },
            start = Instant.now(),
            end = Instant.now()
          )
        )
      )

    testExporter(
      OpenTelemetryJaegerSpanExporter[IO, Chunk]("localhost", 14250, "http", process.attributes),
      updatedBatch,
      batchToJaegerResponse(updatedBatch, process, kindTags, statusTags, processTags, additionalTags)
    )
  }
}
