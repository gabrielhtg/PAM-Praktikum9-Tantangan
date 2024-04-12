package com.ifs21010.glostandfound.models

data class GetAllLostAndFoundsResponse(
    val `data`: ListLostFounds,
    val message: String,
    val success: Boolean
)