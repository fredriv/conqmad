package no.knowit.concurrency.actor.account

import scala.actors._
import scala.actors.Actor._
import scala.collection.mutable.Map

case object AccountNotAvailable
case class GiveMeAccounts(accountsNumbers: List[Int])
case class ReturnAccounts(accounts : List[Account])

class AccountRepository extends Actor {

  var accounts = Map(1 -> new Account(1, 200),
                     2 -> new Account(2, 350))
  
  def act() {
    loop {
      react {
        case GiveMeAccounts(accountNumbers) =>
          println("Getting accounts " + accountNumbers)
          if (accountNumbers.exists(n => !accounts.contains(n))) {
            sender ! AccountNotAvailable
          } else {
            sender ! accountNumbers.map(n => accounts.removeKey(n) match {
              case Some(account) => account
              case None => error("This should never happen!")
            })
          }
        case ReturnAccounts(accts) => 
          accts foreach { a => accounts += (a.nr -> a) }
      }
    }
  }
}

object Main {
  def main(args : Array[String]) {
    var nrs = List(1, 2)
    
    val mgr = new AccountRepository
    mgr.start
    
    mgr ! new GiveMeAccounts(nrs)
    self.receive {
      case s: List[Account] => println(s)
      case AccountNotAvailable => println("Could not get accounts!")
    }
    mgr ! new GiveMeAccounts(nrs)
    self.receive {
      case s => println(s)
    }
  }
}