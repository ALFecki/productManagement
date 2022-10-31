package management.utils

import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder
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
                    xwpfRun.setText(runText.replace(replaceKey, replaceValue), 0)
                    hasModified = true
                }
                else if(runText.isNotEmpty()) { // [{,quantity,}] //example
                    if (index > 0) {
                        val newRunText = this.runs[index - 1].text() + this.runs[index] + this.runs[index + 1]
                        if (newRunText.contains(replaceKey)) {
                            this.runs[index - 1].setText("", 0)
                            this.runs[index + 1].setText("", 0)
                            xwpfRun.setText(newRunText.replace(replaceKey, replaceValue), 0)
                            hasModified = true
                        }

                    }

                }
                if (hasModified) {
                    xwpfRun.setTextHighlightColor("none")
                    if (isUnderline) {
                        xwpfRun.underline = UnderlinePatterns.SINGLE
                    }
                }
            }
        }
    }
}

fun XWPFTable.replaceMultiple(replacingData: Map<String, String?>, isUnderline: Boolean = false) {
    this.rows.forEach { row ->
        row.tableCells.forEach { table ->
            table.paragraphs.forEach { paragraph ->
                paragraph.replaceMultiple(replacingData, isUnderline)
            }
        }
    }
}

fun XWPFDocument.replaceMultiple(replacingData: Map<String, String?>, isUnderline: Boolean = false) : XWPFDocument {
    this.tables.forEach { table ->
        table.replaceMultiple(replacingData, isUnderline)
    }
    this.paragraphs.forEach { parag ->
        parag.replaceMultiple(replacingData, isUnderline)
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

fun XWPFTableCell.makeBorder(borderType: STBorder.Enum= STBorder.BASIC_THIN_LINES) {
    val cell = this.ctTc.tcPr?:this.ctTc.addNewTcPr()
    val borders = cell.tcBorders?:cell.addNewTcBorders()
    (borders.left?:borders.addNewLeft()).`val` = borderType
    (borders.right?:borders.addNewRight()).`val` = borderType
    (borders.top?:borders.addNewTop()).`val` = borderType
    (borders.bottom?:borders.addNewBottom()).`val` = borderType
}