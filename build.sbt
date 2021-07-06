lazy val commonSettings = Seq(
  Compile / compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(compilerPlugin(Dependencies.kindProjector), compilerPlugin(Dependencies.betterMonadicFor))
      case _ => Seq.empty
    }
  },
  scalacOptions := {
    val opts = scalacOptions.value
    val wconf = "-Wconf:src=src_managed/.*:s,any:wv"
    val fatalw = "-Xfatal-warnings"
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => opts :+ wconf
      case Some((2, 12)) => opts.filterNot(Set(fatalw)) :+ wconf
      case _ => opts
    }
  },
  Test / fork := true,
  resolvers += Resolver.sonatypeRepo("releases"),
)

lazy val noPublishSettings =
  commonSettings ++ Seq(publish := {}, publishArtifact := false, publishTo := None, publish / skip := true)

lazy val publishSettings = commonSettings ++ Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  Test / publishArtifact := false
)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "Trace4Cats OpenTelemetry")
  .aggregate(
    `opentelemetry-common`,
    `opentelemetry-jaeger-exporter`,
    `opentelemetry-otlp-grpc-exporter`,
    `opentelemetry-otlp-http-exporter`
  )

lazy val `opentelemetry-common` =
  (project in file("modules/opentelemetry-common"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-opentelemetry-common",
      libraryDependencies ++= Seq(
        Dependencies.openTelemetrySdk,
        Dependencies.collectionCompat,
        Dependencies.grpcApi,
        Dependencies.trace4catsModel,
        Dependencies.trace4catsKernel,
        Dependencies.trace4catsExporterCommon
      )
    )

lazy val `opentelemetry-jaeger-exporter` =
  (project in file("modules/opentelemetry-jaeger-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-opentelemetry-jaeger-exporter",
      libraryDependencies ++= Seq(Dependencies.openTelemetryJaegerExporter),
      libraryDependencies ++= Seq(Dependencies.grpcOkHttp, Dependencies.trace4catsJaegerIntegrationTest).map(_ % Test)
    )
    .dependsOn(`opentelemetry-common`)

lazy val `opentelemetry-otlp-grpc-exporter` =
  (project in file("modules/opentelemetry-otlp-grpc-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-opentelemetry-otlp-grpc-exporter",
      libraryDependencies ++= Seq(Dependencies.openTelemetryOtlpExporter, Dependencies.grpcOkHttp % Test),
      libraryDependencies ++= Seq(Dependencies.grpcOkHttp, Dependencies.trace4catsJaegerIntegrationTest).map(_ % Test)
    )
    .dependsOn(`opentelemetry-common`)

lazy val `opentelemetry-otlp-http-exporter` =
  (project in file("modules/opentelemetry-otlp-http-exporter"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-opentelemetry-otlp-http-exporter",
      libraryDependencies ++= Seq(
        Dependencies.circeGeneric,
        Dependencies.http4sBlazeClient,
        (Dependencies.openTelemetryProto % "protobuf").intransitive(),
        Dependencies.scalapbJson,
        Dependencies.trace4catsModel,
        Dependencies.trace4catsKernel,
        Dependencies.trace4catsExporterCommon,
        Dependencies.trace4catsExporterHttp
      ),
      libraryDependencies ++= Seq(Dependencies.trace4catsJaegerIntegrationTest).map(_ % Test),
      Compile / PB.protoSources += target.value / "protobuf_external",
      Compile / PB.targets := Seq(scalapb.gen(grpc = false, lenses = false) -> (Compile / sourceManaged).value)
    )
