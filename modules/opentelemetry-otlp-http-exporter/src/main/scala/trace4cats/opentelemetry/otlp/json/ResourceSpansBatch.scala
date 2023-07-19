package trace4cats.opentelemetry.otlp.json

import java.time.Instant
import java.time.temporal.ChronoUnit

import cats.Foldable
import cats.syntax.foldable._
import cats.syntax.show._
import io.circe.generic.semiauto._
import io.circe.{Encoder, Json}
import org.apache.commons.codec.binary.Hex
import trace4cats.model.SpanStatus.Internal
import trace4cats.model.{AttributeValue, Batch, CompletedSpan, SpanKind}
import trace4cats.opentelemetry.otlp.json.Span.SpanKind._
import trace4cats.opentelemetry.otlp.json.Status.Code._

import scala.collection.mutable.{ListBuffer, Map => MutMap}

object ResourceSpansBatch {
  def from[G[_]: Foldable](batch: Batch[G]): ResourceSpansBatch = {
    def toAttributes(attributes: Map[String, AttributeValue]): List[KeyValue] =
      attributes.view.map { case (k, v) =>
        KeyValue(
          k,
          v match {
            case AttributeValue.StringValue(v) => AnyValue.stringValue(v.value)
            case AttributeValue.BooleanValue(v) => AnyValue.boolValue(v.value)
            case AttributeValue.DoubleValue(v) => AnyValue.doubleValue(v.value)
            case AttributeValue.LongValue(v) => AnyValue.intValue(v.value)
            case v: AttributeValue.AttributeList => AnyValue.stringValue(v.show)
          }
        )
      }.toList

    def toSpan(span: CompletedSpan): Span =
      Span(
        trace_id = span.context.traceId.value,
        span_id = span.context.spanId.value,
        trace_state = span.context.traceState.values.map { case (k, v) => s"$k=$v" }.mkString(","),
        parent_span_id = span.context.parent.fold(Array.emptyByteArray)(_.spanId.value),
        name = span.name,
        kind = span.kind match {
          case SpanKind.Internal => SPAN_KIND_INTERNAL
          case SpanKind.Client => SPAN_KIND_CLIENT
          case SpanKind.Server => SPAN_KIND_SERVER
          case SpanKind.Producer => SPAN_KIND_PRODUCER
          case SpanKind.Consumer => SPAN_KIND_CONSUMER
        },
        start_time_unix_nano = ChronoUnit.NANOS.between(Instant.EPOCH, span.start),
        end_time_unix_nano = ChronoUnit.NANOS.between(Instant.EPOCH, span.end),
        attributes = toAttributes(span.allAttributes),
        status = Status(
          code = span.status match {
            case s if s.isOk => STATUS_CODE_OK
            case _ => STATUS_CODE_ERROR
          },
          message = span.status match {
            case Internal(message) => message
            case _ => ""
          }
        ),
        links = span.links.fold(List.empty[Span.Link])(_.toList.map { link =>
          Span.Link(link.traceId.value, link.spanId.value)
        })
      )

    ResourceSpansBatch(
      batch.spans
        .foldLeft(MutMap.empty[String, ListBuffer[Span]]) { (acc, span) =>
          acc.getOrElseUpdate(span.serviceName, ListBuffer.empty) += toSpan(span); acc
        }
        .view
        .map { case (service, spans) =>
          ResourceSpans(
            resource = Resource(attributes = List(KeyValue("service.name", AnyValue.stringValue(service)))),
            scope_spans = List(ScopeSpans(scope = InstrumentationScope("trace4cats"), spans = spans.toList))
          )
        }
        .toList
    )
  }

  implicit val resourceSpansBatchEncoder: Encoder.AsObject[ResourceSpansBatch] = deriveEncoder
}

case class ResourceSpansBatch(resource_spans: List[ResourceSpans])

