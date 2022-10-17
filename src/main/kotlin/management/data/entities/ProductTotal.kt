package management.data.entities

import java.math.BigDecimal

data class ProductTotal(
    val price: BigDecimal,
    val priceFormatted: String,
    val quantity: Short,
    val quantityFormatted: String,
    val cost: BigDecimal,
    val costFormatted: String,
    val tax: BigDecimal,
    val taxFormatted: String,
    val taxSum: BigDecimal,
    val taxSumFormatted: String,
    val subTotal: BigDecimal,
    val taxTotal: BigDecimal,
    val taxTotalFormatted: String,
    val subTotalFormatted: String,
    val total: BigDecimal,
    val totalFormatted: String,
    val totalText: String
)
