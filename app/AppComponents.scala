import controllers._
import services._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ning.NingWSComponents
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import router.Routes

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with NingWSComponents {

  val statsService = new StatsService(wsApi, configuration.getString("cricinfo.baseUrl").getOrElse(sys.error("Missing Cricinfo URL")))
  val tweetService = new TweetService(statsService)
  val appController = new Application(statsService, tweetService, configuration.getString("tweet.apiKey").getOrElse(sys.error("Missing tweet API key")))

  val router: Router = new Routes(httpErrorHandler, appController)

}
