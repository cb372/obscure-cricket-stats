package controllers

import org.scalactic.{ Bad, Good }
import play.api.mvc._
import play.api.libs.json.Json

import services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application(statsService: StatsService, tweetService: TweetService, tweetApiKey: String) extends Controller {

  def randomStat = Action.async {
    statsService.randomStat() map {
      case Good(stat) => Ok(Json.toJson(stat))
      case Bad(errorMsg) => InternalServerError(errorMsg)
    }
  }

  def tweet(apiKey: String) = Action.async {
    if (apiKey == tweetApiKey) {
      tweetService.tweetRandomStat() map {
        case Good(stat) => Ok(Json.toJson(stat))
        case Bad(errorMsg) => InternalServerError(s"Failed to tweet. $errorMsg")
      }
    } else Future.successful(Unauthorized("Invalid API key"))
  }

}
