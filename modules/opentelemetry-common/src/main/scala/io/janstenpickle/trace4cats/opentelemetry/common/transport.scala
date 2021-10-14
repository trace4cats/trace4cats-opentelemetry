package io.janstenpickle.trace4cats.opentelemetry.common

object transport {
  sealed trait GrpcTransportType
  object GrpcTransportType {
    object Plaintext extends GrpcTransportType
    object Secure extends GrpcTransportType

    val Default = Plaintext
  }
}
