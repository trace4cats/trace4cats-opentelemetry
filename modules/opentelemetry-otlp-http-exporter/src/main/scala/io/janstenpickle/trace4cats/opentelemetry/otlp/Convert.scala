package io.janstenpickle.trace4cats.opentelemetry.otlp

import cats.Foldable
import cats.syntax.foldable._
import cats.syntax.semigroup._
import cats.syntax.show._
import com.google.protobuf.ByteString
import io.janstenpickle.trace4cats.model.SpanStatus._
import io.janstenpickle.trace4cats.model._
import io.opentelemetry.proto.common.v1.common.AnyValue.Value
import io.opentelemetry.proto.common.v1.common._
import io.opentelemetry.proto.resource.v1.resource.Resource
import io.opentelemetry.proto.trace.v1.trace.Span.SpanKind._
import io.opentelemetry.proto.trace.v1.trace.Status.StatusCode._
import io.opentelemetry.proto.trace.v1.trace.{InstrumentationLibrarySpans, ResourceSpans, Span, Status}
import org.apache.commons.codec.binary.Hex
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import scalapb.json4s.{FormatRegistry, JsonFormat, Printer}

import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer

object Convert {
  def toAttributes(attributes: Map[String, AttributeValue]): List[KeyValue] =
    attributes.toList.map {
      case (k, AttributeValue.StringValue(v)) =>
        KeyValue(key = k, value = Some(AnyValue.of(AnyValue.Value.StringValue(v.value))))
      case (k, AttributeValue.BooleanValue(v)) =>
        KeyValue(key = k, value = Some(AnyValue.of(AnyValue.Value.BoolValue(v.value))))
      case (k, AttributeValue.DoubleValue(v)) =>
        KeyValue(key = k, value = Some(AnyValue.of(AnyValue.Value.DoubleValue(v.value))))
      case (k, AttributeValue.LongValue(v)) =>
        KeyValue(key = k, value = Some(AnyValue.of(AnyValue.Value.IntValue(v.value))))
      case (k, v: AttributeValue.AttributeList) =>
        KeyValue(key = k, value = Some(AnyValue.of(AnyValue.Value.StringValue(v.show))))
    }

  def toSpan(span: CompletedSpan): Span =
    Span(
      traceId = ByteString.copyFrom(span.context.traceId.value),
      spanId = ByteString.copyFrom(span.context.spanId.value),
      parentSpanId = span.context.parent.fold(ByteString.EMPTY)(parent => ByteString.copyFrom(parent.spanId.value)),
      name = span.name,
      kind = span.kind match {
        case SpanKind.Internal => SPAN_KIND_INTERNAL
        case SpanKind.Client => SPAN_KIND_CLIENT
        case SpanKind.Server => SPAN_KIND_SERVER
        case SpanKind.Producer => SPAN_KIND_PRODUCER
        case SpanKind.Consumer => SPAN_KIND_CONSUMER
      },
      startTimeUnixNano = TimeUnit.MILLISECONDS.toNanos(span.start.toEpochMilli),
      endTimeUnixNano = TimeUnit.MILLISECONDS.toNanos(span.end.toEpochMilli),
      attributes = toAttributes(span.allAttributes),
      status = Some(
        Status(
          code = span.status match {
            case s if s.isOk => STATUS_CODE_OK
            case _ => STATUS_CODE_ERROR
          },
          message = span.status match {
            case Internal(message) => message
            case _ => ""
          }
        )
      ),
      links = span.links.fold(List.empty[Span.Link])(_.map { link =>
        Span.Link(ByteString.copyFrom(link.traceId.value), ByteString.copyFrom(link.spanId.value))
      }.toList)
    )

  def toInstrumentationLibrarySpans(spans: List[CompletedSpan]): InstrumentationLibrarySpans =
    InstrumentationLibrarySpans(
      instrumentationLibrary = Some(InstrumentationLibrary("trace4cats")),
      spans = spans
        .foldLeft(ListBuffer.empty[Span]) { (buf, span) =>
          buf += toSpan(span)
        }
        .toList
    )

