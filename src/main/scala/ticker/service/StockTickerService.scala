package ticker.service

import java.time.LocalDate

import org.http4s.Method.GET
import org.http4s.Status.NotFound
import org.http4s.Status.ResponseClass.Successful
import org.http4s.client.Client
import org.http4s.{Request, Uri}
import ticker.model.{StockTick, TickSymbol}

import scalaz.concurrent.Task

trait StockTickerService {
  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[StockTick]]
}

class YahooStockTickerService(httpClient: Client, baseUri: Uri) extends StockTickerService {

  /*
   * Details for url structure are listed under "Historical data" at
   * [[http://meumobi.github.io/stocks%20apis/2016/03/13/get-realtime-stock-quotes-yahoo-finance-api.html]]
   * Months are zero-based indices as per documentation.
   */
  private def pricesURL(businessDate: LocalDate, ticker: TickSymbol): Uri = {
    val lastYear = businessDate.minusYears(1)
    (baseUri / "table.csv")
      .withQueryParam("s", ticker.symbol)
      .withQueryParam("a", lastYear.getMonthValue - 1)
      .withQueryParam("b", lastYear.getDayOfMonth)
      .withQueryParam("c", lastYear.getYear)
      .withQueryParam("d", businessDate.getMonthValue - 1)
      .withQueryParam("e", businessDate.getDayOfMonth)
      .withQueryParam("f", businessDate.getYear)
      .withQueryParam("g", "d")
      .withQueryParam("ignore", ".csv")
  }

  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[StockTick]] =
    httpClient.fetch(Request(GET, pricesURL(businessDate, ticker))) {
      case Successful(response) =>
        response.as[String].map(StockTick.parseCsvContents)
      case NotFound(response) =>
        Task.now(List.empty)
    }

}
