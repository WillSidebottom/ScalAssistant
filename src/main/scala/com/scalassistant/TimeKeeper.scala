package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }
import akka.event.Logging
import java.util.Calendar
import java.text.SimpleDateFormat

object TimeKeeper {
  val timePhrases = List (
    "time.*",
    "current time.*",
    "what is the time.*",
    "What is the current time.*"
  )

  val datePhrases = List (
    "date.*",
    "what is today's date.*",
    "what is the date.*",
    "what is the current date.*"
  )
}

class TimeKeeper extends Actor with ActorLogging {
  import TimeKeeper._

  def receive = {
    case MatchPhrase(phrase) =>
      val timeHandled = Utils.matchesPhrase(timePhrases, phrase)
      val dateHandled = Utils.matchesPhrase(datePhrases, phrase)
      val handled = timeHandled || dateHandled
      log.info(s"TimeKeeper: handled phrase = ${handled}")
      if (timeHandled) println(getCurrentTime)
      if (dateHandled) println(getCurrentDate)
      sender ! PhraseHandled(handled)

    case unknown =>
      log.info("TimeKeeper: received an unknown message" + unknown)
  }

  /* time and date functions using java utility */
  def getCurrentDate: String = getCurrentDateTime("EEEE, MMMM d")

  def getCurrentTime: String = getCurrentDateTime("K:m aa")

  private def getCurrentDateTime(dateTimeFormat: String): String = {
    val dateFormat = new SimpleDateFormat(dateTimeFormat)
    val cal = Calendar.getInstance()
    dateFormat.format(cal.getTime())
  }
}