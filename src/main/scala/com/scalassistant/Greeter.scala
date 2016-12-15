package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }

object Greeter {
  val phrasesWeRespondTo = List (
    "hello.*",
    "hi.*",
    "hola.*",
    "greetings.*",
    "salutatations.*"
  )

  val greetings = List (
    "hello",
    "hi",
    "hola",
    "greetings",
    "salutations"
  )
}

class Greeter extends Actor with ActorLogging {
  import Greeter._

  def receive = {
    case MatchPhrase(phrase) => 
      val handled = Utils.matchesPhrase(phrasesWeRespondTo, phrase)
      log.info(s"Greeter: handled phrase = ${handled}")
      if (handled) println(getRandomHelloPhrase)
      log.info(s"Greeter: Attempting to send message back to Monitor")
      sender ! PhraseHandled(handled)

    case unknown =>
      log.info("Greeter: received an unknown message" + unknown)  
  }

  /* get a random greeting from our list of greetings */
  private def getRandomHelloPhrase: String = {
    Utils.getRandomElement(greetings)
  }

}