package management.utils

import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import java.io.ByteArrayOutputStream

fun XWPFParagraph.replaceMultiple(replacingData : Map<String, String?>, isUnderline : Boolean = false) {
    replacingData.keys.forEach { replaceKey ->
        val replaceValue = replacingData.getValue(replaceKey) ?: return@forEach
        val fullText = this.text
        if (fullText.contains(replaceKey)) {
            this.runs.forEachIndexed { index, xwpfRun ->
                val runText = xwpfRun.text()
                var hasModified = false
                if (runText.contains(replaceKey)) {
                    val text = runText.replace(replaceKey, replaceValue)
                    xwpfRun.setText(text, 0)
                    hasModified = true
                } // TODO else
                if (hasModified) {
//                    xwpfRun.setTextHighlightColor("none")
                    if (isUnderline) {
                        xwpfRun.underline = UnderlinePatterns.SINGLE
                    }
                }
            }
        }
    }
}

fun XWPFTable.replaceMultiple(replacingData: Map<String, String?>, isUnderline: Boolean = false) {
    this.rows.forEach {
        it.tableCells.forEach {
            it.paragraphs.forEach {
                it.replaceMultiple(replacingData, isUnderline)
            }
        }
    }
}

fun XWPFDocument.replaceMultiple(replacingData: Map<String, String?>, isUnderline: Boolean = false) : XWPFDocument {
    this.tables.forEachIndexed { index, table ->
        table.replaceMultiple(replacingData, isUnderline)
    }
    this.paragraphs.forEachIndexed {index, parag ->
        parag.replaceMultiple(replacingData,isUnderline)
    }
    return this
}


fun XWPFDocument.toByteArray(closeAfterExport: Boolean = false): ByteArray {
    val out = ByteArrayOutputStream()
    this.write(out)
    out.close()

    if (closeAfterExport) {
        this.close()
    }
    return out.toByteArray()
}