package ticker.service

import java.time.LocalDate

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSpec, Inspectors, Matchers}
import org.scalamock.scalatest.MockFactory
import ticker.model.{StockTick, TickSymbol}

import scala.io.Source
import scalaz.concurrent.Task

class StockValuationServiceTest
    extends FunSpec
    with Matchers
    with Inspectors
    with MockFactory
    with TypeCheckedTripleEquals {

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
        results should have size 254
        results.head.getOrElse(fail("No first result")) should ===(BigDecimal("823.309998"))
        results.last.getOrElse(fail("No last result")) should ===(BigDecimal("699.98999"))
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
        results should have size 253
        forAll(results)(_.isRight)
        results.head.getOrElse(fail("No first result")) should ===(BigDecimal("-0.010623116") +- 0.000000001)
        results.last.getOrElse(fail("No last result")) should ===(BigDecimal("0.044243536") +- 0.000000001)
      }

      it("from a valid date for an unknown tick symbol") {
        val tickSymbol   = TickSymbol("ZZZZ")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(List.empty))

        val results = stockValuationService.dailyReturns(businessDate, tickSymbol).unsafePerformSync
        results shouldBe 'empty
      }
    }

    describe("should calculate the 1-year mean return") {
      it("from a valid date for a known tick symbol") {
        val tickSymbol   = TickSymbol("GOOG")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(testDataSet))

        val result = stockValuationService.meanAnnualReturn(businessDate, tickSymbol).unsafePerformSync
        result shouldBe 'defined
        result.get.getOrElse(fail("Could not find result")) should ===(BigDecimal("0.000711926") +- 0.00000001)
      }

      it("from a valid date for an unknown tick symbol") {
        val tickSymbol   = TickSymbol("ZZZZ")
        val businessDate = LocalDate.of(2017, 1, 27)
        (stockTickerService.dailyPrices _).when(businessDate, tickSymbol).returns(Task.now(List.empty))

        val result = stockValuationService.meanAnnualReturn(businessDate, tickSymbol).unsafePerformSync
        result should ===(None)
      }
    }
  }

}
