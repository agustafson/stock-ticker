package ticker.model

import java.time.LocalDate

import cats.syntax.either._

import scala.io.Source
import scala.util.Try

case class StockTick(date: LocalDate,
                     open: BigDecimal,
                     high: BigDecimal,
                     low: BigDecimal,
                     close: BigDecimal,
                     volume: Int,
                     adjClose: BigDecimal)

object StockTick {

  def parseCsvContents(csvResults: String): List[Either[StockTickParseError, StockTick]] = {
    def stockTickFromArray(row: Array[String]): Either[StockTickParseError, StockTick] = {
      Try {
        StockTick(LocalDate.parse(row(0)),
                  BigDecimal(row(1)),
                  BigDecimal(row(2)),
                  BigDecimal(row(3)),
                  BigDecimal(row(4)),
                  row(5).toInt,
                  BigDecimal(row(6)))
      }.toEither.leftMap(StockTickParseError(row, _))
    }

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

case class StockTickParseError(row: Array[String], exception: Throwable)
