package com.propertio.developer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.propertio.developer.dasbor.DashboardFragment
import com.propertio.developer.databinding.ActivityMainBinding
import com.propertio.developer.pesan.ChatFragment
import com.propertio.developer.profile.ProfileFragment
import com.propertio.developer.project.ProjectFragment
import com.propertio.developer.project.ProjectViewModel
import com.propertio.developer.project.ProjectViewModelFactory

class MainActivity : AppCompatActivity() {
    private var toastMessage : String? = null

    private lateinit var projectViewModel: ProjectViewModel

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Token
        val sharedPreferences = getSharedPreferences("account_data", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("MainActivity", "Token from prefs: $token")


        // View Model
        val factory = ProjectViewModelFactory((application as PropertioDeveloperApplication).repository)
        projectViewModel = ViewModelProvider(this, factory)[ProjectViewModel::class.java]
        fetchProjectListData(token!!)


        // Toolbar
        val toolbar = binding.toolbarContainer
        val theToolbar = binding.toolbarContainer.root


        toastMessage = intent.getStringExtra("toastMessage")

        // Nav Graph
        with(binding) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            bottomNavigation.setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.dashboardFragment -> {
                        navController.navigate(R.id.dashboardFragment)
                        toolbarContainer.root.visibility = View.GONE
                        true
                    }
                    R.id.projectFragment -> {
                        navController.navigate(R.id.projectFragment)
                        toolbarContainer.root.visibility = View.VISIBLE
                        toolbar.textViewTitle.text = "Proyek Saya"
                        true
                    }
                    R.id.chatFragment -> {
                        navController.navigate(R.id.chatFragment)
                        toolbarContainer.root.visibility = View.VISIBLE
                        toolbar.textViewTitle.text = "Pesan"
                        true
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.profileFragment)
                        toolbarContainer.root.visibility = View.GONE
                        true
                    }

                    else -> true
                }
            }
        }
    }

    private fun fetchProjectListData(token: String) {
        projectViewModel.fetchLiteProject(token)
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
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}