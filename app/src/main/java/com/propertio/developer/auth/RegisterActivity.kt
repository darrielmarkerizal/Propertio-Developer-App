package com.propertio.developer.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.RegisterUserRequest
import com.propertio.developer.api.auth.RegisterUserResponse
import com.propertio.developer.api.auth.UserApi
import com.propertio.developer.databinding.ActivityRegisterBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.CountryPhoneCodeSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PhoneCodeViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    // Phone Code
    private lateinit var phoneCodeViewModel: PhoneCodeViewModel

    // Province & City
    private var isProvinceSelected : Boolean = false
    private lateinit var provinceViewModel: ProvinceSpinnerViewModel
    private lateinit var cityViewModel: CitiesSpinnerViewModel


    // Image
    private var imageUri: Uri? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            // Store the imageUri in a variable to use it later in the Register function
            this.imageUri = imageUri
            val filename = getPathFromUri(imageUri).split("/").last()
            binding.buttonTambahProfile.text = filename
            Log.d("RegisterActivity", "onActivityResult: $filename")
        }
}




    private fun getPathFromUri(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        cursor?.moveToNext()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA) ?: 0
        val path = cursor?.getString(if (columnIndex >= 0) columnIndex else 0)
        cursor?.close()
        return path!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        phoneCodeSpinner()

        provinceSpinner()
        citySpinner()


        passwordUIErrorListener()


        // Add Image
        binding.buttonTambahProfile.setOnClickListener {
            pickPhoto()
        }

        // Validate And Register
        binding.buttonRegistrasi.setOnClickListener {
            validateForm()
        }

        // Go To Login Activity
        binding.linkToLogin.setOnClickListener {
            goToLoginActivity()
        }


    }

    private fun pickPhoto() {
        val mediaStoreIntent = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val intent = Intent(Intent.ACTION_PICK, mediaStoreIntent)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun passwordUIErrorListener() {
        with(binding) {
            editTextPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextPassword.error = null
                    editTextPasswordLayout.isEndIconVisible = true
                }
            })

            editTextPasswordConfirmation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextPasswordConfirmation.error = null
                    editTextPasswordConfirmationLayout.isEndIconVisible = true
                }
            })
        }
    }

    private fun Register(userRequest: RegisterUserRequest) {
        val retro = Retro(null)
            .getRetroClientInstance()
            .create(UserApi::class.java)

        val email = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.email!!)
        val password = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.password!!)
        val passwordConfirmation = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.passwordConfirmation!!)
        val firstName = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.firstName!!)
        val lastName = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.lastName!!)
        val phoneNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.phoneNumber!!)
        val city = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.city!!)
        val province = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.province!!)
        val role = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.role)
        val status = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.status)
        val address = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.address!!)

