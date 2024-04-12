package com.ifs21010.glostandfound.models

data class AddLostFoundResponse(
    val `data`: DataAddLostFoundResponse,
    val message: String,
    val success: Boolean
)