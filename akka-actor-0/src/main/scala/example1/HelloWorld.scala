package example1

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

object HelloWorld {

  /**
   * アクターの処理を定義しているメソッド。
   * アクターのメッセージハンドラを [[Behaviors.receive]] を使って記述している。
   * このメソッドはメッセージを受信した際のPartial Function（部分関数）を指定する。
   *
   * @see <a href="https://yuroyoro.hatenablog.com/entry/20100705/1278328898">ScalaのPartialFunctionが便利ですよ | ゆるよろ日記</a>
   * @return
   */
  def apply(): Behavior[String] = Behaviors.receive[String] { (context: ActorContext[String], message: String) =>
    // ロギング機能
    context.log.info(s"Hello, $message!")
    // アクターが現在の状態から変化しない = 同一であることを意味している。
    Behaviors.same
  }

  def main(args: Array[String]): Unit = {
    val ref = ActorSystem(apply(), "main")

    ref ! "World"
  }
}
