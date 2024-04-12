package com.ifs21010.glostandfound.models

data class AddLostFoundRequest(
    val title: String,
    val description: String,
    val status: String
)
