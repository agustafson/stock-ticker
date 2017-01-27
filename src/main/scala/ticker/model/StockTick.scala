package ticker.model

import java.time.LocalDate

case class StockTick(date: LocalDate,
                     open: BigDecimal,
                     high: BigDecimal,
                     low: BigDecimal,
                     close: BigDecimal,
                     volume: Int,
                     adjClose: BigDecimal)
