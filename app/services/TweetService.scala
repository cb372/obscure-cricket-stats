package services

import models.Stat
import org.scalactic.{ Bad, Good, Or }
import twitter4j.TwitterFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TweetService(statsService: StatsService) {

  val twitter = TwitterFactory.getSingleton

  val twitterShortUrlLength = twitter.help.getAPIConfiguration.getShortURLLengthHttps

  /**
   * Attempts to generate a random stat and tweet it.
   *
   * If it fails 5 times (either because the stat is too long or the stat generation failed for some other reason)
   * then it gives up.
   *
   * If stat generation resulted in a `Failure` then something really bad probably happened,
   * so it won't retry.
   *
   * @return
   */
  def tweetRandomStat(): Future[Stat Or String] = {
    def tweetRandomStat(attemptsLeft: Int, lastStat: Option[Stat Or String] = None): Future[Stat Or String] = {
      if (attemptsLeft == 0)
        Future.successful(Bad(s"Failed to send a tweet. Last result was: $lastStat"))
      else {
        val fStat = statsService.randomStat()
        fStat flatMap {
          case good @ Good(stat) if canTweet(stat) =>
            twitter.updateStatus(s"${stat.text} ${stat.url}")
            Future.successful(good)
          case untweetable => tweetRandomStat(attemptsLeft - 1, Some(untweetable))
        }
      }
    }

    tweetRandomStat(attemptsLeft = 5)
  }

  private def canTweet(stat: Stat) =
    stat.text.length + 1 + twitterShortUrlLength <= 140

}
