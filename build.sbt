import Dependencies._

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.13.8",
  scalacOptions ++= Seq( // https://docs.scala-lang.org/overviews/compiler-options/index.html
    "-feature",          // 明示的に import する必要のある機能を使用した場合、警告と場所を知らせる
    "-deprecation",      // 非推奨のAPIの仕様している場合、警告と場所を知らせる
    "-unchecked",        // 生成されたコードが仮定に依存する場合は警告を出す
    //    "-Xfatal-warnings", // 警告が出た場合はコンパイル失敗させる
    "-Xlint",          // 推奨される警告の有効化
    "-Ywarn-dead-code" // デットコードがあれば警告する
  ),
  libraryDependencies ++= Seq(
    Logback.classic,
    ScalaTest.scalatest,
    TypeSafe.config,
    TypeSafe.Akka.actor,
    TypeSafe.Akka.testKit % Test
  )
)

lazy val `akka-actor-0` = (project in file("akka-actor-0"))
  .settings(commonSettings)

lazy val `akka-actor-1` = (project in file("akka-actor-1"))
  .settings(commonSettings)

lazy val `chatroom` = (project in file("chatroom"))
  .settings(commonSettings)
