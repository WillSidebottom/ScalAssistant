package com.scalassistant

import java.net.{ URL, HttpURLConnection }
import scala.util.{ Try, Success, Failure }
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Utils {
  /* Use this function for getting content from a RESTful URL */
  def getContent(url: String, connectTimeout: Int = 10000, readTimeout: Int = 10000, requestMethod: String = "GET") = {
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
  }

  /* Will take a List of phrase regexes to matches against a certain phrase */
  def matchesPhrase(listOfPhrases: List[String], phrase: String): Boolean = {
    listOfPhrases.exists(p => phrase.toLowerCase.matches(p))
  }

  /* Generic json parser which accepts a class or case class to contain the parsed json string as a Manifest */
  def parseJsonString[Container : Manifest](jsonStr: String): Option[Container] = {
    implicit val formats = DefaultFormats
    val json = parse(jsonStr)
    Try(json.extract[Container]) match {
      case Success(filledContainer) =>
        Some(filledContainer)
      case Failure(error) =>
        None
    }
  }

  /* Retrieves a random element from a sequence */
  def getRandomElement[A](list: Seq[A]): A = {
    val r = new scala.util.Random 
    val i = r.nextInt(list.size)
    list(i)
  }
}