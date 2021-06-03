addSbtPlugin("org.scalameta"             % "sbt-scalafmt"         % "2.4.2")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"         % "0.1.20")
addSbtPlugin("com.codecommit"            % "sbt-github-actions"   % "0.12.0")
addSbtPlugin("io.shiftleft"              % "sbt-ci-release-early" % "2.0.16")
addSbtPlugin("com.thesamet"              % "sbt-protoc"           % "1.0.4")
addSbtPlugin("com.dwijnand"              % "sbt-dynver"           % "4.1.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.3"
