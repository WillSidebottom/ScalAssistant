package com.scalassistant

import akka.actor.{ Actor, ActorLogging }

object Helper {
  val emptyHelpMessage = List(
    "Try using the following help commands to get a better idea of what I can do:",
    "help greetings",
    "help time",
    "help date",
    "help weather",
    "help exit"
  )

  val invalidRequest = List("I'm sorry, I can't help you with that.")
}

class Helper extends Actor with ActorLogging {
  import Greeter._
  import Weatherman._
  import TimeKeeper._
  import Helper._
  import Terminator._

  def receive = {
    case MatchPhrase(phrase) =>
      val handled = Utils.matchesPhrase(List("help.*"), phrase)
      log.info(s"Helper: handled phrase = ${handled}")
      if (handled) {
        log.info("Helper: Attempting to assist the user")
        val message = processPhrase(phrase)
        message.foreach(println)
      }
      sender ! PhraseHandled(handled)

    case unknown =>
      log.info("Helper: received an unknown message " + unknown)
  }

  def processPhrase(phrase: String): List[String] = {
    val helpRegex = "help".r
    val function = helpRegex.replaceAllIn(phrase, "").trim
    function match {
      case ""           => emptyHelpMessage
      case "greetings"  => Greeter.validGreetings
      case "time"       => TimeKeeper.validTimePhrases
      case "date"       => TimeKeeper.validDatePhrases
      case "weather"    => Weatherman.validWeatherPhrases
      case "exit"       => Terminator.validTerminationPhrases
      case _            => invalidRequest
    }
  }
}