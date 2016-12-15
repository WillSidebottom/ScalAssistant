package com.scalassistant

import akka.actor.{ ActorRef, Props, Actor, ActorLogging }
import akka.event.Logging
import java.net.URLEncoder
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.util.{Try, Success, Failure}

object Weatherman {
  case class Condition(code: Option[String], date: Option[String], temp: Option[String], text: Option[String])
  case class Item(condition: Option[Condition])
  case class Channel(item: Option[Item])
  case class Results(channel: Option[Channel])
  case class Query(count: Option[Int], created: Option[String], lang: Option[String], results: Option[Results])
  case class Weather(query: Option[Query])

  val phrasesWeRespondTo = List(
    "weather forecast.*",
    "weather.*",
    "forecast.*"
  )

  val validWeatherPhrases = List(
    "weather <location>",
    "weather forecast <location>",
    "forecast <location>",
    "weather tallahassee, fl",
    "weather miami",
    "weather forecast tampa"
  )
}

class Weatherman extends Actor with ActorLogging {
  import Weatherman._

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
        log.info("Weatherman: received an unknown message")
  }

  /* attempt to process the given phrase */
  def processPhrase(phrase: String): List[String] = {
    val weatherRegex = "weather|forecast".r
    val location = weatherRegex.replaceAllIn(phrase, "").trim
    location match {
      case "" =>
        getWeatherConditions(buildWeatherUrl())
      case _ =>
        getWeatherConditions(buildWeatherUrl(location), location)
    }
  }

  /* build a valid yahoo query url based on the specified location */
  def buildWeatherUrl(location: String = "tallahassee, fl"): String = {
    val baseUrl = "https://query.yahooapis.com/v1/public/yql?q="
    val locationQuery = "(select woeid from geo.places where text=\"%s\")".format(location)
    val yqlQuery = s"select item.condition from weather.forecast where woeid in ${locationQuery}"
    
    baseUrl + URLEncoder.encode(yqlQuery, "UTF-8") + "&format=json"
  }

  /* attempt to get weather conditions for specified location */
  def getWeatherConditions(url: String, location: String = "tallahassee, fl"): List[String] = {
    Try(Utils.getContent(url)) match {
      case Success(jsonStr) =>  
        val weatherObject = Utils.parseJsonString[Weather](jsonStr)
        formatResult(weatherObject, location)
      
      case Failure(error) => 
        log.error(s"WeatherMan recieved this error ${error.getMessage}")
        List("Sorry, could not retrieve the weather")
    }
  }

  /* format the retrieved weather result */
  private def formatResult(weatherObject: Option[Weather] = None, location: String): List[String] = {
    def getConditions(weather: Option[Weather]): Option[Condition] = 
      for {
        w <- weather
        q <- w.query
        r <- q.results
        c <- r.channel
        i <- c.item
        c <- i.condition
      } yield c

    val currentConditions: Option[Condition] = getConditions(weatherObject)
    currentConditions match {
      case Some(conditions) =>
        List(
          s"Weather Forecast for ${location}: ",
          s"Current date: ${conditions.date getOrElse ""}",
          s"Current weather condition(s): ${conditions.text getOrElse ""}",
          s"Current temperature (deg): ${conditions.temp getOrElse ""}"
        )

      case None =>
        List("Sorry, could not retrieve informations about that location")
    }
  }

}
