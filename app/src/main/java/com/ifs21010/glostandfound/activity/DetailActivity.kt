package com.ifs21010.glostandfound.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.databinding.ActivityDetailItemBinding
import com.ifs21010.glostandfound.models.LostFoundDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = DataBindingUtil.setContentView(this@DetailActivity, R.layout.activity_detail_item)

        val itemId = intent.getIntExtra("id", 0)

        val retrofit = RetrofitObject().getRetrofit()
        val apiService = retrofit.create(Api::class.java)

        val sharedPref = this@DetailActivity.getSharedPreferences(
            "my_prefs_file",
            Context.MODE_PRIVATE
        )

        val myValue = sharedPref?.getString("auth_token", "")

        val authToken = "Bearer $myValue"

        val call = apiService.getDetailLostFound(authToken, itemId)

        call.enqueue(object : Callback<LostFoundDetailResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                p0: Call<LostFoundDetailResponse>,
                p1: Response<LostFoundDetailResponse>
            ) {
                if (p1.isSuccessful) {

                    if (p1.body()?.data?.lost_found?.cover !== null) {
                        Glide.with(this@DetailActivity).load(p1.body()?.data?.lost_found?.cover)
                            .into(binding.imageOfLostfound)
                    }

                    binding.title.text = p1.body()!!.data.lost_found.title
                    binding.deskripsi.text = p1.body()!!.data.lost_found.description
                    binding.status.text = p1.body()!!.data.lost_found.status

                    if (p1.body()!!.data.lost_found.is_completed == 1) {
                        binding.completed.text = "Yes"
                    } else {
                        binding.completed.text = "No"
                    }

                    binding.lastUpdated.text = convertDate(p1.body()!!.data.lost_found.updated_at)

                    binding.namaUploader.text = p1.body()!!.data.lost_found.author.name

                    if (p1.body()?.data?.lost_found?.author?.photo !== null) {
                        Glide.with(this@DetailActivity)
                            .load("https://public-api.delcom.org/" + p1.body()!!.data.lost_found.author.photo)
                            .into(binding.circleImageView)
                    }

                    binding.loadingAnimation.visibility = View.INVISIBLE
                    binding.scrollView.visibility = View.VISIBLE

                }
            }

            override fun onFailure(p0: Call<LostFoundDetailResponse>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


    fun convertDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

}