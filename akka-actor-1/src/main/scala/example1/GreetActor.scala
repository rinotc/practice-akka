package example1

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

// 挨拶を受け取り、応答を返すアクター
object GreetActor {
  import Protocol._

  def apply(): Behavior[Greet] = Behaviors.receive { (context: ActorContext[Greet], message: Greet) =>
    context.log.info(s"GreetActor | ${message.text}, from ${message.whom}")
    message.replyTo ! GreetReply(message.whom, context.self)
    Behaviors.same
  }
}
