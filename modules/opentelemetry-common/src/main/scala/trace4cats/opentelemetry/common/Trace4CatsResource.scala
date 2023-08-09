package trace4cats.opentelemetry.common

import io.opentelemetry.sdk.resources.Resource
import trace4cats.AttributeValue

object Trace4CatsResource {
  def apply(serviceName: String, attributes: Map[String, AttributeValue]): Resource =
    Resource.create(Trace4CatsAttributes(Map[String, AttributeValue]("service.name" -> serviceName) ++ attributes))
}
