package com.github.rinotc.akkaGettingStarted

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

object Protocol {
  final case class Greet(
      text: String,
      whom: String,
      replyTo: ActorRef[GreetReply]
  )

  final case class GreetReply(whom: String, replyTo: ActorRef[Greet])
}

object HelloWorld {
  import Protocol._

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info(s"${message.text}, ${message.whom}!")
    message.replyTo ! GreetReply(message.whom, context.self)
    Behaviors.same
  }
}

object Controller {
  import Protocol._

  def apply(max: Int): Behavior[GreetReply] = Behaviors.setup[GreetReply] { context =>
    val helloWorldRef = context.spawn(HelloWorld(), "thread")
    helloWorldRef ! Greet("message: 0", "user-1", context.self)
    processing(greetingCounter = 0, max)(context)
  }

  private def processing(greetingCounter: Int, max: Int)(context: ActorContext[GreetReply]): Behavior[GreetReply] = {
    Behaviors.receiveMessage[GreetReply] { message =>
      val n = greetingCounter + 1
      context.log.info(s"receive from ${message.whom}!")
      if (n == max)
        Behaviors.stopped
      else {
        message.replyTo ! Greet(s"message: $n", message.whom, context.self)
        processing(n, max)(context)
      }
    }
  }
}

// エントリポイント
object Main {
  def main(args: Array[String]): Unit = {
    def apply(max: Int): Behavior[Any] = Behaviors.setup[Any] { context =>
      val controllerRef = context.spawn(Controller(max), "controller")
      context.watch(controllerRef)
      Behaviors.receiveSignal({
        case (context, Terminated(ref)) if ref == controllerRef =>
          context.log.info("The child Actor have stopped, then will stop self.")
          Behaviors.stopped
      })
    }

    ActorSystem(apply(10), "main")
  }
}
