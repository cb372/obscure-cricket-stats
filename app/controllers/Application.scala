package controllers

import org.scalactic.{ Bad, Good }
import play.api.mvc._
import play.api.libs.json.Json

import services._

import scala.concurrent.ExecutionContext.Implicits.global

class Application(statsService: StatsService) extends Controller {

  def randomStat = Action.async {
    statsService.randomStat() map {
      case Good(stat) => Ok(Json.toJson(stat))
      case Bad(errorMsg) => InternalServerError(errorMsg)
    }
  }

  def tweet = TODO

}
