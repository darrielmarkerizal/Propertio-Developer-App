package com.propertio.developer.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.api.common.address.City
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.api.profile.ProfileUpdateRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val token: String) : ViewModel() {
    private val _profileData = MutableLiveData<ProfileResponse.ProfileData>()
    val profileData: LiveData<ProfileResponse.ProfileData> get() = _profileData

    private val _provincesData = MutableLiveData<List<Province>>()
    val provincesData: LiveData<List<Province>> get() = _provincesData

    private val _cityData = MutableLiveData<List<City>>()
    val cityData: LiveData<List<City>> get() = _cityData

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

            profileApi.getProvinces().enqueue(object : Callback<List<Province>> {
                override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch provinces data: ${t.message}")
                }

                override fun onResponse(call: Call<List<Province>>, response: Response<List<Province>>) {
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

            profileApi.getCities(provinceId).enqueue(object : Callback<List<City>> {
                override fun onFailure(call: Call<List<City>>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch city data: ${t.message}")
                }

                override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                    if (response.isSuccessful) {
                        _cityData.value = response.body()
                        Log.d("ProfileViewModel", "City data fetched successfully for province id: $provinceId")
                        Log.d("ProfileViewModel", "City response: ${response.body()}")
                        // Log each city
                        response.body()?.forEach { city ->
                            Log.d("ProfileViewModel", "City: ${city.name}, ID: ${city.id}")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileViewModel", "Failed to fetch city data: $errorBody")
                    }
                }
            })
        }
    }

    fun updateProfile(request: ProfileUpdateRequest, pictureProfileFile: MultipartBody.Part?) {
        Log.d("ProfileViewModel", "Updating profile with request: $request")
        if (pictureProfileFile != null) {
            Log.d("ProfileViewModel", "Picture profile file: ${pictureProfileFile.body}")
            Log.d("ProfileViewModel", "Picture profile file name: ${pictureProfileFile.headers?.get("Content-Disposition")}")
            Log.d("ProfileViewModel", "Picture profile file size: ${pictureProfileFile.body.contentLength()}")
            Log.d("ProfileViewModel", "Picture profile file type: ${pictureProfileFile.body.contentType()}")

        } else {
            Log.d("ProfileViewModel", "No picture profile file to send")
        }
        previousProfileData = _profileData.value
        viewModelScope.launch {
            val retro = Retro(token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)
            request.fullName?.toRequestBody("text/plain".toMediaTypeOrNull()!!)?.let { fullName ->
                request.phone?.toRequestBody("text/plain".toMediaTypeOrNull()!!)?.let { phone ->
                    request.address?.toRequestBody("text/plain".toMediaTypeOrNull()!!)?.let { address ->
                        request.city?.toRequestBody("text/plain".toMediaTypeOrNull()!!)?.let { city ->
                            request.province?.toRequestBody("text/plain".toMediaTypeOrNull()!!)?.let { province ->
                                profileApi.updateProfile(
                                    token,
                                    fullName,
                                    phone,
                                    address,
                                    city,
                                    province,
                                    pictureProfileFile
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
                    }
                }
            }
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
                null
            )
            updateProfile(request, null)
            _isUndoSuccessful.value = true
        }
    }
}