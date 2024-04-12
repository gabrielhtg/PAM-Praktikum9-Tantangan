package com.ifs21010.glostandfound.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.databinding.ActivityRegisterBinding
import com.ifs21010.glostandfound.models.RegisterRequest
import com.ifs21010.glostandfound.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = DataBindingUtil.setContentView(this@RegisterActivity, R.layout.activity_register)

        binding.tombolHalamanLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        binding.loadingAnimation.hide()

        val retrofit = RetrofitObject().getRetrofit()

        binding.tombolRegister.setOnClickListener {
            binding.loadingAnimation.show()

            val registerReq = RegisterRequest(
                binding.inputNamaRegister.text.toString(),
                binding.inputEmailRegister.text.toString(),
                binding.inputPasswordRegister.text.toString()
            )

            val service = retrofit.create(Api::class.java)
            service.register(registerReq).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    p0: Call<RegisterResponse>,
                    p1: Response<RegisterResponse>
                ) {
                    if (p1.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, p1.body()?.message, Toast.LENGTH_LONG)
                            .show()
                        binding.loadingAnimation.hide()
                        finish()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@RegisterActivity, "Login Gagal", Toast.LENGTH_LONG)
                            .show()
                        binding.loadingAnimation.hide()
                    }
                }

                override fun onFailure(p0: Call<RegisterResponse>, p1: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Login Gagal", Toast.LENGTH_LONG).show()
                    binding.loadingAnimation.hide()
                }
            });
        }
    }
}