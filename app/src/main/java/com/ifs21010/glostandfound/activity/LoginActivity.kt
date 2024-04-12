package com.ifs21010.glostandfound.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.databinding.ActivityLoginBinding
import com.ifs21010.glostandfound.models.LoginRequest
import com.ifs21010.glostandfound.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val retrofit = RetrofitObject().getRetrofit()

        binding.loadingAnimation.hide()

        binding.tombolLogin.setOnClickListener {
            binding.loadingAnimation.show()
            val service = retrofit.create(Api::class.java)

            val loginReq = LoginRequest(
                binding.inputEmailLogin.text.toString(),
                binding.inputPasswordLogin.text.toString()
            )

            service.login(loginReq).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(p0: Call<LoginResponse>, p1: Response<LoginResponse>) {
                    if (p1.isSuccessful) {
                        val sharedPref = applicationContext.getSharedPreferences(
                            "my_prefs_file",
                            Context.MODE_PRIVATE
                        )

                        val editor = sharedPref.edit()
                        editor.putString("auth_token", p1.body()?.data?.token)
                        editor.apply()

                        Log.i("login", "Berhasil login!")
                        binding.loadingAnimation.hide()
                        finish()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.i("login", "Gagal login. ${p1.body()?.message}")
                        Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                        binding.loadingAnimation.hide()
                    }
                }

                override fun onFailure(p0: Call<LoginResponse>, p1: Throwable) {
                    Log.i("login", "Gagal login. ${p1.message}")
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                    binding.loadingAnimation.hide()
                }
            })
        }

        binding.tombolHalamanRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }
}