package management.utils

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse

fun serveFile(file: ByteArray, filename: String = "attachment"): HttpResponse<*> {
    return HttpResponse.ok(file)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=\"${filename.toRFC5987()}\""
        )
        .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
        .header(HttpHeaders.TRANSFER_ENCODING, "utf-8")
        .header(HttpHeaders.CONTENT_TRANSFER_ENCODING, "utf-8")
}




fun notFound() : HttpResponse<*> {
    return HttpResponse.notFound<Any>()
}