package com.scalassistant

import akka.actor.{ Actor, ActorLogging }

object Greeter {
  val phrasesWeRespondTo = List ( /* list of greetings we respond to */
    "hello.*",
    "hi.*",
    "hola.*",
    "greetings.*",
    "salutations.*",
    "hey.*",
    "howdy.*"
  )

  val greetings = List (  /* actual greetings we can say */
    "Hello!",
    "Hi!",
    "Hola!",
    "Greetings!",
    "Salutations!",
    "Hi there!",
    "Hey!",
    "Howdy!"
  )

  val validGreetings = List ( /* Show the user what greetings we respond to */
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

  def receive = { /* match incoming messages */
    case MatchPhrase(phrase) => 
      val handled = Utils.matchesPhrase(phrasesWeRespondTo, phrase.toLowerCase)
      log.info(s"Greeter: handled phrase = ${handled}")
      if (handled) println(getRandomHelloPhrase)
      sender ! PhraseHandled(handled)

    case unknown =>
      log.info("Greeter: received an unknown message" + unknown)  
  }

  /* get a random greeting from our list of greetings */
  private def getRandomHelloPhrase: String = {
    Utils.getRandomElement(greetings)
  }

}