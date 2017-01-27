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

      it("from a valid date for an unknown tick symbol") {
        val tickSymbol   = TickSymbol("ZZZZ")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(List.empty))

        val results = stockValuationService.dailyPrices(businessDate, tickSymbol).unsafePerformSync
        results shouldBe 'empty
      }
    }

    describe("should calculate daily returns") {
      it("from a valid date for a known tick symbol") {
        val tickSymbol   = TickSymbol("GOOG")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(testDataSet))

        val results = stockValuationService.dailyReturns(businessDate, tickSymbol).unsafePerformSync
        results should have size 230
        results.head should ===(BigDecimal("-0.00421214004") +- 0.00000000001)
        results.last should ===(BigDecimal("0.03015316995") +- 0.00000000001)
      }

      it("from a valid date for an unknown tick symbol") {
        val tickSymbol   = TickSymbol("ZZZZ")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(List.empty))

        val results = stockValuationService.dailyReturns(businessDate, tickSymbol).unsafePerformSync
        results shouldBe 'empty
      }
    }
  }

}
