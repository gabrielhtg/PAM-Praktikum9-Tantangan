package com.ifs21010.glostandfound.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.databinding.ActivitySetProfilePictBinding
import com.ifs21010.glostandfound.services.AllServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SetProfilePictActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    private lateinit var file: File
    private lateinit var imgProfile: ImageView
    private lateinit var binding: ActivitySetProfilePictBinding
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    uri = fileUri

                    file = fileUri.toFile()
                    imgProfile.setImageURI(fileUri)
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        this@SetProfilePictActivity,
                        ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        this@SetProfilePictActivity,
                        "Task Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_profile_pict)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = DataBindingUtil.setContentView(
            this@SetProfilePictActivity,
            R.layout.activity_set_profile_pict
        )

        imgProfile = binding.fotoProfil

        binding.chooseProfilePict.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .crop(1f, 1f)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.saveProfilePict.setOnClickListener {
            val retrofit = RetrofitObject().getRetrofit()
            val apiService = retrofit.create(Api::class.java)

            val authToken = AllServices().getAuthToken(this@SetProfilePictActivity)
            val coverPart = MultipartBody.Part.createFormData(
                "photo",
                "photo.png",
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )

            val call = apiService.setProfilePict(authToken, coverPart)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(p0: Call<Void>, p1: Response<Void>) {
                    if (p1.isSuccessful) {
                        Toast.makeText(
                            this@SetProfilePictActivity,
                            "Berhasil set foto profil",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@SetProfilePictActivity,
                            "Gagal set foto profil",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(p0: Call<Void>, p1: Throwable) {
                    Toast.makeText(
                        this@SetProfilePictActivity,
                        "Gagal set foto profil",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }

    }
}