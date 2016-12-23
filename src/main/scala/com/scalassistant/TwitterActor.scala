package com.scalassistant

import akka.actor.{ Actor, ActorLogging }
import scala.io.Source
import twitter4j.TwitterFactory
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder
import twitter4j.Query
import scala.collection.JavaConversions._   /* convert a java list to a scala list */

object TwitterActor {
  val phrasesWeRespondTo = List (
    "twitter post.*",
    "twitter home timeline",
    "twitter user timeline.*",
    "twitter search tweets.*"
  )

  val validTwitterPhrases = List (
    "Want to tweet? Here are some twitter methods I can handle:",
    "twitter post <your_tweet_goes_here>",
    "twitter home timeline",
    "twitter user timeline <@user>",
    "twitter search tweets <person, place, trend, etc>"
  )

  val twitterPattern = "twitter".r
  val postPattern = "(post)(.*)".r
  val homeTimelinePattern = "(home timeline)".r
  val userTimelinePattern = "(user timeline)(.*)".r
  val searchTweetsPattern = "(search tweets)(.*)".r
}

class TwitterActor extends Actor with ActorLogging {
  import TwitterActor._

  val twitter = getTwitterInstance

  def receive = {
    case MatchPhrase(phrase) =>
      val handled = Utils.matchesPhrase(phrasesWeRespondTo, phrase)
      log.info(s"TwitterActor: handled phrase = ${handled}")
      if (handled) {
        log.info("TwitterActor: attempting to process twitter phrase")
        val message = processPhrase(phrase)
        message.foreach(println)
      }
      sender ! PhraseHandled(handled)

    case unknown =>
      log.info("TwitterActor: received an unknown message " + unknown)
  }

  def processPhrase(phrase: String): List[String] = {
    val remainder = twitterPattern.replaceAllIn(phrase, "").trim
    remainder match {
      case postPattern(post, msg)         => postStatus(msg.trim)
      case homeTimelinePattern(home)      => getHomeTimeline
      case userTimelinePattern(ut, user)  => getUserTimeline(user.trim)
      case searchTweetsPattern(st, tweet) => searchTweets(tweet)
      case _                              => List("Sorry, that was an incorrect twitter method") 
    }
  }

  def postStatus(msg: String): List[String]= {
    val status = twitter.updateStatus(msg)
    List(s"Successfully updated the status to [ ${status.getText()} ].")
  }

  def getHomeTimeline: List[String] = {
    val statuses = twitter.getHomeTimeline.toList
    statuses map(status => status.getUser.getName + ":" + status.getText)
  }

  def getUserTimeline(user: String): List[String] = {
    val statuses = twitter.getUserTimeline(user).toList
    statuses map(status => status.getUser.getName + ":" + status.getText)
  }

  def searchTweets(tweet: String): List[String] = {
    val query = new Query(tweet)
    val result = twitter.search(query)
    val tweets = result.getTweets.toList
    tweets map (status => "@" + status.getUser.getScreenName + ":" + status.getText)
  }

  def getTwitterInstance = {
    val cb = new ConfigurationBuilder
    val source = Source.fromFile("src/main/resources/twitter.txt")
    val lines = source.getLines.toList
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(lines(0).split(" ")(1))
      .setOAuthConsumerSecret(lines(1).split(" ")(1))
      .setOAuthAccessToken(lines(2).split(" ")(1))
      .setOAuthAccessTokenSecret(lines(3).split(" ")(1))
    source.close
    val tf = new TwitterFactory(cb.build)
    tf.getInstance
  }
}