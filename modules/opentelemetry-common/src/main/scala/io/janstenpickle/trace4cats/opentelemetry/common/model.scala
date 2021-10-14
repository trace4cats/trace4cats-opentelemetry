package io.janstenpickle.trace4cats.opentelemetry.common

object model {
  sealed trait GrpcTransportType
  object GrpcTransportType {
    object Plaintext extends GrpcTransportType
    object SSL extends GrpcTransportType

    val Default = Plaintext
  }
}
