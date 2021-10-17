package example1

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

// GreeterActorとメッセージを交換するアクター
object Controller {
  import Protocol._

  def apply(max: Int): Behavior[GreetReply] = Behaviors.receive {
    (context: ActorContext[GreetReply], message: GreetReply) =>
      val greetRef: ActorRef[Greet] = context.spawn(GreetActor(), "thread")
      greetRef ! Greet("message: 0", "user-1", context.self)
      processing(greetingCounter = 0, max)(context)
  }

  private def processing(greetingCounter: Int, max: Int)(context: ActorContext[GreetReply]): Behavior[GreetReply] = {
    Behaviors.receiveMessage[GreetReply] { message: GreetReply =>
      val n = greetingCounter + 1
      if (n == max) Behaviors.stopped
      else {
        message.replyTo ! Greet(s"message: $n", message.whom, context.self)
        processing(n, max)(context)
      }
    }
  }
}
