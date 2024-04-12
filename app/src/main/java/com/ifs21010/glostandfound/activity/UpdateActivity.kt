package com.ifs21010.glostandfound.activity

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.ifs21010.glostandfound.data.UpdateData
import com.ifs21010.glostandfound.databinding.ActivityUpdateItemBinding
import com.ifs21010.glostandfound.models.UpdateLostFoundResponse
import com.ifs21010.glostandfound.models.UploadCoverResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateItemBinding
    private lateinit var file: File
    private lateinit var imgProfile: ImageView
    private lateinit var uri: Uri
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
                        this@UpdateActivity,
                        ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(this@UpdateActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = DataBindingUtil.setContentView(this@UpdateActivity, R.layout.activity_update_item)

        val idItem = intent.getIntExtra("id", 0)
        val currentTitle = intent.getStringExtra("title")
        val currentDesc = intent.getStringExtra("desc")

        binding.inputTitleTextfield.setText(currentTitle)
        binding.inputDescTextfield.setText(currentDesc)

        val retrofit = RetrofitObject().getRetrofit()
        val apiService = retrofit.create(Api::class.java)

        val sharedPref = this@UpdateActivity.getSharedPreferences(
            "my_prefs_file",
            Context.MODE_PRIVATE
        )

        val myValue = sharedPref?.getString("auth_token", "")

        val authToken = "Bearer $myValue"

        imgProfile = binding.cover

        binding.tombolTambahFoto.setOnClickListener {
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

        binding.tombolUpdateLostfound.setOnClickListener {
            var isCompleted = 0

            if (binding.completedSelection.text.toString() == "Yes") {
                isCompleted = 1
            }

            val body = UpdateData(
                binding.inputTitleTextfield.text.toString(),
                binding.inputDescTextfield.text.toString(),
                binding.selection.text.toString().lowercase(),
                isCompleted
            )

            Log.i("update_tag", body.toString())

            val call = apiService.updateLostFound(authToken, idItem.toString(), body)

            call.enqueue(object : Callback<UpdateLostFoundResponse> {
                override fun onResponse(
                    p0: Call<UpdateLostFoundResponse>,
                    p1: Response<UpdateLostFoundResponse>
                ) {
                    if (p1.isSuccessful) {
                        try {
                            val coverPart = MultipartBody.Part.createFormData(
                                "cover",
                                "cover.png",
                                file.asRequestBody("image/*".toMediaTypeOrNull())
                            )

                            val call2 =
                                apiService.uploadCover(authToken, coverPart, idItem.toString())

                            call2.enqueue(object : Callback<UploadCoverResponse> {
                                override fun onResponse(
                                    p0: Call<UploadCoverResponse>,
                                    p1: Response<UploadCoverResponse>
                                ) {
                                    if (p1.isSuccessful) {
                                        Toast.makeText(
                                            this@UpdateActivity,
                                            "Update Success!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@UpdateActivity,
                                            "Gagal menambahkan gambar!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("my_error", p1.code().toString())
                                    }
                                }

                                override fun onFailure(
                                    p0: Call<UploadCoverResponse>,
                                    p1: Throwable
                                ) {
                                    Toast.makeText(this@UpdateActivity, "Error", Toast.LENGTH_SHORT)
                                        .show()
                                    Log.e("my_error", p1.toString())
                                }
                            })
                        } catch (e: UninitializedPropertyAccessException) {
                            Toast.makeText(
                                this@UpdateActivity,
                                "Update Success!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@UpdateActivity, "Update Failed!", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("update_tag", p0.toString())
                    }
                }

                override fun onFailure(p0: Call<UpdateLostFoundResponse>, p1: Throwable) {
                    Toast.makeText(this@UpdateActivity, "Update Failed!", Toast.LENGTH_SHORT).show()
                    Log.e("update_tag", p1.toString())
                }
            })
        }
    }
}