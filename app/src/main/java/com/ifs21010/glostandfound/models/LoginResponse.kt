package com.ifs21010.glostandfound.models

data class LoginResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
)