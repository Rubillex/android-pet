package com.example.di1.network

import com.example.di1.network.models.MessageRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("https://webhook.site") }

        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun sendMessage(data: MessageRequest): HttpResponse {
        return client.post("/fef98b9a-cf27-45f4-b287-8f03309d34d5") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
    }

    suspend fun getMessage(): MessageRequest {
        return client.get("/fef98b9a-cf27-45f4-b287-8f03309d34d5").body()
    }
}