//        val pictureProfileFile = createImagePart()
        val fileDir = applicationContext.filesDir
        val file = File(fileDir, "image.jpg")
        val fileInputStream = contentResolver.openInputStream(imageUri!!)
        val fileOutputStream = FileOutputStream(file)
        fileInputStream?.copyTo(fileOutputStream)
        fileInputStream!!.copyTo(fileOutputStream)
        fileInputStream.close()

        val pictureProfileFile = MultipartBody.Part.createFormData(
            "picture_profile_file",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        Log.d("RegisterActivity", "Register. pictureProfileFile is success")

        retro.registerUserForm(
            email,
            password,
            passwordConfirmation,
            firstName,
            lastName,
            phoneNumber,
            city,
            province,
            role,
            status,
            address,
            pictureProfileFile
        ).enqueue(object : Callback<RegisterUserResponse> {
            override fun onResponse(
                call: Call<RegisterUserResponse>,
                response: Response<RegisterUserResponse>
            ) {
                Log.d("RegisterActivity", "Register. onResponse Body: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("RegisterActivity", "Register. onResponse Success: " +
                            "status: ${response.body()?.status}" +
                            "message: ${response.body()?.message}")
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    goToLoginActivity()

                }
                else if (response.code() == 422) {
                    Log.d("RegisterActivity", "response code 422: ${response.errorBody()?.string()}")
                }
                else {
                    Log.e("RegisterActivity", "Register. onResponse not Successful: " +
                            "status: ${response.body()?.status}" +
                            "message: ${response.body()?.message}")
                    Toast.makeText(this@RegisterActivity, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterUserResponse>, t: Throwable) {
                Log.e("RegisterActivity", "Register. onFailure: ${t.message}")
            }
        })


    }

    private fun validateForm() {
        with(binding) {
            if (editTextNamaDepan.text.toString().isEmpty()) {
                editTextNamaDepan.error = "Nama Depan tidak boleh kosong"
                editTextNamaDepan.requestFocus()
                return
            }
            if (editTextNamaBelakang.text.toString().isEmpty()) {
                editTextNamaBelakang.error = "Nama Belakang tidak boleh kosong"
                editTextNamaBelakang.requestFocus()
                return
            }
            if (editTextEmail.text.toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) {
                editTextEmail.error = "Email tidak valid"
                editTextEmail.requestFocus()
                return
            }
            if (editTextNomorTelepon.text.toString().isEmpty()) {
                editTextNomorTelepon.error = "Nomor Telepon tidak boleh kosong"
                editTextNomorTelepon.requestFocus()
                return
            }
            if (editTextPassword.text.toString().isEmpty()) {
                editTextPasswordLayout.isEndIconVisible = false
                editTextPassword.error = "Password tidak boleh kosong"
                editTextPassword.requestFocus()
                return
            }
            if (editTextPasswordConfirmation.text.toString() != editTextPassword.text.toString()) {
                editTextPasswordConfirmationLayout.isEndIconVisible = false
                editTextPasswordConfirmation.error = "Password tidak sama"
                editTextPasswordConfirmation.requestFocus()
                return
            }
            if (provinceViewModel.provinceData.value?.provinceId == "" || provinceViewModel.provinceData.value?.provinceId == null) {
                Toast.makeText(this@RegisterActivity, "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show()
                buttonProvincesSelection.error = "Pilih Provinsi terlebih dahulu"
                buttonProvincesSelection.requestFocus()
                return
            }
            if (cityViewModel.citiesData.value?.citiesId == "" || cityViewModel.citiesData.value?.citiesId == null) {
                Toast.makeText(this@RegisterActivity, "Pilih Kota terlebih dahulu", Toast.LENGTH_SHORT).show()
                spinnerDistrict.error = "Pilih Kota terlebih dahulu"
                spinnerDistrict.requestFocus()
                return
            }
            if (editTextAddress.text.toString().isEmpty()) {
                editTextAddress.error = "Alamat tidak boleh kosong"
                editTextAddress.requestFocus()
                return
            }
            if (imageUri == null) {
                Toast.makeText(this@RegisterActivity, "Pilih Foto terlebih dahulu", Toast.LENGTH_SHORT).show()
                return
            }

            val userRequest = RegisterUserRequest(
                email = editTextEmail.text.toString(),
                password = editTextPassword.text.toString(),
                passwordConfirmation = editTextPasswordConfirmation.text.toString(),
                firstName = editTextNamaDepan.text.toString(),
                lastName = editTextNamaBelakang.text.toString(),
                phoneNumber = editTextNomorTelepon.text.toString(),
                city = cityViewModel.citiesData.value?.citiesId,
                province = provinceViewModel.provinceData.value?.provinceId,
                role = "developer",
                status = "active",
                address = editTextAddress.text.toString(),
            )

            Register(userRequest)
        }
    }

    private fun goToLoginActivity() {
        val intentToActivityLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToActivityLogin)
        finish()
    }


    private fun citySpinner() {
        cityViewModel = ViewModelProvider(this)[CitiesSpinnerViewModel::class.java]


        binding.spinnerDistrict.setOnClickListener {
            if (isProvinceSelected) {
                CitiesSheetFragment().show(supportFragmentManager, "CitySheetFragment")
            } else {
                Toast.makeText(this, "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }



        cityViewModel.citiesData.observe(this) {
            binding.spinnerDistrict.text = it.citiesName
            binding.spinnerDistrict.error = null
        }
    }



    private fun provinceSpinner() {
        provinceViewModel = ViewModelProvider(this)[ProvinceSpinnerViewModel::class.java]
        binding.buttonProvincesSelection.setOnClickListener {
            ProvinceSheetFragment().show(supportFragmentManager, "ProvinceSheetFragment")
            Log.d("RegisterActivity", "provinceSpinner. is selected :$isProvinceSelected")
        }

        provinceViewModel.provinceData.observe(this) {
            binding.buttonProvincesSelection.text = it.provinceName
            binding.buttonProvincesSelection.error = null
            isProvinceSelected = true
            cityViewModel.citiesData
                .postValue(
                    CitiesModel(
                        citiesId = "",
                        provinceId = it.provinceId,
                        citiesName = "-- Pilih Kota"
                    )
                )
        }
    }



    private fun phoneCodeSpinner() {
        phoneCodeViewModel = ViewModelProvider(this)[PhoneCodeViewModel::class.java]
        binding.spinnerNomorTeleponPrefix.setOnClickListener {
            CountryPhoneCodeSheetFragment().show(supportFragmentManager, "CountryPhoneCodeSheetFragment")
        }

        phoneCodeViewModel.phoneCodeData.observe(this) {
            val countryId = it.emoji + " " + it.countryId
            binding.spinnerNomorTeleponPrefix.text = countryId
            binding.textViewNomorTeleponPrefix.text = it.code
        }
    }


}