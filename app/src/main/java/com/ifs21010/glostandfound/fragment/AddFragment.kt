package com.ifs21010.glostandfound.fragment

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import com.ifs21010.glostandfound.activity.HomeActivity
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.models.AddLostFoundRequest
import com.ifs21010.glostandfound.models.AddLostFoundResponse
import com.ifs21010.glostandfound.models.UploadCoverResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class AddFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var uri: Uri
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: Api
    private lateinit var authToken: String
    private lateinit var file: File
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
                    Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tombolTambahFoto = view.findViewById<Button>(R.id.tombol_tambah_foto)
        val tombolTambahLostFound = view.findViewById<Button>(R.id.tombol_tambah_lostfound)
        val inputTitle = view.findViewById<TextInputEditText>(R.id.input_title_textfield)
        val inputDesc = view.findViewById<TextInputEditText>(R.id.input_desc_textfield)
        val inputStatus = view.findViewById<AutoCompleteTextView>(R.id.selection)

        val title = inputTitle.text
        val desc = inputDesc.text

        val sharedPref = context?.getSharedPreferences(
            "my_prefs_file",
            Context.MODE_PRIVATE
        )

        val myValue = sharedPref?.getString("auth_token", "")

        authToken = "Bearer $myValue"

        retrofit = RetrofitObject().getRetrofit()
        apiService = retrofit.create(Api::class.java)
        imgProfile = view.findViewById(R.id.cover)

        tombolTambahFoto.setOnClickListener {
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


        tombolTambahLostFound.setOnClickListener {
            addLostFound(title.toString(), desc.toString(), inputStatus)
        }
    }

    private fun addLostFound(title: String, desc: String, status: AutoCompleteTextView) {
        val data = AddLostFoundRequest(title, desc, status.text.toString().lowercase())
        var id: Int

        val call = apiService.createLostFound(authToken, data)
        call.enqueue(object : Callback<AddLostFoundResponse> {
            override fun onResponse(
                p0: Call<AddLostFoundResponse>,
                p1: Response<AddLostFoundResponse>
            ) {
                if (p1.isSuccessful) {
                    id = p1.body()?.data!!.lost_found_id

                    try {
                        val coverPart = MultipartBody.Part.createFormData(
                            "cover",
                            title,
                            file.asRequestBody("image/*".toMediaTypeOrNull())
                        )

                        val call2 = apiService.uploadCover(authToken, coverPart, id.toString())

                        call2.enqueue(object : Callback<UploadCoverResponse> {
                            override fun onResponse(
                                p0: Call<UploadCoverResponse>,
                                p1: Response<UploadCoverResponse>
                            ) {
                                if (p1.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Berhasil menambahkan!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val mainActivity = activity as? HomeActivity

                                    val bundle = Bundle()

                                    mainActivity?.loadFragment(HomeFragment(), bundle)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Gagal menambahkan gambar!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("my_error", p1.code().toString())
                                }
                            }

                            override fun onFailure(p0: Call<UploadCoverResponse>, p1: Throwable) {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                                Log.e("my_error", p1.toString())
                            }

                        })
                    } catch (e: UninitializedPropertyAccessException) {
                        Toast.makeText(context, "Berhasil menambahkan!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.i("my_tag", "Gagal menambahkan lost found!")
                }
            }

            override fun onFailure(p0: Call<AddLostFoundResponse>, p1: Throwable) {
                Toast.makeText(context, "Gagal menambahkan!", Toast.LENGTH_SHORT).show()
            }

        })
    }

}