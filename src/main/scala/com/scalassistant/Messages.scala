package com.scalassistant

/* list of messages to use for our app */
case class Response(msg: String)
case class ConsoleMessage(msg: String)
case class MatchPhrase(phrase: String)
case class PhraseHandled(handled: Boolean)