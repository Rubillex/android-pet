package com.example.di1.network.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val body: String,
    val from: String,
    val simId: String
)
