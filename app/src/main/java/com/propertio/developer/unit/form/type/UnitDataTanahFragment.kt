package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.api.developer.unitmanagement.UpdateUnitRequest
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.database.MasterDataDeveloperPropertio.searchByUser
import com.propertio.developer.databinding.FragmentUnitDataTanahBinding
import com.propertio.developer.dialog.RoadAccessSheetFragment
import com.propertio.developer.dialog.viewmodel.RoadAccessTypeSpinnerViewModel
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.form.UnitFormViewModel
import com.propertio.developer.unit_management.UpdateUnitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnitDataTanahFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

    private var isRoadAccessTypeSpinnerSelected = false
    private val roadAccessTypeViewModel by lazy { ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java] }

    private val binding by lazy {
        FragmentUnitDataTanahBinding.inflate(layoutInflater)
    }
    private val formActivity by lazy { activity as UnitFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        roadAccessTypeSpinner()

        unitFormViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("UnitDataTanahFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }

        activityBinding.floatingButtonBack.setOnClickListener {
            val luas_tanah = binding.editLuasTanahTanah.text.toString()
            val road_access_type = binding.spinnerAksesJalanTanah.text.toString()

            formActivity.unitFormViewModel.luasTanah = luas_tanah
            formActivity.unitFormViewModel.roadAccessType = MasterDataDeveloperPropertio.roadAccess.searchByUser(road_access_type)

            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataTanahFragment", "Next button clicked")

            updateViewModelFromForm()
            val unitRequest = createUnitRequest()

            if (unitFormViewModel.isUploaded == false) {
                postUnit(unitRequest)
            } else {
                updateUnit(unitRequest)
            }
        }
    }

    private fun updateViewModelFromForm() {
        formActivity.unitFormViewModel.apply {
            luasTanah = if (binding.editLuasTanahTanah.text.toString().isEmpty()) "0" else binding.editLuasTanahTanah.text.toString()
            roadAccessType = MasterDataDeveloperPropertio.roadAccess.searchByUser(binding.spinnerAksesJalanTanah.text.toString())
        }
    }

    private fun createUnitRequest(): UnitRequest {
        return UnitRequest(
            title = unitFormViewModel.namaUnit ?: "",
            price = unitFormViewModel.hargaUnit ?: "",
            description = unitFormViewModel.deskripsiUnit ?: "",
            stock = unitFormViewModel.stokUnit ?: "",
            surfaceArea = unitFormViewModel.luasTanah,
            buildingArea = unitFormViewModel.luasBangunan,
            floor = unitFormViewModel.jumlahLantai,
            bedroom = unitFormViewModel.jumlahKamarTidur,
            bathroom = unitFormViewModel.jumlahKamarMandi,
            garage = unitFormViewModel.jumlahParkir?.toDb,
            powerSupply = unitFormViewModel.electricityType?.toDb,
            waterType = unitFormViewModel.waterType?.toDb,
            interior = unitFormViewModel.interiorType?.toDb,
            roadAccess = unitFormViewModel.roadAccessType?.toDb,
            order = null
        )
    }

    private fun postUnit(unitRequest: UnitRequest) {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.postStoreUnit(
            id = unitFormViewModel.projectId ?: 0,
            unitRequest = unitRequest
        ).enqueue(object : Callback<PostUnitResponse> {
            override fun onResponse(
                call: Call<PostUnitResponse>,
                response: Response<PostUnitResponse>
            ) {
                if(response.isSuccessful) {
                    val responseData = response.body()?.data
                    if (responseData != null) {
                        Log.d("UnitDataTanahFragment", "onResponse: $responseData")

                        //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                        formActivity.unitId = responseData.id
                        if (formActivity.unitId != null || formActivity.unitId != 0) {
                            formActivity.onNextButtonUnitManagementClick()
                        } else {
                            Log.e("UnitDataTanahFragment", "onResponse: Unit ID is null or 0")
                            Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    var errorMessage = response.errorBody()?.string()
                    errorMessage = errorMessage?.split("\"data\":")?.last()
                    errorMessage = errorMessage?.trim('{', '}')

                    if (errorMessage != null) {
                        for (error in errorMessage.split(",")) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                        }
                    }
                    Log.w("UnitDataTanahFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                    if (response.code() == 500) {
                        Log.e("UnitDataTanahFragment", "Server error: Something went wrong on the server side.")
                    }
                }
            }

            override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                Log.e("UnitDataTanahFragment", "onFailure: ${t.message}", t)
                Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUnit(unitRequest: UnitRequest) {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        val updateUnitRequest = UpdateUnitRequest(
            unitId = unitFormViewModel.unitId ?: 0,
            title = unitRequest.title,
            price = unitRequest.price,
            description = unitRequest.description,
            stock = unitRequest.stock,
            surfaceArea = unitRequest.surfaceArea,
            buildingArea = unitRequest.buildingArea,
            floor = unitRequest.floor,
            bedroom = unitRequest.bedroom,
            bathroom = unitRequest.bathroom,
            garage = unitRequest.garage,
            powerSupply = unitRequest.powerSupply,
            waterType = unitRequest.waterType,
            interior = unitRequest.interior,
            roadAccess = unitRequest.roadAccess,
            order = null
        )

        retro.updateUnit(unitFormViewModel.projectId ?: 0, updateUnitRequest).enqueue(object : Callback<UpdateUnitResponse> {
            override fun onResponse(
                call: Call<UpdateUnitResponse>,
                response: Response<UpdateUnitResponse>
            ) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        Log.d("UnitDataTanahFragment", "onResponse: ${response.body()}")
                        formActivity.onNextButtonUnitManagementClick()
                    } else {
                        Log.e("UnitDataTanahFragment", "onResponse: ${response.errorBody()}")
                        Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                Log.e("UnitDataTanahFragment", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanTanah.setOnClickListener {
            binding.spinnerAksesJalanTanah.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanTanah.text = it.toUser
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        unitFormViewModel.printLog()
        binding.editLuasTanahTanah.setText(unitFormViewModel.luasTanah)
        binding.spinnerAksesJalanTanah.setText(unitFormViewModel.roadAccessType?.toUser)
    }

}