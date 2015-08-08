package models

import org.scalactic.{ Bad, Good }
import parsing.ResponseParser
import play.api.libs.ws.WSAPI

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class BattingStatBuilder(ws: WSAPI, baseUrl: String) extends StatBuilder {
  import StatBuilder._
  import BattingStatBuilder._

  def generateStat() = {
    // mandatory stuff
    val matchClass = random(MatchClass.values)
    val metric = random(Metric.values)

    // optional filters
    val team = Team()
    val opposition = Opposition.not(team.value)
    val homeAway = HomeAway()
    val matchResult = MatchResult()
    val toss = Toss()
    val battingHand = BattingHand()

    val filterUrlParams = Seq(team, opposition, homeAway, matchResult, toss, battingHand).flatMap(_.urlParam).mkString(";")

    val cricinfoUrl = s"$baseUrl;type=batting;class=${matchClass.id};orderby=${metric.id};$filterUrlParams"

    ws.url(cricinfoUrl).get() map { resp =>
      val name = ResponseParser.extractFirstName(resp.body)
      name match {
        case Some(_) =>
          val textParts = Seq(
            name, // KP Pietersen
            Some(metric.text), // has scored most runs
            Some("of all"),
            battingHand.text, // right-handed
            team.text, // England
            matchClass.text, // Test
            Some("batsmen"),
            matchResult.text, // winning
            homeAway.text, // away
            opposition.text, // vs India
            toss.text // after losing the toss
          ).flatten
          val text = textParts.mkString(" ")
          Good(Stat(text, cricinfoUrl))
        case None =>
          Bad(s"Failed to extract player name from Cricinfo response. Cricinfo URL: $cricinfoUrl, response: ${resp.body}")
      }
    }
  }

}

object BattingStatBuilder {

  sealed abstract class MatchClass(val text: Option[String], val id: String)
  object MatchClass {
    case object Test extends MatchClass(Some("Test"), "1")
    case object ODI extends MatchClass(Some("ODI"), "2")
    case object T20I extends MatchClass(Some("T20 intl"), "3")
    case object All extends MatchClass(None, "11")

    val values = Seq(Test, ODI, T20I, All)
  }

  sealed abstract class Metric(val text: String, val id: String)
  object Metric {
    case object Runs extends Metric("has scored most runs", "runs")
    case object Average extends Metric("has the highest average", "batting_average")

    val values = Seq(Runs, Average)
  }

  sealed trait Filter {
    def value: Option[FilterValue]
    def urlParamKey: String

    def text: Option[String] = value.map(_.text)
    def urlParam: Option[String] = value.map(v => s"$urlParamKey=${v.id}")
  }

  object Filter {
    def rnd(values: Seq[FilterValue]): Option[FilterValue] = {
      if (Random.nextDouble() > 0.4) Some(StatBuilder.random(values)) else None
    }
  }

  case class Team(value: Option[FilterValue] = Filter.rnd(FilterValues.team)) extends Filter {
    val urlParamKey = "team"
  }

  case class Opposition(value: Option[FilterValue] = Filter.rnd(FilterValues.team)) extends Filter {
    val urlParamKey = "opposition"
    override def text = value.map("vs " + _.text)
  }

  object Opposition {
    def not(team: Option[FilterValue]): Opposition = {
      val validTeams = team match {
        case Some(t) => FilterValues.team.filterNot(_ == t)
        case None => FilterValues.team
      }
      Opposition(value = Filter.rnd(validTeams))
    }
  }

  case class HomeAway(value: Option[FilterValue] = Filter.rnd(FilterValues.homeAway)) extends Filter {
    val urlParamKey = "home_or_away"
  }

  case class MatchResult(value: Option[FilterValue] = Filter.rnd(FilterValues.matchResult)) extends Filter {
    val urlParamKey = "result"
  }

  case class Toss(value: Option[FilterValue] = Filter.rnd(FilterValues.toss)) extends Filter {
    val urlParamKey = "toss"
  }

  case class BattingHand(value: Option[FilterValue] = Filter.rnd(FilterValues.battingHand)) extends Filter {
    val urlParamKey = "batting_hand"
  }

  case class FilterValue(text: String, id: String)

  object FilterValues {

    val team = Seq(
      FilterValue("Australia", "2"),
      FilterValue("Bangladesh", "25"),
      FilterValue("England", "1"),
      FilterValue("ICC World XI", "140"),
      FilterValue("India", "6"),
      FilterValue("New Zealand", "5"),
      FilterValue("Pakistan", "7"),
      FilterValue("South Africa", "3"),
      FilterValue("Sri Lanka", "8"),
      FilterValue("West Indies", "4"),
      FilterValue("Zimbabwe", "9")
    )

    val homeAway = Seq(
      FilterValue("at home", "1"),
      FilterValue("away", "2")
    )

    val matchResult = Seq(
      FilterValue("winning", "1"),
      FilterValue("losing", "2"),
      FilterValue("drawing", "4")
    )

    val toss = Seq(
      FilterValue("after winning the toss", "1"),
      FilterValue("after losing the toss", "2")
    )

    val battingHand = Seq(
      FilterValue("right-handed", "1"),
      FilterValue("left-handed", "2")
    )
  }

}

