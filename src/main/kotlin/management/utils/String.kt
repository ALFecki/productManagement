package management.utils

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import java.io.ByteArrayOutputStream
import java.util.*

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