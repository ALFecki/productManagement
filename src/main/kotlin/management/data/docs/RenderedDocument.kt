package management.data.docs

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


data class RenderedDocument (
    var name : String,
    val content : ByteArray,
    val extension : String = "docx"
    ) {
    fun toZip(zOut: ZipOutputStream) {
        val entry = ZipEntry("$name.$extension")
        zOut.putNextEntry(entry)
        zOut.write(content)
        zOut.closeEntry()
    }
}