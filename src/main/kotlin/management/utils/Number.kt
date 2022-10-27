package management.utils

import kotlin.math.abs

fun Number.morph(f1: String, f2: String, f5: String = f2): String {
    val n: Int = abs(this.toInt()) % 100

    if(n in 11..19) return f5

    return when(n % 10) {
        1 -> f1
        in 2..4 -> f2
        else -> f5
    }
}