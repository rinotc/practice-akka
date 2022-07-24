package chatroom

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ChatRoom {
  sealed trait RoomCommand
  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand
  private case class PublishSessionMessage(screenName: String, message: String)    extends RoomCommand

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
  final case class SessionDenied(reason: String)                      extends SessionEvent
  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  sealed trait SessionCommand
  final case class PostMessage(message: String)                 extends SessionCommand
  private final case class NotifyClient(message: MessagePosted) extends SessionCommand

  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { case (context, message) =>
      message match {
        case GetSession(screenName, client) =>
          // クライアントとさらに対話するための子アクターを作成する
          val ses = context.spawn(
            session(context.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8)
          )
          client ! SessionGranted(ses)
          chatRoom(ses :: sessions)
        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }
    }

  private def session(
      room: ActorRef[PublishSessionMessage],
      screenName: String,
      client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand] = Behaviors.receiveMessage {
    case PostMessage(message) =>
      // クライアントから、ルームを介して他の人にパブリッシュする
      room ! PublishSessionMessage(screenName, message)
      Behaviors.same
    case NotifyClient(message) =>
      client ! message
      Behaviors.same
  }

}
