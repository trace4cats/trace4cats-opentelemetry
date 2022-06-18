package trace4cats.opentelemetry.common

final case class Endpoint(protocol: String, host: String, port: Int) {
  def render: String = s"$protocol://$host:$port"
}
