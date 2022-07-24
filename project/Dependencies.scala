import sbt._

object Dependencies {

  object Versions {
    val akka           = "2.6.19"
    val akkaHttp       = "10.2.9"
    val logback        = "1.2.11"
    val typeSafeConfig = "1.4.2"
    val scalaTest      = "3.2.12"
  }

  object Logback {
    val classic = "ch.qos.logback" % "logback-classic" % Versions.logback
  }

  object ScalaTest { // https://www.scalatest.org/
    val scalactic = "org.scalactic" %% "scalactic" % Versions.scalaTest
    val scalatest = "org.scalatest" %% "scalatest" % Versions.scalaTest
  }

  object TypeSafe {

    val config = "com.typesafe" % "config" % Versions.typeSafeConfig

    object Akka { // https://doc.akka.io/docs/akka/current/index.html
      // akkaには新APIとclassic APIがある。新APIを使うようにしている。
      // https://doc.akka.io/docs/akka/current/index-classic.html (classic API)
      val actor   = "com.typesafe.akka" %% "akka-actor-typed"         % Versions.akka
      val testKit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.akka
      val slf4j   = "com.typesafe.akka" %% "akka-slf4j"               % Versions.akka
      val stream  = "com.typesafe.akka" %% "akka-stream"              % Versions.akka
      val http    = "com.typesafe.akka" %% "akka-http"                % Versions.akkaHttp
    }
  }
}
