package com.ifs21010.glostandfound.data

import com.ifs21010.glostandfound.models.AddLostFoundRequest
import com.ifs21010.glostandfound.models.AddLostFoundResponse
import com.ifs21010.glostandfound.models.DeleteResponse
import com.ifs21010.glostandfound.models.GetAllLostAndFoundsResponse
import com.ifs21010.glostandfound.models.GetCurrentUserResponse
import com.ifs21010.glostandfound.models.LoginRequest
import com.ifs21010.glostandfound.models.LoginResponse
import com.ifs21010.glostandfound.models.LostFoundDetailResponse
import com.ifs21010.glostandfound.models.RegisterRequest
import com.ifs21010.glostandfound.models.RegisterResponse
import com.ifs21010.glostandfound.models.UpdateLostFoundResponse
import com.ifs21010.glostandfound.models.UploadCoverResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest) : Call<RegisterResponse>

    @GET("lost-founds")
    fun getLostFounds(
        @Header("Authorization") token: String,
        @Query("is_completed") isCompleted: Int?,
        @Query("is_me") isMe: Int?,
        @Query("status") status: String?
    ): Call<GetAllLostAndFoundsResponse>

    @DELETE("lost-founds/{id}")
    fun deleteLostFound(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteResponse>

    @GET("users/me")
    fun getCurrentUser(
        @Header("Authorization") token: String
    ): Call<GetCurrentUserResponse>

    @POST("lost-founds")
    fun createLostFound(
        @Header("Authorization") authorization: String,
        @Body lostFoundData:AddLostFoundRequest
    ): Call<AddLostFoundResponse>

    @Multipart
    @POST("lost-founds/{id}/cover")
    fun uploadCover(
        @Header("Authorization") authorization: String,
        @Part cover: MultipartBody.Part,
        @Path("id") id: String
    ): Call<UploadCoverResponse>

    @GET("lost-founds/{id}")
    fun getDetailLostFound(
        @Header("Authorization") authorization: String,
        @Path("id") id : Int
    ) : Call<LostFoundDetailResponse>

    @PUT("lost-founds/{id}")
    fun updateLostFound (
        @Header("Authorization") authorization: String,
        @Path("id") id : String,
        @Body updateData : UpdateData
    ) : Call<UpdateLostFoundResponse>

    @Multipart
    @POST("users/photo")
    fun setProfilePict (
        @Header("Authorization") authorization: String,
        @Part photo: MultipartBody.Part,
    ) : Call<Void>
}