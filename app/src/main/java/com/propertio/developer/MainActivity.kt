package com.propertio.developer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.propertio.developer.dasbor.DashboardFragment
import com.propertio.developer.databinding.ActivityMainBinding
import com.propertio.developer.pesan.ChatFragment
import com.propertio.developer.pesan.ChatViewModel
import com.propertio.developer.pesan.ChatViewModelFactory
import com.propertio.developer.profile.ProfileFragment
import com.propertio.developer.project.ProjectFragment
import com.propertio.developer.project.ProjectViewModel
import com.propertio.developer.project.ProjectViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var toastMessage : String? = null

    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var chatViewModel: ChatViewModel

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

        var unreadChat : Int = 0
        val chatBadges = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        val chatFactory = ChatViewModelFactory((application as PropertioDeveloperApplication).repository)
        chatViewModel = ViewModelProvider(this, chatFactory)[ChatViewModel::class.java]

        lifecycleScope.launch {
            chatViewModel.fetchDataFromApi(token)
            unreadChat = withContext(Dispatchers.IO) {
                chatViewModel.countUnread()
            }

            chatViewModel.unreadChatCount.observe(this@MainActivity) { count ->
                chatBadges.isVisible = count > 0
                chatBadges.number = count
            }
        }





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