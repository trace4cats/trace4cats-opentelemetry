package trace4cats.opentelemetry

import trace4cats.SemanticTags
import trace4cats.model.{AttributeValue, SpanKind, SpanStatus, TraceProcess}

package object jaeger {
  val kindTags: SpanKind => Map[String, AttributeValue] = {
    case SpanKind.Internal => Map.empty
    case e => SemanticTags.kindTags(e)
  }

  val statusCode: SpanStatus => String = {
    case s if s.isOk => "OK"
    case _ => "ERROR"
  }

  val statusTags: SpanStatus => Map[String, AttributeValue] = { s =>
    val attrs = Map[String, AttributeValue](s"otel.status_code" -> statusCode(s))
    val errorAttrs: Map[String, AttributeValue] =
      s match {
        case SpanStatus.Internal(message) =>
          attrs ++ Map[String, AttributeValue]("error" -> true, "otel.status_description" -> message)
        case s if s.isOk => attrs
        case _ => attrs + ("error" -> true)
      }
    attrs ++ errorAttrs
  }

  val processTags: TraceProcess => Map[String, AttributeValue] = p =>
    Map[String, AttributeValue]("service.name" -> p.serviceName) ++ p.attributes

  val additionalTags: Map[String, AttributeValue] =
    Map("otel.library.name" -> "trace4cats", "otel.scope.name" -> "trace4cats")

  val excludedTagKeys: Set[String] = Set("ip")
}
