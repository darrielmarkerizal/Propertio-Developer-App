package com.propertio.developer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.propertio.developer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(DashboardFragment())

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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}