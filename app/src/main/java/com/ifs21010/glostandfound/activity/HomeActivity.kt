package com.ifs21010.glostandfound.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ifs21010.glostandfound.R
import com.ifs21010.glostandfound.databinding.ActivityMainBinding
import com.ifs21010.glostandfound.fragment.AddFragment
import com.ifs21010.glostandfound.fragment.HomeFragment
import com.ifs21010.glostandfound.fragment.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = Bundle()

        loadFragment(HomeFragment(), bundle)

        binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_main)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_bottom -> {
                    binding.topAppBar.title = "Home"
                    loadFragment(HomeFragment(), bundle)
                    true
                }

                R.id.add_bottom -> {
                    binding.topAppBar.title = "Add Lost Found"
                    loadFragment(AddFragment(), bundle)
                    true
                }

                R.id.profile_bottom -> {
                    binding.topAppBar.title = "Profile"
                    loadFragment(ProfileFragment(), bundle)
                    true
                }

                else -> false
            }
        }

        setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.perbarui -> {
                    val fragment =
                        supportFragmentManager.findFragmentById(R.id.tempat_fragment) as? HomeFragment

                    Toast.makeText(this@HomeActivity, "Reloading!", Toast.LENGTH_SHORT).show()
                    fragment?.loadData()

                    true
                }

                R.id.sort -> {
                    if (binding.cardFilter.visibility == View.VISIBLE) {
                        binding.cardFilter.visibility = View.INVISIBLE
                    } else {
                        binding.cardFilter.visibility = View.VISIBLE
                    }

                    true
                }

                R.id.logout -> {
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }


        bundle.putInt("isCompleted", 0)

        binding.completed.setOnClickListener {
            if (binding.completed.isChecked) {
                binding.notCompleted.isChecked = false
                bundle.putInt("isCompleted", 1)
            } else if (!binding.completed.isChecked && !binding.notCompleted.isChecked) {
                bundle.remove("isCompleted")
            }
        }

        binding.notCompleted.setOnClickListener {
            if (binding.notCompleted.isChecked) {
                binding.completed.isChecked = false
                bundle.putInt("isCompleted", 0)
            } else if (!binding.completed.isChecked && !binding.notCompleted.isChecked) {
                bundle.remove("isCompleted")
            }
        }

        binding.isMe.setOnClickListener {
            if (binding.isMe.isChecked) {
                bundle.putInt("isMe", 1)
            } else {
                bundle.putInt("isMe", 0)
            }
        }

        binding.lost.setOnClickListener {
            if (binding.lost.isChecked) {
                bundle.putString("status", "lost")
                binding.found.isChecked = false
            }
        }

        binding.found.setOnClickListener {
            if (binding.found.isChecked) {
                bundle.putString("status", "found")
                binding.lost.isChecked = false
            }
        }


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val fragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragment.arguments = bundle

        fragmentTransaction.replace(R.id.tempat_fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu);
        return true
    }

}