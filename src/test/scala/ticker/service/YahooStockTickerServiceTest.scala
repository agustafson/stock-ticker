package ticker.service

import java.time.LocalDate

import com.itv.scalapact.ScalaPactForger._
import org.http4s.Uri
import org.http4s.client.blaze.SimpleHttp1Client
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSpec, Matchers}
import ticker.model.{StockTick, TickSymbol}

class YahooStockTickerServiceTest extends FunSpec with Matchers with TypeCheckedTripleEquals {

  val consumer = "Ticker Service"
  val provider = "Yahoo stock service"

  describe("the yahoo stock ticker service") {
    it("should return a list of results for a known tick symbol on date in the past") {
      val csvResults = io.Source.fromResource("csv_results.csv").mkString
      forgePact
        .between(consumer)
        .and(provider)
        .addInteraction(
          interaction
            .description("a simple get example")
            .uponReceiving(GET, "/table.csv", query = Some("s=GOOG&a=1&b=27&c=2016&d=1&e=27&f=2017&g=d&ignore=.csv"))
            .willRespondWith(200, csvResults)
        )
        .runConsumerTest { config =>
          val client = SimpleHttp1Client()
          val stockTickerService =
            new YahooStockTickerService(client, Uri.fromString(config.baseUrl).valueOr(throw _))

          val results = stockTickerService.dailyPrices(LocalDate.of(2017, 1, 27), TickSymbol("GOOG")).unsafePerformSync
          results should have size 231
          results.head should ===(
            StockTick(LocalDate.of(2017, 1, 26), 837.809998, 838.00, 827.01001, 832.150024, 2734400, 832.150024))
          results.last should ===(
            StockTick(LocalDate.of(2016, 2, 29), 700.320007, 710.890015, 697.679993, 697.77002, 2481100, 697.77002))
        }
    }

    it("should return an empty list for an unknown tick symbol on a date in the past") {
      forgePact
        .between(consumer)
        .and(provider)
        .addInteraction(
          interaction
            .description("a simple get example")
            .uponReceiving(GET, "/table.csv", query = Some("s=ZZZZ&a=1&b=27&c=2016&d=1&e=27&f=2017&g=d&ignore=.csv"))
            .willRespondWith(404)
        )
        .runConsumerTest { config =>
          val client = SimpleHttp1Client()
          val stockTickerService =
            new YahooStockTickerService(client, Uri.fromString(config.baseUrl).valueOr(throw _))

          val results = stockTickerService.dailyPrices(LocalDate.of(2017, 1, 27), TickSymbol("GOOG")).unsafePerformSync
          results shouldBe 'empty
        }
    }
  }

}
