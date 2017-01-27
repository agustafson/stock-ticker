package ticker.model

import java.time.LocalDate

import scala.io.Source

case class StockTick(date: LocalDate,
                     open: BigDecimal,
                     high: BigDecimal,
                     low: BigDecimal,
                     close: BigDecimal,
                     volume: Int,
                     adjClose: BigDecimal)

object StockTick {
  def parseCsvContents(csvResults: String): List[StockTick] = {
    def stockTickFromArray(arr: Array[String]): StockTick = {
      StockTick(LocalDate.parse(arr(0)),
                BigDecimal(arr(1)),
                BigDecimal(arr(2)),
                BigDecimal(arr(3)),
                BigDecimal(arr(4)),
                arr(5).toInt,
                BigDecimal(arr(6)))
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
