package com.ifs21010.glostandfound.models

data class GetCurrentUserResponse(
    val `data`: DataGetCurrentUser,
    val message: String,
    val success: Boolean
)