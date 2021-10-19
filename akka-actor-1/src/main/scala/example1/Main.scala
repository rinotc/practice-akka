package example1

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors

// エントリポイント
object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem(apply(10), "main")
  }

  def apply(max: Int): Behavior[Any] = Behaviors.setup[Any] { context =>
    // Controller アクターを起動する処理。
    // アクターは起動すると停止するまでプロセスとして常駐した状態になる
    // ActorContext#spawnの第一引数にはBehavior[A]型のインスタンスを渡す。A型はアクターが受け取るメッセージの型
    // 第二引数はアクター名を指定する※アクター名は必ずユニーク
    // 生成が成功すると、spawnの戻り値としてActorRef[A]型のインスタンスが返される。A型はアクターが受け取るメッセージ
    val controllerRef: ActorRef[Protocol.GreetReply] = context.spawn(Controller(max), "controller")
    context.watch(controllerRef)
    Behaviors.receiveSignal {
      case (context, Terminated(ref)) if ref == controllerRef =>
        context.log.info("The child actor have stopped, then will stop self.")
        Behaviors.stopped
    }
  }
}
