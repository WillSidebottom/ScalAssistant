package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


class Monitor extends Actor with ActorLogging{

  /* actors we monitor */
  val greeter = context.actorSelection("/user/greeter")
  val timeKeeper = context.actorSelection("/user/timeKeeper")
  val weatherman = context.actorSelection("/user/weatherman")

  /* create a list of those actors */ 
  val workers = List(greeter, timeKeeper, weatherman)

  def receive = {
  	case ConsoleMessage(msg: String) => 
      log.info(s"Monitor: Recieved a Console Message ${msg}")
      log.info(s"Monitor: Attempting to match phrase")
      if (tryMatchingPhrase(msg.toLowerCase) == false) 
        println("Sorry, I couldn't handle that phrase")
      log.info(s"Montior: responding back to sender")
      sender ! PromptUser(msg)

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