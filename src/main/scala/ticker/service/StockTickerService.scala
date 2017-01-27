package ticker.service

import java.time.LocalDate

import org.http4s.Uri
import org.http4s.client.Client
import ticker.model.{StockTick, TickSymbol}

import scalaz.concurrent.Task

trait StockTickerService {
  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[StockTick]]
}

class YahooStockTickerService(httpClient: Client, baseUri: Uri) extends StockTickerService {

  private def pricesURL(businessDate: LocalDate, ticker: TickSymbol): Uri = {
    val lastYear = businessDate.minusYears(1)
    baseUri
      .withQueryParam("s", ticker.symbol)
      .withQueryParam("a", lastYear.getMonthValue)
      .withQueryParam("b", lastYear.getDayOfMonth)
      .withQueryParam("c", lastYear.getYear)
      .withQueryParam("d", businessDate.getMonthValue)
      .withQueryParam("e", businessDate.getDayOfMonth)
      .withQueryParam("f", businessDate.getYear)
      .withQueryParam("g", "d")
      .withQueryParam("ignore", ".csv")
  }

  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[StockTick]] =
    httpClient.expect[String](pricesURL(businessDate, ticker)).map(StockTick.parseCsvContents)

}