case class ResourceSpans(resource: Resource, scope_spans: List[ScopeSpans])
object ResourceSpans {
  implicit val resourceSpansEncoder: Encoder.AsObject[ResourceSpans] = deriveEncoder
}

case class Resource(attributes: List[KeyValue], dropped_attributes_count: Int = 0)
object Resource {
  implicit val resourceEncoder: Encoder.AsObject[Resource] = deriveEncoder
}

case class ScopeSpans(scope: InstrumentationScope, spans: List[Span])
object ScopeSpans {
  implicit val scopeSpansEncoder: Encoder.AsObject[ScopeSpans] = deriveEncoder
}

case class InstrumentationScope(
  name: String,
  version: String = "",
  attributes: List[KeyValue] = Nil,
  dropped_attributes_count: Int = 0
)
object InstrumentationScope {
  implicit val instrumentationScopeEncoder: Encoder.AsObject[InstrumentationScope] = deriveEncoder
}

case class Status(message: String, code: Status.Code)
object Status {
  implicit val statusEncoder: Encoder.AsObject[Status] = deriveEncoder

  sealed abstract class Code(val value: Int)
  object Code {
    implicit val codeEncoder: Encoder[Code] = Encoder.encodeInt.contramap[Code](_.value)

    case object STATUS_CODE_OK extends Code(1)
    case object STATUS_CODE_ERROR extends Code(2)
  }
}

case class Span(
  trace_id: Array[Byte],
  span_id: Array[Byte],
  trace_state: String = "",
  parent_span_id: Array[Byte],
  name: String,
  kind: Span.SpanKind,
  start_time_unix_nano: Long,
  end_time_unix_nano: Long,
  attributes: List[KeyValue],
  dropped_attributes_count: Int = 0,
  events: List[Span.Event] = List.empty,
  dropped_events_count: Int = 0,
  links: List[Span.Link],
  dropped_links_count: Int = 0,
  status: Status
)

object Span {
  implicit val byteArrayEncoder: Encoder[Array[Byte]] = Encoder.encodeString.contramap[Array[Byte]](Hex.encodeHexString)

  implicit val spanEncoder: Encoder.AsObject[Span] = deriveEncoder

  sealed abstract class SpanKind(val value: Int)
  object SpanKind {
    implicit val spanKindEncoder: Encoder[SpanKind] = Encoder.encodeInt.contramap[SpanKind](_.value)

    case object SPAN_KIND_INTERNAL extends SpanKind(1)
    case object SPAN_KIND_SERVER extends SpanKind(2)
    case object SPAN_KIND_CLIENT extends SpanKind(3)
    case object SPAN_KIND_PRODUCER extends SpanKind(4)
    case object SPAN_KIND_CONSUMER extends SpanKind(5)
  }

  case class Event(time_unix_nano: Long, name: String, attributes: List[KeyValue], dropped_attributes_count: Int = 0)
  object Event {
    implicit val eventEncoder: Encoder.AsObject[Event] = deriveEncoder
  }

  final case class Link(
    trace_id: Array[Byte],
    span_id: Array[Byte],
    trace_state: String = "",
    attributes: List[KeyValue] = List.empty,
    dropped_attributes_count: Int = 0
  )
  object Link {
    implicit val linkEncoder: Encoder.AsObject[Link] = deriveEncoder
  }
}

case class KeyValue(key: String, value: AnyValue)
object KeyValue {
  implicit val keyValueEncoder: Encoder.AsObject[KeyValue] = deriveEncoder
}

sealed trait AnyValue {
  def value: Any
}
object AnyValue {
  implicit val anyValueEncoder: Encoder.AsObject[AnyValue] =
    deriveEncoder[AnyValue].mapJsonObject(_.mapValues(_.hcursor.downField("value").focus.getOrElse(Json.Null)))

  case class boolValue(value: Boolean) extends AnyValue
  case class doubleValue(value: Double) extends AnyValue
  case class intValue(value: Long) extends AnyValue
  case class stringValue(value: String) extends AnyValue
}
