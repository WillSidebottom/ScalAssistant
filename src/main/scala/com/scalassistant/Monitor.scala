package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Terminator {
  val terminationPhrases = List (
    "goodbye.*",
    "turn off.*",
    "exit.*",
    "quit.*",
    "terminate.*"
  )
}

class Monitor extends Actor with ActorLogging{
  import Terminator._

  /* actors we monitor */
  val greeter    = context.actorSelection("/user/greeter")
  val timeKeeper = context.actorSelection("/user/timeKeeper")
  val weatherman = context.actorSelection("/user/weatherman")

  /* create a list of those actors */ 
  val workers = List(greeter, timeKeeper, weatherman)

  def receive = {
  	case ConsoleMessage(msg: String) => 
      log.info(s"Monitor: Recieved a Console Message ${msg}")
      if (Utils.matchesPhrase(terminationPhrases, msg)) { /* check for shutdown phrase */
        println("See you later!")
        log.info(s"Monitor: sending shutdown message to sender")
        sender ! Response("terminate")
      } else if (tryMatchingPhrase(msg.toLowerCase) == false) { /* check if phrase wasn't handled by workers*/
        println("Sorry, I couldn't handle that phrase")
        log.info("Monitor: could not handle phrase")
        sender ! Response(msg)
      } else { /* else, respond to main */
        log.info(s"Montior: responding back to sender")
        sender ! Response(msg)
      }

    case unknown =>
      log.info("Monitor: received an unknown message" + unknown)  
  }

  /* see if any of our actors can handle console message */
  def tryMatchingPhrase(phrase: String): Boolean = {
    implicit val timeout = Timeout(15 seconds)
    for(w <- workers) {
      val future = w ? MatchPhrase(phrase)
      val phraseHandled = Await.result(future, timeout.duration).asInstanceOf[PhraseHandled]
      if (phraseHandled.handled) return true
    }
    false
  }
}