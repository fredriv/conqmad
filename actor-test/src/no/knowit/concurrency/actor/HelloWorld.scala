package no.knowit.concurrency.actor

import scala.actors._
import scala.actors.Actor._

case object Stop

case class HelloWorldMessage(name: String, age: Int) {
  def this(name: String) = this(name, 0)
}

class HelloWorld extends Actor {
  def act() {
    println("Acting")
    loop {
      react {
        case HelloWorldMessage(foo, bar) =>
          actor {
            Thread.sleep(100)
            println("Hello, " + foo + " - you are " + bar + " years old.")
          }
          println("Helper started")
        case Stop =>
          println("Stopping")
          exit
      }
    }
  }
}

object Main {
  def main(args : Array[String]) {
    val helloActor = new HelloWorld
    println("Starting actor")
    helloActor.start
    Thread.sleep(100)
    println("Sending greeting")
    helloActor ! new HelloWorldMessage("Alf") 
    helloActor ! new HelloWorldMessage("Fredrik", 32) 
    helloActor ! Stop
    helloActor ! new HelloWorldMessage("Fredrik", 32) 
  }
}
