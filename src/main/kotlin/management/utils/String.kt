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
    val attr_char = byteArrayOf('!'.toByte(), '#'.toByte(), '$'.toByte(), '&'.toByte(), '+'.toByte(), '-'.toByte(), '.'.toByte(), '0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(), '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), 'A'.toByte(), 'B'.toByte(), 'C'.toByte(), 'D'.toByte(), 'E'.toByte(), 'F'.toByte(), 'G'.toByte(), 'H'.toByte(), 'I'.toByte(), 'J'.toByte(), 'K'.toByte(), 'L'.toByte(), 'M'.toByte(), 'N'.toByte(), 'O'.toByte(), 'P'.toByte(), 'Q'.toByte(), 'R'.toByte(), 'S'.toByte(), 'T'.toByte(), 'U'.toByte(), 'V'.toByte(), 'W'.toByte(), 'X'.toByte(), 'Y'.toByte(), 'Z'.toByte(), '^'.toByte(), '_'.toByte(), '`'.toByte(), 'a'.toByte(), 'b'.toByte(), 'c'.toByte(), 'd'.toByte(), 'e'.toByte(), 'f'.toByte(), 'g'.toByte(), 'h'.toByte(), 'i'.toByte(), 'j'.toByte(), 'k'.toByte(), 'l'.toByte(), 'm'.toByte(), 'n'.toByte(), 'o'.toByte(), 'p'.toByte(), 'q'.toByte(), 'r'.toByte(), 's'.toByte(), 't'.toByte(), 'u'.toByte(), 'v'.toByte(), 'w'.toByte(), 'x'.toByte(), 'y'.toByte(), 'z'.toByte(), '|'.toByte(), '~'.toByte())
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
    return if(this.count() > length) {
        "${this.substring(0, length-1)}..."
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

fun String.toPDF() : ByteArray {
    val out = ByteArrayOutputStream()
    HtmlConverter.convertToPdf(this, out, ConverterProperties());
    val result = out.toByteArray()
    out.close()
    return result
}