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
            .uponReceiving(GET, "/table.csv", query = Some("s=GOOG&a=0&b=27&c=2016&d=0&e=27&f=2017&g=d&ignore=.csv"))
            .willRespondWith(200, csvResults)
        )
        .runConsumerTest { config =>
          val client = SimpleHttp1Client()
          val stockTickerService =
            new YahooStockTickerService(client, Uri.fromString(config.baseUrl).valueOr(throw _))

          val results = stockTickerService.dailyPrices(LocalDate.of(2017, 1, 27), TickSymbol("GOOG")).unsafePerformSync
          results should have size 254
          results.head should ===(
            StockTick(LocalDate.of(2017, 1, 27), 834.710022, 841.950012, 820.440002, 823.309998, 2951800, 823.309998))
          results.last should ===(
            StockTick(LocalDate.of(2016, 1, 27), 713.669983, 718.234985, 694.390015, 699.98999, 2194200, 699.98999))
        }
    }

    it("should return an empty list for an unknown tick symbol on a date in the past") {
      forgePact
        .between(consumer)
        .and(provider)
        .addInteraction(
          interaction
            .description("a simple get example")
            .uponReceiving(GET, "/table.csv", query = Some("s=ZZZZ&a=0&b=27&c=2016&d=0&e=27&f=2017&g=d&ignore=.csv"))
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
