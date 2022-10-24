package management.data.docs





data class RenderedDocuments (
    var name : String,
    val content : ByteArray,
    val extension : String = "docx"
    ) {

}