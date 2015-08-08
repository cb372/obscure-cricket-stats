package services

import models._
import org.scalactic.Or
import play.api.libs.ws._

import scala.concurrent.Future
import scala.util.Random

class StatsService(ws: WSAPI, baseUrl: String) {

  val builders: Seq[StatBuilder] = Seq(
    new BattingStatBuilder(ws, baseUrl)
  // TODO bowling, team stats
  )

  def randomStat(): Future[Stat Or String] = Random.shuffle(builders).head.generateStat()

}