  def toResourceSpans[G[_]: Foldable](batch: Batch[G]): Iterable[ResourceSpans] =
    batch.spans
      .foldLeft(Map.empty[String, List[CompletedSpan]]) { (acc, span) =>
        acc |+| Map(span.serviceName -> List(span))
      }
      .map { case (service, spans) =>
        ResourceSpans(
          resource = Some(
            Resource(attributes =
              List(KeyValue("service.name", Some(AnyValue.of(AnyValue.Value.StringValue(service)))))
            )
          ),
          instrumentationLibrarySpans = List(toInstrumentationLibrarySpans(spans))
        )
      }

  val formatRegistry: FormatRegistry = JsonFormat.DefaultRegistry
    .registerWriter[AnyValue](
      _.value match {
        case Value.StringValue(value) => "stringValue" -> value
        case Value.BoolValue(value) => "boolValue" -> value
        case Value.IntValue(value) => "intValue" -> value
        case Value.DoubleValue(value) => "doubleValue" -> value
        case _ => JNothing // Other `Value` subtypes are not used in `toAttributes`
      },
      _ => AnyValue.defaultInstance // Decoder is not needed
    )
    .registerMessageFormatter[Span](
      {
        case (
              printer,
              Span(
                traceId,
                spanId,
                traceState,
                parentSpanId,
                name,
                kind,
                startTimeUnixNano,
                endTimeUnixNano,
                attributes,
                droppedAttributesCount,
                events,
                droppedEventsCount,
                links,
                droppedLinksCount,
                status,
                _ // unknownFields
              )
            ) =>
          val tmp = ("trace_id" -> Hex.encodeHexString(traceId.toByteArray)) ~
            ("span_id" -> Hex.encodeHexString(spanId.toByteArray)) ~
            ("trace_state" -> traceState) ~
            ("parent_span_id" -> Hex.encodeHexString(parentSpanId.toByteArray)) ~
            ("name" -> name) ~
            ("kind" -> kind.value) ~
            ("start_time_unix_nano" -> startTimeUnixNano) ~
            ("end_time_unix_nano" -> endTimeUnixNano) ~
            ("attributes" -> attributes.map(printer.toJson)) ~
            ("dropped_attributes_count" -> droppedAttributesCount) ~
            ("events" -> events.map(printer.toJson)) ~
            ("dropped_events_count" -> droppedEventsCount) ~
            ("links" -> links.map(printer.toJson)) ~
            ("dropped_links_count" -> droppedLinksCount)
          status.fold(tmp)(s => tmp ~ ("status" -> printer.toJson(s)))
      },
      (_, _) => Span.defaultInstance // Decoder is not needed
    )
    .registerMessageFormatter[Span.Link](
      {
        case (
              printer,
              Span.Link(
                traceId,
                spanId,
                traceState,
                attributes,
                droppedAttributesCount,
                _ // unknownFields
              )
            ) =>
          ("trace_id" -> Hex.encodeHexString(traceId.toByteArray)) ~
            ("span_id" -> Hex.encodeHexString(spanId.toByteArray)) ~
            ("trace_state" -> traceState) ~
            ("attributes" -> attributes.map(printer.toJson)) ~
            ("dropped_attributes_count" -> droppedAttributesCount)
      },
      (_, _) => Span.Link.defaultInstance // Decoder is not needed
    )
    .registerWriter[Status](
      {
        case Status(
              _, // deprecatedCode
              message,
              code,
              _ // unknownFields
            ) =>
          ("message" -> message) ~
            ("code" -> code.value)
      },
      _ => Status.defaultInstance // Decoder is not needed
    )

  val printer: Printer = new Printer().preservingProtoFieldNames.includingDefaultValueFields
    .withFormatRegistry(formatRegistry = formatRegistry)

  def toJsonString[G[_]: Foldable](batch: Batch[G]): String = {
    val json = "resource_spans" -> Convert.toResourceSpans(batch).map(printer.toJson).toList
    compact(render(json))
  }
}
