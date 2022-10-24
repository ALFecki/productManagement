package management.data.docs





data class RenderedDocument (
    var name : String,
    val content : ByteArray,
    val extension : String = "docx"
    ) {

}