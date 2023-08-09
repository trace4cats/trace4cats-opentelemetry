package trace4cats.opentelemetry.otlp.proto

import cats.Foldable
import cats.syntax.all._
import com.google.protobuf.ByteString
import io.opentelemetry.proto.collector.trace.v1.service.ExportTraceServiceRequest
import io.opentelemetry.proto.common.v1.common.{AnyValue, InstrumentationScope, KeyValue}
import io.opentelemetry.proto.resource.v1.resource.Resource
import io.opentelemetry.proto.trace.v1.trace.{ResourceSpans, ScopeSpans, Span, Status}
import trace4cats.model.SpanStatus.Internal
import trace4cats.model.{AttributeValue, Batch, CompletedSpan, SpanKind}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.collection.mutable.{ListBuffer, Map => MutMap}

object ExportSpansRequest {
  def from[G[_]: Foldable](batch: Batch[G]): ExportTraceServiceRequest = {
    def toAttributes(attributes: Map[String, AttributeValue]): List[KeyValue] =
      attributes.view.map { case (k, v) =>
        KeyValue(
          k,
          v match {
            case AttributeValue.StringValue(v) => AnyValue(AnyValue.Value.StringValue(v.value)).some
            case AttributeValue.BooleanValue(v) => AnyValue(AnyValue.Value.BoolValue(v.value)).some
            case AttributeValue.DoubleValue(v) => AnyValue(AnyValue.Value.DoubleValue(v.value)).some
            case AttributeValue.LongValue(v) => AnyValue(AnyValue.Value.IntValue(v.value)).some
            case v: AttributeValue.AttributeList => AnyValue(AnyValue.Value.StringValue(v.show)).some
          }
        )
      }.toList

    def toSpan(span: CompletedSpan): Span =
      Span(
        traceId = ByteString.copyFrom(span.context.traceId.value),
        spanId = ByteString.copyFrom(span.context.spanId.value),
        traceState = span.context.traceState.values.map { case (k, v) => s"$k=$v" }.mkString(","),
        parentSpanId = ByteString.copyFrom(span.context.parent.fold(Array.emptyByteArray)(_.spanId.value)),
        name = span.name,
        kind = span.kind match {
          case SpanKind.Internal => Span.SpanKind.SPAN_KIND_INTERNAL
          case SpanKind.Client => Span.SpanKind.SPAN_KIND_CLIENT
          case SpanKind.Server => Span.SpanKind.SPAN_KIND_SERVER
          case SpanKind.Producer => Span.SpanKind.SPAN_KIND_PRODUCER
          case SpanKind.Consumer => Span.SpanKind.SPAN_KIND_CONSUMER
        },
        startTimeUnixNano = ChronoUnit.NANOS.between(Instant.EPOCH, span.start),
        endTimeUnixNano = ChronoUnit.NANOS.between(Instant.EPOCH, span.end),
        attributes = toAttributes(span.allAttributes),
        status = Status
          .of(
            code = span.status match {
              case s if s.isOk => Status.StatusCode.STATUS_CODE_OK
              case _ => Status.StatusCode.STATUS_CODE_ERROR
            },
            message = span.status match {
              case Internal(message) => message
              case _ => ""
            }
          )
          .some,
        links = span.links
          .fold(List.empty[Span.Link])(_.toList.map { link =>
            Span
              .Link(traceId = ByteString.copyFrom(link.traceId.value), spanId = ByteString.copyFrom(link.spanId.value))
          })
      )

    ExportTraceServiceRequest(
      batch.spans
        .foldLeft(MutMap.empty[String, ListBuffer[Span]]) { (acc, span) =>
          acc.getOrElseUpdate(span.serviceName, ListBuffer.empty) += toSpan(span); acc
        }
        .view
        .map { case (service, spans) =>
          ResourceSpans(
            resource = Resource(attributes =
              List(KeyValue("service.name", AnyValue(AnyValue.Value.StringValue(service)).some))
            ).some,
            scopeSpans = List(ScopeSpans(scope = InstrumentationScope(name = "trace4cats").some, spans = spans.toList))
          )
        }
        .toList
    )
  }

}
