package management.utils

import pl.allegro.finance.tradukisto.MoneyConverters
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.asWords(): String {
    val converter = MoneyConverters.RUSSIAN_BANKING_MONEY_VALUE

    val rem = ((this.rem(BigDecimal.ONE)) * 100.toBigDecimal()).toInt()
    val strRem = if(rem == 0) {
        "00"
    } else {
        rem.toString()
    }
    return "${converter.asWords(this).substringBefore("руб")} бел. руб. $strRem коп."
}

fun BigDecimal.toFixed(precision: Int=2): BigDecimal {
    return this.setScale(precision, RoundingMode.HALF_UP)
}