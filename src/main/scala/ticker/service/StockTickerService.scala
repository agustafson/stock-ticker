package ticker.service

import java.time.LocalDate

import org.http4s.Uri
import org.http4s.client.Client
import ticker.model.{StockTick, TickSymbol}

import scala.io.Source
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

  private def stockTickFromArray(arr: Array[String]): StockTick = {
    StockTick(LocalDate.parse(arr(0)),
              BigDecimal(arr(1)),
              BigDecimal(arr(2)),
              BigDecimal(arr(3)),
              BigDecimal(arr(4)),
              arr(5).toInt,
              BigDecimal(arr(6)))
  }

  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[StockTick]] = {
    httpClient.expect[String](pricesURL(businessDate, ticker)).map { csvResults =>
      Source
        .fromString(csvResults)
        .getLines()
        .drop(1)
        .map { line =>
          stockTickFromArray(line.split(','))
        }
        .toList
    }
  }
}
