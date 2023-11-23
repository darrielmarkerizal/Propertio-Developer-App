package com.propertio.developer.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertio.developer.api.Retro
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val token: String) : ViewModel() {
    private val _profileData = MutableLiveData<ProfileResponse.ProfileData>()
    val profileData: LiveData<ProfileResponse.ProfileData> get() = _profileData

    private val _provincesData = MutableLiveData<List<ProfileResponse.Province>>()
    val provincesData: LiveData<List<ProfileResponse.Province>> get() = _provincesData

    val combinedData = MediatorLiveData<Pair<ProfileResponse.ProfileData?, List<ProfileResponse.Province>?>>().apply {
        var profileData: ProfileResponse.ProfileData? = null
        var provincesData: List<ProfileResponse.Province>? = null

        addSource(this@ProfileViewModel.profileData) {
            profileData = it
            if (profileData != null && provincesData != null) {
                value = Pair(profileData, provincesData)
            }
        }

        addSource(this@ProfileViewModel.provincesData) {
            provincesData = it
            if (profileData != null && provincesData != null) {
                value = Pair(profileData, provincesData)
            }
        }
    }

    init {
        fetchProfileData()
        fetchProvincesData()
    }

    fun fetchProfileData() {
        viewModelScope.launch {
            val retro = Retro(token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)

            profileApi.getProfile().enqueue(object : Callback<ProfileResponse> {
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch profile data: ${t.message}")
                }

                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        _profileData.value = response.body()?.data
                        Log.d("ProfileViewModel", "Profile data fetched successfully")
                    } else {
                        Log.e("ProfileViewModel", "Failed to fetch profile data: ${response.errorBody()}")
                    }
                }
            })
        }
    }

    fun fetchProvincesData() {
        viewModelScope.launch {
            val retro = Retro(token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)

            profileApi.getProvinces().enqueue(object : Callback<List<ProfileResponse.Province>> {
                override fun onFailure(call: Call<List<ProfileResponse.Province>>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch provinces data: ${t.message}")
                }

                override fun onResponse(call: Call<List<ProfileResponse.Province>>, response: Response<List<ProfileResponse.Province>>) {
                    if (response.isSuccessful) {
                        _provincesData.value = response.body()
                        Log.d("ProfileViewModel", "Provinces data fetched successfully")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Failed to fetch provinces data: $errorBody")
                    }
                }
            })
        }
    }
}