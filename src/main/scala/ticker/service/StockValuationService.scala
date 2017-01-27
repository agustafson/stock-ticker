package ticker.service

import java.time.LocalDate

import ticker.model.TickSymbol

import scalaz.concurrent.Task

class StockValuationService(stockTickerService: StockTickerService) {
  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[BigDecimal]] =
    stockTickerService.dailyPrices(businessDate, ticker).map(_.map(_.close))
}
