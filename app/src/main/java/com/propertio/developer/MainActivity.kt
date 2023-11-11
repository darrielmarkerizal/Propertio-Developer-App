package com.propertio.developer

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.propertio.developer.dasbor.DashboardFragment
import com.propertio.developer.databinding.ActivityMainBinding
import com.propertio.developer.pesan.ChatFragment
import com.propertio.developer.profile.ProfileFragment
import com.propertio.developer.project.ProjectFragment

class MainActivity : AppCompatActivity() {
    private var toastMessage : String? = null

    private val dashboardFragment = DashboardFragment()
    private val projectFragment = ProjectFragment()
    private val chatFragment = ChatFragment()
    private val profileFragment = ProfileFragment()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Token
        val sharedPreferences = getSharedPreferences("account_data", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("MainActivity", "Token from prefs: $token")

        // Toolbar
        val toolbar = binding.toolbarContainer
        val theToolbar = binding.toolbarContainer.root

        replaceFragment(DashboardFragment())

        toastMessage = intent.getStringExtra("toastMessage")

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.dashboard -> {
                    theToolbar.visibility = View.GONE


                    replaceFragment(dashboardFragment)
                }
                R.id.project -> {
                    theToolbar.visibility = View.VISIBLE
                    toolbar.textViewTitle.text = "Proyek Saya"

                    replaceFragment(projectFragment)
                }
                R.id.message -> {
                    theToolbar.visibility = View.VISIBLE
                    toolbar.textViewTitle.text = "Pesan"

                    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
                    val fragment = supportFragmentManager.findFragmentByTag("ChatFragment")
                    if (fragment == null) {
                        supportFragmentManager.beginTransaction()
                            .hide(currentFragment!!)
                            .add(R.id.frame_layout, chatFragment, "ChatFragment")
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .hide(currentFragment!!)
                            .show(fragment)
                            .commit()
                    }
                }
                R.id.profile_setting -> {
                    theToolbar.visibility = View.GONE

                    replaceFragment(profileFragment)
                }

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