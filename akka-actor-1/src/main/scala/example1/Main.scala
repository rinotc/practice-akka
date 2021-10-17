package example1

import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors

// エントリポイント
object Main {
  def main(args: Array[String]): Unit = {
    def apply(max: Int): Behavior[Any] = Behaviors.setup[Any] { context =>
      val controllerRef = context.spawn(Controller(max), "controller")
      context.watch(controllerRef)
      Behaviors.receiveSignal {
        case (context, Terminated(ref)) if ref == controllerRef =>
          context.log.info("The child actor have stopped, the will stop self.")
          Behaviors.stopped
      }
    }

    ActorSystem(apply(10), "main")
  }
}
