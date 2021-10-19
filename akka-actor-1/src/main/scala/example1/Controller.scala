package example1

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

// GreeterActorとメッセージを交換するアクター
object Controller {
  import Protocol._

  // Mainオブジェクトからapplyが呼ばれると内部のBehaviors.setupの処理が実行される。
  // setupメソッドはアクターの初期化を行うメソッド。
  // 関数の引数には ActorContext が渡される。
  def apply(max: Int): Behavior[GreetReply] = Behaviors.setup { context: ActorContext[GreetReply] =>
    // GreetActor を ActorContext#spawn をを使って生成する。
    val greetRef: ActorRef[Greet] = context.spawn(GreetActor(), "thread")
    // GreetActorにメッセージを送信する。ActorRef型のインスタンス ! 送信するメッセージ
    // Controllerアクターが、GreetActorアクターにメッセージを送っている。
    greetRef ! Greet("message: 0", "user-1", context.self) // context.self は自分自身のActorRefを意味する。誰から送信がきたのか分かるように。
    processing(greetingCounter = 0, max)(context)
  }

  private def processing(greetingCounter: Int, max: Int)(context: ActorContext[GreetReply]): Behavior[GreetReply] = {
    // この部分はメッセージを受信した際の処理を示している。いわゆるメッセージハンドラ
    // メッセージの型はGreetReplyで、それ以外の型は関数に渡されない。
    Behaviors.receiveMessage[GreetReply] { message: GreetReply =>
      val n = greetingCounter + 1
      context.log.info(s"processing | receive $message, $n times")
      if (n == max) Behaviors.stopped // Behaviors.stopped を返すとアクターは停止する。
      else {
        // GreetReply.replyTo に対してメッセージを送信
        message.replyTo ! Greet(s"message: $n", message.whom, context.self)
        processing(n, max)(context)
      }
    }
  }
}
