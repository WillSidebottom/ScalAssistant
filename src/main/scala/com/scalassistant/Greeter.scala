package com.scalassistant

import akka.actor.{ Actor, ActorLogging }

object Greeter {
  val phrasesWeRespondTo = List (
    "hello.*",
    "hi.*",
    "hola.*",
    "greetings.*",
    "salutations.*",
    "hey.*",
    "howdy.*"
  )

  val greetings = List (
    "Hello!",
    "Hi!",
    "Hola!",
    "Greetings!",
    "Salutations!",
    "Hi there!",
    "Hey!",
    "Howdy!"
  )

  val validGreetings = List (
    "Don't be shy! I know a few greetings out there:",
    "hello",
    "hi",
    "hola",
    "greetings",
    "salutations",
    "hey",
    "howdy"
  )
}

class Greeter extends Actor with ActorLogging {
  import Greeter._

  def receive = {
    case MatchPhrase(phrase) => 
      val handled = Utils.matchesPhrase(phrasesWeRespondTo, phrase.toLowerCase)
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