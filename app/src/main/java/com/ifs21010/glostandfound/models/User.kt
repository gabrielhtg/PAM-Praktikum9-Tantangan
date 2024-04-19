package com.ifs21010.glostandfound.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("created_at")
    val createdAt: String,
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any,
    val id: Int,
    val name: String,
    val photo: String,
    @SerializedName("updated_at")
    val updatedAt: String
)