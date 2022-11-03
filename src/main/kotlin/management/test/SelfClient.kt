package management.test

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient

import io.micronaut.http.client.annotation.Client
import jakarta.inject.Singleton


@Singleton
class SelfClient(@param:Client("http://localhost:8080") private val httpClient: HttpClient) {
    fun post(url: String, data: String): String {
        val request = HttpRequest.POST(url, data)

        val pew = httpClient.toBlocking().exchange(request, String::class.java)
        return pew.body() ?: "result is null"
    }
}