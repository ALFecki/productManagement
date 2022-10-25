package management.utils

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.ByteArrayOutputStream

fun XWPFDocument.toByteArray(closeAfterExport: Boolean = false): ByteArray {
    val out = ByteArrayOutputStream()
    this.write(out)
    out.close()

    if (closeAfterExport) {
        this.close()
    }
    return out.toByteArray()
}