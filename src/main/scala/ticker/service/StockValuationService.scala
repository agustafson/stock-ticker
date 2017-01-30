package ticker.service

import java.time.LocalDate

import cats.data.ValidatedNel
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.traverse._
import ticker.model.{StockTickParseError, TickSymbol}

import scalaz.concurrent.Task

class StockValuationService(stockTickerService: StockTickerService) {
  def dailyPrices(businessDate: LocalDate, ticker: TickSymbol): Task[List[Either[StockTickParseError, BigDecimal]]] =
    stockTickerService.dailyPrices(businessDate, ticker).map(_.map(_.map(_.close)))

  def dailyReturns(businessDate: LocalDate, ticker: TickSymbol): Task[List[Either[StockTickParseError, BigDecimal]]] =
    stockTickerService.dailyPrices(businessDate, ticker).map {
      _.sliding(2).map {
        case List(Right(tickCurrent), Right(tickPrevious)) =>
          Right((tickCurrent.close - tickPrevious.close) / tickPrevious.close)
//        case List(Left(error), _) =>
//          Left(error)
//        case List(_, Left(error)) =>
//          Left(error)
      }.toList
    }

  def meanAnnualReturn(businessDate: LocalDate,
                       ticker: TickSymbol): Task[Option[ValidatedNel[StockTickParseError, BigDecimal]]] =
    dailyReturns(businessDate, ticker).map(_.map(_.toValidatedNel) match {
      case Nil => None
      case results =>
        Some(results.sequenceU.map { returns =>
          returns.sum / returns.size
        })
    })
}
