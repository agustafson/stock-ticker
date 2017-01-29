package ticker

import java.time.LocalDate

import org.http4s.Uri
import org.http4s.client.blaze.SimpleHttp1Client
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSpec, Inspectors, Matchers}
import ticker.model.TickSymbol
import ticker.service.YahooStockTickerService

class YahooStockTickerServiceIT extends FunSpec with Matchers with Inspectors with TypeCheckedTripleEquals {
  describe("the live yahoo ticker service") {
    it("should return the correct date range") {
      val client = SimpleHttp1Client()
      val stockTickerService =
        new YahooStockTickerService(client, Uri.uri("http://real-chart.finance.yahoo.com/"))

      val dates = Seq(LocalDate.of(2017, 1, 26), LocalDate.of(2016, 12, 1), LocalDate.of(2016, 6, 15))

      forAll(dates) { date =>
        val results = stockTickerService.dailyPrices(date, TickSymbol("GOOG")).unsafePerformSync
        results.head.date should ===(date)
        results.last.date should ===(date.minusYears(1))
      }
    }
  }
}
