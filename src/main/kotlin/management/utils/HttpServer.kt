package management.utils

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.server.types.files.SystemFile
import org.apache.commons.io.FileUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipInputStream

fun serveFile(data: ByteArray, filename: String = "attachment"): SystemFile {
    val file = File.createTempFile(filename, ".tmp")
    FileUtils.writeByteArrayToFile(file, data)
    return SystemFile(file).attach(filename)
}


fun notFound(): HttpResponse<*> {
    return HttpResponse.notFound<Any>()
}