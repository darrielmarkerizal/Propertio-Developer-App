package com.propertio.developer.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.api.profile.ProfileUpdateRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val token: String) : ViewModel() {
    private val _profileData = MutableLiveData<ProfileResponse.ProfileData>()
    val profileData: LiveData<ProfileResponse.ProfileData> get() = _profileData

    private val _provincesData = MutableLiveData<List<ProfileResponse.Province>>()
    val provincesData: LiveData<List<ProfileResponse.Province>> get() = _provincesData

    private val _cityData = MutableLiveData<List<ProfileResponse.City>>()
    val cityData: LiveData<List<ProfileResponse.City>> get() = _cityData

    private val _updateProfileResponse = MutableLiveData<Response<UserResponse>>()
    val updateProfileResponse: LiveData<Response<UserResponse>> = _updateProfileResponse

    private var previousProfileData: ProfileResponse.ProfileData? = null

    private val _isUndoSuccessful = MutableLiveData<Boolean>()
    val isUndoSuccessful: LiveData<Boolean> get() = _isUndoSuccessful


    fun resetUndoSuccessStatus() {
        _isUndoSuccessful.value = false
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
                        Log.d("ProfileViewModel", "Profile data fetched successfully: ${response.body()?.data}")
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
                        Log.d("ProfileViewModel", "Provinces data fetched successfully: ${response.body()}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Failed to fetch provinces data: $errorBody")
                    }
                }
            })
        }
    }

    fun fetchCityData(provinceId: String) {
        viewModelScope.launch {
            val retro = Retro(token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)

            profileApi.getCities(provinceId).enqueue(object : Callback<List<ProfileResponse.City>> {
                override fun onFailure(call: Call<List<ProfileResponse.City>>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch city data: ${t.message}")
                }

                override fun onResponse(call: Call<List<ProfileResponse.City>>, response: Response<List<ProfileResponse.City>>) {
                    if (response.isSuccessful) {
                        _cityData.value = response.body()
                        Log.d("ProfileViewModel", "City data fetched successfully for province id: $provinceId")
                        Log.d("ProfileViewModel", "City response: ${response.body()}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Failed to fetch city data: $errorBody")
                    }
                }
            })
        }
    }

    fun updateProfile(request: ProfileUpdateRequest) {
        Log.d("ProfileViewModel", "Updating profile with request: $request")
        previousProfileData = _profileData.value
        viewModelScope.launch {
            val retro = Retro(token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)
            profileApi.updateProfile(
                token,
                request.fullName,
                request.phone,
                request.address,
                request.province,
                request.city
            ).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        _updateProfileResponse.value = response
                        Log.d("ProfileViewModel", "Profile update successful with response: ${response.body()}")
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Profile update failed with error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("ProfileViewModel", "Profile update failed with exception: ${t.message}")
                }
            })
        }
    }

    fun undoProfileUpdate() {
        previousProfileData?.let { previousData ->
            val request = ProfileUpdateRequest(
                previousData.userData?.fullName,
                previousData.userData?.phone,
                previousData.userData?.address,
                previousData.userData?.city,
                previousData.userData?.province,
                previousData.role,
                previousData.userData?.pictureProfile
            )
            updateProfile(request)
            _isUndoSuccessful.value = true
        }
    }
}