package management.utils

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import java.io.ByteArrayOutputStream
import java.util.*

fun String.toRFC5987(): String {
    val stringBytes: ByteArray = this.toByteArray(Charsets.UTF_8)
    val len = stringBytes.size
    val sb = StringBuilder(len shl 1)
    val digits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val attr_char = byteArrayOf(
        '!'.code.toByte(),
        '#'.code.toByte(),
        '$'.code.toByte(),
        '&'.code.toByte(),
        '+'.code.toByte(),
        '-'.code.toByte(),
        '.'.code.toByte(),
        '0'.code.toByte(),
        '1'.code.toByte(),
        '2'.code.toByte(),
        '3'.code.toByte(),
        '4'.code.toByte(),
        '5'.code.toByte(),
        '6'.code.toByte(),
        '7'.code.toByte(),
        '8'.code.toByte(),
        '9'.code.toByte(),
        'A'.code.toByte(),
        'B'.code.toByte(),
        'C'.code.toByte(),
        'D'.code.toByte(),
        'E'.code.toByte(),
        'F'.code.toByte(),
        'G'.code.toByte(),
        'H'.code.toByte(),
        'I'.code.toByte(),
        'J'.code.toByte(),
        'K'.code.toByte(),
        'L'.code.toByte(),
        'M'.code.toByte(),
        'N'.code.toByte(),
        'O'.code.toByte(),
        'P'.code.toByte(),
        'Q'.code.toByte(),
        'R'.code.toByte(),
        'S'.code.toByte(),
        'T'.code.toByte(),
        'U'.code.toByte(),
        'V'.code.toByte(),
        'W'.code.toByte(),
        'X'.code.toByte(),
        'Y'.code.toByte(),
        'Z'.code.toByte(),
        '^'.code.toByte(),
        '_'.code.toByte(),
        '`'.code.toByte(),
        'a'.code.toByte(),
        'b'.code.toByte(),
        'c'.code.toByte(),
        'd'.code.toByte(),
        'e'.code.toByte(),
        'f'.code.toByte(),
        'g'.code.toByte(),
        'h'.code.toByte(),
        'i'.code.toByte(),
        'j'.code.toByte(),
        'k'.code.toByte(),
        'l'.code.toByte(),
        'm'.code.toByte(),
        'n'.code.toByte(),
        'o'.code.toByte(),
        'p'.code.toByte(),
        'q'.code.toByte(),
        'r'.code.toByte(),
        's'.code.toByte(),
        't'.code.toByte(),
        'u'.code.toByte(),
        'v'.code.toByte(),
        'w'.code.toByte(),
        'x'.code.toByte(),
        'y'.code.toByte(),
        'z'.code.toByte(),
        '|'.code.toByte(),
        '~'.code.toByte()
    )
    for (i in 0 until len) {
        val b = stringBytes[i]
        if (Arrays.binarySearch(attr_char, b) >= 0) {
            sb.append(b.toChar())
        } else {
            sb.append('%')
            sb.append(digits[0x0f and (b.toInt() ushr 4)])
            sb.append(digits[b.toInt() and 0x0f])
        }
    }
    return sb.toString()
}


fun String.ellipsis(length: Short): String {
    return if (this.count() > length) {
        "${this.substring(0, length - 1)}..."
    } else {
        this
    }
}

fun String.replaceMultiple(what: HashMap<String, String>): String {
    var where = this
    what.forEach {
        where = where.replace(it.key, it.value)
    }
    return where
}

fun String.toPDF(): ByteArray {
    val out = ByteArrayOutputStream()
    HtmlConverter.convertToPdf(this, out, ConverterProperties());
    val result = out.toByteArray()
    out.close()
    return result
}