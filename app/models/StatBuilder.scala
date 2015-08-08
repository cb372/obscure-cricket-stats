package models

import org.scalactic.Or

import scala.concurrent.Future
import scala.util.Random

trait StatBuilder {

  def generateStat(): Future[Stat Or String]

}

object StatBuilder {

  def random[A](values: Seq[A]): A = Random.shuffle(values).head

}

