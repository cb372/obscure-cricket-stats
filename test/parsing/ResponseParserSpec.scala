package parsing

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.scalatest.{ FlatSpec, Matchers }

class ResponseParserSpec extends FlatSpec with Matchers {

  it should "extract a player name from a Cricinfo response" in {
    val html = Resources.toString(Resources.getResource("cricinfo-response-batting.html"), Charsets.UTF_8)
    ResponseParser.extractFirstName(html) should be(Some("GA Gooch"))
  }

}
