package com.scalassistant

import akka.actor.{ ActorRef, ActorSystem, Props, Actor }       /* Necessary akka libraries */
import akka.event.Logging                                       /* Enables Akka logging     */
import scala.concurrent.{ ExecutionContext, Promise, Future }   /* Allows use of Promises and Futures  */
import concurrent.ExecutionContext.Implicits.global             /* Allows use of an implicit execution context */
import akka.util.Timeout                                        /* To use a Timeout duration on ask */
import akka.pattern.ask                                         /* Enables asking with actors   */
import scala.concurrent.Await                                   /* Await result from ask */
import scala.concurrent.duration._                              /* Allows waiting for a duration of time */
import scala.language.postfixOps                                /* so compiler wont complain about 'seconds' */
import java.util.Scanner                                        /* Enables use of Scanner */

object Main extends App {
  /* create the actor system */
  implicit val system = ActorSystem()

  /* create an implicit timeout for ask */
  implicit val timeout = Timeout(20 seconds)

  /* create actors in our system */
  val monitor     = system.actorOf(Props[Monitor]   , name = "monitor")
  val greeter     = system.actorOf(Props[Greeter]   , name = "greeter")
  val timeKeeper  = system.actorOf(Props[TimeKeeper], name = "timeKeeper")
  val weatherman  = system.actorOf(Props[Weatherman], name = "weatherman")
  val helper      = system.actorOf(Props[Helper],    name = "helper")

  /* print a greeting on app startup */
  println("Hello, I am ScalAssistant, a command line assistant written in Scala!")
  println("I am still in testing, so my functionality is limited.")
  println("If you are a new user and unfamiliar with my interface, try using the \"help\" command.")

  /* create a scanner for getting input */
  val scanner = new Scanner(System.in)

  var flag: Boolean = false

  /* continuously loop, printing a prompt, accepting input, then send it to be processed by other actors. WAITS FOR A RESPONSE*/
  while (flag == false) {
  	print("\nScalAssistant: ")                                                     
  	val input = scanner.nextLine                                                 
    val future = monitor ? ConsoleMessage(input)
    val result = Await.result(future, timeout.duration).asInstanceOf[Response]
    if (result.msg == "terminate") flag = true
  }

  system.terminate
}