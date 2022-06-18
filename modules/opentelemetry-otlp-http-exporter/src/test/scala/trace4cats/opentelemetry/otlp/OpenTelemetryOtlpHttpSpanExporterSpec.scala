package trace4cats.opentelemetry.otlp

import java.time.Instant

import cats.effect.{IO, Resource}
import fs2.Chunk
import org.http4s.blaze.client.BlazeClientBuilder
import trace4cats.SemanticTags
import trace4cats.model.{Batch, TraceProcess, TraceState}
import trace4cats.test.jaeger.BaseJaegerSpec

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
            end = Instant.now(),
            context = span.context.copy(traceState = TraceState.empty)
          )
        )
      )
    val exporter = BlazeClientBuilder[IO].resource.flatMap { client =>
      Resource.eval(OpenTelemetryOtlpHttpSpanExporter[IO, Chunk](client, "localhost", 4318))
    }

    testExporter(
      exporter,
      updatedBatch,
      batchToJaegerResponse(updatedBatch, process, SemanticTags.kindTags, statusTags, processTags, additionalTags)
    )
  }
}
