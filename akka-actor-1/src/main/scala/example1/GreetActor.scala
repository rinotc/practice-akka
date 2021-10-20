package example1

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

// 挨拶を受け取り、応答を返すアクター
object GreetActor {
  import Protocol._

  // Behavior#recive はメッセージハンドラ。Greetメッセージのみ受信
  def apply(): Behavior[Greet] = Behaviors.receive { (context: ActorContext[Greet], message: Greet) =>
    // ログを出力したあと、
    context.log.info(s"GreetActor | ${message.text}, from ${message.whom}")
    // すぐにメッセージの送信元である、Greet.replyToにGreetReplyメッセージを返す。
    message.replyTo ! GreetReply(message.whom, context.self)
    // 状態繊維はしない
    Behaviors.same
  }
}
