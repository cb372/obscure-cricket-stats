package models

import play.api.libs.json.Json

case class Stat(text: String, url: String)

object Stat {
  implicit val writes = Json.writes[Stat]
}
