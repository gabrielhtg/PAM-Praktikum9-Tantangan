package com.ifs21010.glostandfound.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.RetrofitObject
import com.ifs21010.glostandfound.activity.SetProfilePictActivity
import com.ifs21010.glostandfound.data.Api
import com.ifs21010.glostandfound.models.GetCurrentUserResponse
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tombolSetFoto = view.findViewById<Button>(R.id.btn_set_image)

        tombolSetFoto.setOnClickListener {
            context?.startActivity(Intent(context, SetProfilePictActivity::class.java))
        }

        getCurrentUser()
    }

    private fun getCurrentUser() {
        val retrofit = RetrofitObject().getRetrofit()
        val apiService = retrofit.create(Api::class.java)
        val loadingAnimation: CircularProgressIndicator =
            requireView().findViewById(R.id.loading_animation)
        val tempatProfil: ConstraintLayout = requireView().findViewById(R.id.tempat_profil)
        val namaProfil: TextView = requireView().findViewById(R.id.nama_profil)
        val emailProfil: TextView = requireView().findViewById(R.id.email_profil)
        val fotoProfile: CircleImageView = requireView().findViewById(R.id.foto_profil)

        val sharedPref = context?.getSharedPreferences(
            "my_prefs_file",
            Context.MODE_PRIVATE
        )

        val myValue = sharedPref?.getString("auth_token", "")

        val authToken = "Bearer $myValue"

        val call = apiService.getCurrentUser(authToken)

        call.enqueue(object : Callback<GetCurrentUserResponse> {
            override fun onResponse(
                p0: Call<GetCurrentUserResponse>,
                p1: Response<GetCurrentUserResponse>
            ) {
                loadingAnimation.visibility = View.INVISIBLE
                tempatProfil.visibility = View.VISIBLE

                try {
                    if (p1.body()?.data!!.user.photo !== null) {
                        Glide.with(context!!)
                            .load("https://public-api.delcom.org/${p1.body()?.data!!.user.photo}")
                            .into(fotoProfile)
                    }
                } catch (e: NullPointerException) {
                    // do nothing
                }

                namaProfil.text = p1.body()?.data!!.user.name
                emailProfil.text = p1.body()?.data!!.user.email
            }

            override fun onFailure(p0: Call<GetCurrentUserResponse>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

}