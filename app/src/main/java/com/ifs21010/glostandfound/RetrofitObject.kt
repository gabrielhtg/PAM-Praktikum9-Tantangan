package com.ifs21010.glostandfound

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitObject {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://public-api.delcom.org/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRetrofit(): Retrofit {
        return retrofit
    }
}