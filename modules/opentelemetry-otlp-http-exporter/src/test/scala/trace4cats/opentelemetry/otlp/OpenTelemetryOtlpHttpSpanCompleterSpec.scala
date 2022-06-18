package trace4cats.opentelemetry.otlp

import java.time.Instant

import cats.effect.IO
import fs2.Chunk
import org.http4s.blaze.client.BlazeClientBuilder
import trace4cats.model.{Batch, CompletedSpan, TraceProcess, TraceState}
import trace4cats.test.jaeger.BaseJaegerSpec
import trace4cats.{CompleterConfig, SemanticTags}

import scala.concurrent.duration._

class OpenTelemetryOtlpHttpSpanCompleterSpec extends BaseJaegerSpec {
  it should "Send a span to jaeger" in forAll { (span: CompletedSpan.Builder, serviceName: String) =>
    val process = TraceProcess(serviceName)

    val updatedSpan = span.copy(
      start = Instant.now(),
      end = Instant.now(),
      attributes = span.attributes.filterNot { case (key, _) =>
        excludedTagKeys.contains(key)
      },
      context = span.context.copy(traceState = TraceState.empty)
    )
    val batch = Batch(Chunk(updatedSpan.build(process)))
    val completer = BlazeClientBuilder[IO].resource.flatMap { client =>
      OpenTelemetryOtlpHttpSpanCompleter[IO](
        client,
        process,
        "localhost",
        4318,
        config = CompleterConfig(batchTimeout = 50.millis)
      )
    }

    testCompleter(
      completer,
      updatedSpan,
      process,
      batchToJaegerResponse(batch, process, SemanticTags.kindTags, statusTags, processTags, additionalTags)
    )
  }
}
