package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }
import akka.event.Logging
import java.net.URLEncoder
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.util.{Try, Success, Failure}

object Weatherman {
  val phrasesWeRespondTo = List(
    "weather forecast.*",
    "weather.*",
    "forecast.*"
  )

  val validWeatherPhrases = List(
    "Ask me anything about the weather in the United States! Here are the formats for a date request:",
    "weather <location>",
    "weather forecast <location>",
    "forecast <location>",
    "EXAMPLE: weather tallahassee, fl"
  )
}

class Weatherman extends Actor with ActorLogging {
  import Weatherman._
  import WeatherUnderground._

  def receive = {
    case MatchPhrase(phrase) =>
      val handled = Utils.matchesPhrase(phrasesWeRespondTo, phrase)
      log.info(s"Weatherman: handled phrase = ${handled}")
      if (handled) {
        log.info("Weatherman: Attempting to process weather phrase")
        val message = processPhrase(phrase)
        message.foreach(println)
      }
      sender ! PhraseHandled(handled)

      case unknown =>
        log.info("Weatherman: received an unknown message " + unknown)
  }

  /* attempt to process the given phrase */
  def processPhrase(phrase: String): List[String] = {
    val weatherRegex = "weather|forecast".r
    val location = weatherRegex.replaceAllIn(phrase, "").trim
    location match {
      case "" => getWeatherConditions(buildWeatherUrl())
      case _ => getWeatherConditions(buildWeatherUrl(location))
    }
  }

  /* build a valid yahoo query url based on the specified location */
  def buildWeatherUrl(location: String = "tallahassee, fl"): String = {
    val cityAndState = location.split(",").map(_.trim)
    val city = cityAndState(0)
    val state = cityAndState(1)
    
    s"http://api.wunderground.com/api/095476a7964abce1/conditions/q/${state}/${city}.json"
  }

  /* attempt to get weather conditions for specified location */
  def getWeatherConditions(url: String): List[String] = {
    Try(Utils.getContent(url)) match {
      case Success(jsonStr) =>  
        val weatherObject = Utils.parseJsonString[Weather](jsonStr)
        formatResult(weatherObject)
      
      case Failure(error) => 
        log.error(s"WeatherMan recieved this error ${error.getMessage}")
        List("Sorry, could not retrieve the weather")
    }
  }

  /* format the retrieved weather result */
  private def formatResult(weatherObject: Option[Weather] = None): List[String] = weatherObject match {
    case Some(weather) =>
      List(
        s"Retrieved weather conditions for: ${weather.current_observation.display_location.full}",
        s"${weather.current_observation.observation_time}",
        s"Current conditions are: ${weather.current_observation.weather}",
        s"Current temperature is: ${weather.current_observation.temperature_string}",
        s"It feels like: ${weather.current_observation.feelslike_string}",
        s"Humidity: ${weather.current_observation.relative_humidity}",
        s"Wind: ${weather.current_observation.wind_string}"
      )
    case None =>
      List("Sorry, the weather conditions could not be retireved for that location")
  }

}
