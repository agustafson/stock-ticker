package ticker.service

import java.time.LocalDate

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSpec, Matchers}
import org.scalamock.scalatest.MockFactory
import ticker.model.{StockTick, TickSymbol}

import scala.io.Source
import scalaz.concurrent.Task

class StockValuationServiceTest extends FunSpec with Matchers with MockFactory with TypeCheckedTripleEquals {

  val stockTickerService    = stub[StockTickerService]
  val stockValuationService = new StockValuationService(stockTickerService)

  val testDataSet = StockTick.parseCsvContents(Source.fromResource("csv_results.csv").mkString)

  describe("stock valuation service") {
    describe("should list daily prices") {
      it("from a valid date for a known tick symbol") {
        val tickSymbol   = TickSymbol("GOOG")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(testDataSet))

        val results = stockValuationService.dailyPrices(businessDate, tickSymbol).unsafePerformSync
        results should have size 231
        results.head should ===(BigDecimal("832.150024"))
        results.last should ===(BigDecimal("697.77002"))
      }
    }
  }

}
