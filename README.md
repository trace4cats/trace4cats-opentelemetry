# Trace4Cats OpenTelemetry Exporters

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/trace4cats/trace4cats-opentelemetry/Continuous%20Integration)](https://github.com/trace4cats/trace4cats-opentelemetry/actions?query=workflow%3A%22Continuous%20Integration%22)
[![GitHub stable release](https://img.shields.io/github/v/release/trace4cats/trace4cats-opentelemetry?label=stable&sort=semver)](https://github.com/trace4cats/trace4cats-opentelemetry/releases)
[![GitHub latest release](https://img.shields.io/github/v/release/trace4cats/trace4cats-opentelemetry?label=latest&include_prereleases&sort=semver)](https://github.com/trace4cats/trace4cats-opentelemetry/releases)
[![Maven Central early release](https://img.shields.io/maven-central/v/io.janstenpickle/trace4cats-opentelemetry-jaeger-exporter_2.13?label=early)](https://maven-badges.herokuapp.com/maven-central/io.janstenpickle/trace4cats-opentelemetry-jaeger-exporter_2.13)
[![Join the chat at https://gitter.im/trace4cats/community](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/trace4cats/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

[OpenTelemetry] exporters for [Trace4Cats].

Add it to your `build.sbt`:

```scala
"io.janstenpickle" %% "trace4cats-opentelemetry-otlp-grpc-exporter" % "0.14.2"
"io.janstenpickle" %% "trace4cats-opentelemetry-otlp-http-exporter" % "0.14.2"
"io.janstenpickle" %% "trace4cats-opentelemetry-jaeger-exporter" % "0.14.2"
"io.janstenpickle" %% "trace4cats-opentelemetry-otlp-http4s-grpc-exporter" % "0.14.2"
```


## Contributing

This project supports the [Scala Code of Conduct](https://typelevel.org/code-of-conduct.html) and aims that its channels
(mailing list, Gitter, github, etc.) to be welcoming environments for everyone.

[Trace4Cats]: https://github.com/trace4cats/trace4cats
[OpenTelemetry]: http://opentelemetry.io
