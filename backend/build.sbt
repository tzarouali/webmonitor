lazy val catsVersion = "1.0.0-RC1"
lazy val circeVersion = "0.9.0-M2"
lazy val catsEffectVersion = "0.5"
lazy val jsoupVersion = "1.10.3"
lazy val phantomVersion = "2.13.5"
lazy val playCirceVersion = "2609M2.0"
lazy val akkaActorVersion = "2.5.8"
lazy val logbackVersion = "1.2.3"
lazy val scalaLoggingVersion = "3.7.2"
lazy val akkaStreamKafkaVersion = "0.18"

val strictScalacOptions = Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)



lazy val commonSettings = Seq(
  organization := "com.github.tzarouali.webmonitor",
  scalaVersion := "2.12.3",
  version      := "0.0.1"
)


lazy val common = (project in file("common"))
  .settings(
    commonSettings,
    name := "model",
    libraryDependencies ++= Seq(
      "io.circe"            %% "circe-core"             % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-generic"          % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-parser"           % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-java8"            % circeVersion            withSources() withJavadoc()
    ),
    scalacOptions := strictScalacOptions
  )


lazy val restapi = (project in file("restapi"))
  .enablePlugins(PlayScala)
  .settings(
    commonSettings,
    name := "restapi",
    libraryDependencies ++= Seq(
      "io.circe"            %% "circe-core"             % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-generic"          % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-parser"           % circeVersion            withSources() withJavadoc(),
      "io.circe"            %% "circe-java8"            % circeVersion            withSources() withJavadoc(),

      "com.dripower"        %% "play-circe"             % playCirceVersion        withSources() withJavadoc(),

      "org.typelevel"       %% "cats-core"              % catsVersion             withSources() withJavadoc(),
      "org.typelevel"       %% "cats-effect"            % catsEffectVersion       withSources() withJavadoc(),

      "com.outworkers"      %% "phantom-dsl"            % phantomVersion          withSources() withJavadoc(),
      "com.outworkers"      %% "phantom-jdk8"           % phantomVersion          withSources() withJavadoc(),

      "org.jsoup"           % "jsoup"                   % jsoupVersion
    ),
    scalacOptions := strictScalacOptions.filterNot(Set(
      "-Xfatal-warnings"
    ))
  ).dependsOn(common)


lazy val subscraper = (project in file("subscraper"))
  .settings(
    commonSettings,
    name := "subscraper",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"           %% "akka-stream"            % akkaActorVersion        withSources() withJavadoc(),

      "org.typelevel"               %% "cats-core"              % catsVersion             withSources() withJavadoc(),
      "org.typelevel"               %% "cats-effect"            % catsEffectVersion       withSources() withJavadoc(),

      "com.outworkers"              %% "phantom-dsl"            % phantomVersion          withSources() withJavadoc(),
      "com.outworkers"              %% "phantom-jdk8"           % phantomVersion          withSources() withJavadoc(),

      "com.typesafe.scala-logging"  %% "scala-logging"          % scalaLoggingVersion,
      "ch.qos.logback"              %  "logback-classic"        % logbackVersion,

      "org.jsoup"                   %  "jsoup"                  % jsoupVersion,

      "com.typesafe.akka"           %% "akka-stream-kafka"      % akkaStreamKafkaVersion withSources() withJavadoc()
    ),
    scalacOptions := strictScalacOptions,
    assemblyJarName in assembly := "subscraper.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyMergeStrategy in assembly := {
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ).dependsOn(common)


lazy val subsocket = (project in file("subsocket"))
  .enablePlugins(PlayScala)
  .settings(
    commonSettings,
    name := "subsocket",
    libraryDependencies ++= Seq(
      "io.circe"                    %% "circe-core"               % circeVersion            withSources() withJavadoc(),
      "io.circe"                    %% "circe-generic"            % circeVersion            withSources() withJavadoc(),
      "io.circe"                    %% "circe-parser"             % circeVersion            withSources() withJavadoc(),
      "io.circe"                    %% "circe-java8"              % circeVersion            withSources() withJavadoc(),

      "com.dripower"                %% "play-circe"               % playCirceVersion        withSources() withJavadoc(),

      "org.typelevel"               %% "cats-core"                % catsVersion             withSources() withJavadoc(),
      "org.typelevel"               %% "cats-effect"              % catsEffectVersion       withSources() withJavadoc(),

      "com.typesafe.scala-logging"  %% "scala-logging"            % scalaLoggingVersion,
      "ch.qos.logback"              %  "logback-classic"          % logbackVersion,

      "com.typesafe.akka"           %% "akka-stream-kafka"        % akkaStreamKafkaVersion withSources() withJavadoc()
    ),
    scalacOptions := strictScalacOptions.filterNot(Set(
      "-Xfatal-warnings"
    ))
  ).dependsOn(common)


lazy val root = (project in file("."))
  .aggregate(common, restapi, subscraper, subsocket)
