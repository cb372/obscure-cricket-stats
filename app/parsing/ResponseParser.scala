package parsing

import org.jsoup.Jsoup
import scala.collection.JavaConverters._

object ResponseParser {

  def extractFirstName(html: String): Option[String] = {
    val doc = Jsoup.parse(html)
    val tables = doc.body().select("table.engineTable").asScala
    val firstCellText = tables.collectFirst {
      case tableWithCaption if !tableWithCaption.getElementsByTag("caption").isEmpty =>
        tableWithCaption.select("td").first.text.trim
    }
    firstCellText.filterNot(_.startsWith("No records available"))
  }

}
