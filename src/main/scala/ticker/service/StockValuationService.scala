package ticker.service

import java.time.LocalDate

import ticker.model.TickSymbol

import scalaz.concurrent.Task

class StockValuationService(stockTickerService: StockTickerService) {
  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[BigDecimal]] =
    stockTickerService.dailyPrices(businessDate, ticker).map(_.map(_.close))

  def dailyReturns(businessDate: LocalDate, ticker: TickSymbol): Task[List[BigDecimal]] =
    stockTickerService.dailyPrices(businessDate, ticker).map {
      _.sliding(2).map {
        case List(tickCurrent, tickPrevious) =>
          (tickCurrent.close - tickPrevious.close) / tickPrevious.close
      }.toList
    }

  def meanAnnualReturn(businessDate: LocalDate, ticker: TickSymbol): Task[Option[BigDecimal]] =
    dailyReturns(businessDate, ticker).map { returns =>
      Some(returns.size).filterNot(_ == 0).map(returns.sum / _)
    }
}