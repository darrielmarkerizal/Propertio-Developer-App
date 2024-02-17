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
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.ActivityMainBinding
import com.propertio.developer.permissions.NetworkAccess
import com.propertio.developer.pesan.ChatViewModel
import com.propertio.developer.pesan.ChatViewModelFactory
import com.propertio.developer.project.ProjectViewModel
import com.propertio.developer.project.ProjectViewModelFactory
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel
import com.propertio.developer.project.viewmodel.FacilityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var toastMessage : String? = null

    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var facilityAndInfrastructureTypeViewModel: FacilityAndInfrastructureTypeViewModel
    private lateinit var chatViewModel: ChatViewModel

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // View Model
        val factory = ProjectViewModelFactory((application as PropertioDeveloperApplication).repository)
        projectViewModel = ViewModelProvider(this, factory)[ProjectViewModel::class.java]
        fetchProjectListData(TokenManager(this).token!!)
        fetchInfrastructureTypeApi()

        var unreadChat : Int = 0
        val chatBadges = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        val chatFactory = ChatViewModelFactory((application as PropertioDeveloperApplication).repository)
        chatViewModel = ViewModelProvider(this, chatFactory)[ChatViewModel::class.java]

        lifecycleScope.launch {
            chatViewModel.fetchDataFromApi(TokenManager(this@MainActivity).token!!)
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
                if (NetworkAccess.isNetworkAvailable(this@MainActivity).not()) run {
                    NetworkAccess.buildNoConnectionToast(this@MainActivity).show()
                }

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

        fetchProjectListData(TokenManager(this).token!!)

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    private fun fetchInfrastructureTypeApi() {
        val retro = Retro(TokenManager(this).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.getInfrastructureType().enqueue(object : Callback<GeneralTypeResponse> {
            override fun onResponse(
                call: Call<GeneralTypeResponse>,
                response: Response<GeneralTypeResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        submitToLocalInfrastructureDatabase(data)
                        Log.d("InfrastructureTypeSheet", "onResponse: $data")
                    } else {
                        Log.d("InfrastructureTypeSheet", "onResponse: data is null")
                    }

                } else {
                    Log.d("InfrastructureTypeSheet", "onResponse UnSuccessful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {
                Log.d("InfrastructureTypeSheet", "onFailure: ${t.message}")
            }
        })
    }

    private fun submitToLocalInfrastructureDatabase(data: List<GeneralType>) {
        val factory = FacilityViewModelFactory((application as PropertioDeveloperApplication).repository)
        facilityAndInfrastructureTypeViewModel = ViewModelProvider(this, factory)[FacilityAndInfrastructureTypeViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {
            data.forEach {
                try {
                    this.launch(Dispatchers.IO) {
                        facilityAndInfrastructureTypeViewModel.insertInfrastructure(
                            id = it.id!!,
                            name = it.name!!,
                            icon = it.icon!!,
                            description = it.category ?: ""
                        )
                    }
                } catch (e: Exception) {
                    Log.e("InfrastructureTypeSheet", "submitToLocalInfrastructureDatabase: ${e.message}")
                }

            }
        }
    }
}