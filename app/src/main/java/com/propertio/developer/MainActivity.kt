package com.propertio.developer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.propertio.developer.dasbor.DashboardFragment
import com.propertio.developer.databinding.ActivityMainBinding
import com.propertio.developer.pesan.ChatFragment
import com.propertio.developer.profile.ProfileFragment
import com.propertio.developer.project.ProjectFragment

class MainActivity : AppCompatActivity() {
    private var toastMessage : String? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(DashboardFragment())

        toastMessage = intent.getStringExtra("toastMessage")

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.dashboard -> replaceFragment(DashboardFragment())
                R.id.project -> replaceFragment(ProjectFragment())
                R.id.message -> replaceFragment(ChatFragment())
                R.id.profile_setting -> replaceFragment(ProfileFragment())

                else -> {

                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()

        if (toastMessage != null) {
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            toastMessage = null
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}