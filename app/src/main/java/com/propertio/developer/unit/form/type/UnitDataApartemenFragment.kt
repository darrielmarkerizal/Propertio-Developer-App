package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.api.developer.unitmanagement.UpdateUnitRequest
import com.propertio.developer.databinding.FragmentUnitDataApartemenBinding
import com.propertio.developer.dialog.ElectricitySheetFragment
import com.propertio.developer.dialog.InteriorSheetFragment
import com.propertio.developer.dialog.ParkingSheetFragment
import com.propertio.developer.dialog.RoadAccessSheetFragment
import com.propertio.developer.dialog.WaterSheetFragment
import com.propertio.developer.dialog.viewmodel.ElectricityTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.InteriorTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ParkingTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.RoadAccessTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.WaterTypeSpinnerViewModel
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.form.UnitFormViewModel
import com.propertio.developer.unit_management.UpdateUnitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnitDataApartemenFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

    private var isParkingTypeSpinnerSelected = false
    private val parkingTypeViewModel by lazy { ViewModelProvider(requireActivity())[ParkingTypeSpinnerViewModel::class.java] }

    private var electricityTypeSpinnerSelected = false
    private val electricityTypeViewModel by lazy { ViewModelProvider(requireActivity())[ElectricityTypeSpinnerViewModel::class.java] }

    private var waterTypeSpinnerSelected = false
    private val waterTypeViewModel by lazy { ViewModelProvider(requireActivity())[WaterTypeSpinnerViewModel::class.java] }

    private var interiorTypeSpinnerSelected = false
    private val interiorTypeViewModel by lazy { ViewModelProvider(requireActivity())[InteriorTypeSpinnerViewModel::class.java] }

    private var roadAccessTypeSpinnerSelected = false
    private val roadAccessTypeViewModel by lazy { ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java] }

    private val binding by lazy {
        FragmentUnitDataApartemenBinding.inflate(layoutInflater)
    }
    private val formActivity by lazy { activity as UnitFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parkingTypeSpinner()
        electricityTypeSpinner()
        waterTypeSpinner()
        interiorTypeSpinner()
        roadAccessTypeSpinner()

        unitFormViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("UnitDataApartemenFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            val luas_bangunan = binding.editLuasBangunanApartemen.text.toString()
            val kamar = binding.editKamarApartemen.text.toString()
            val kamar_mandi = binding.editKamarMandiApartemen.text.toString()
            val parking_type = binding.spinnerTempatParkirApartemen.text.toString()
            val electricity_type = binding.spinnerDayaListrikApartemen.text.toString()
            val water_type = binding.spinnerJenisAirApartemen.text.toString()
            val interior_type = binding.spinnerInteriorApartemen.text.toString()
            val road_access_type = binding.spinnerAksesJalanApartemen.text.toString()

            formActivity?.unitFormViewModel?.luasBangunan = luas_bangunan
            formActivity?.unitFormViewModel?.jumlahKamarTidur = kamar
            formActivity?.unitFormViewModel?.jumlahKamarMandi = kamar_mandi
            formActivity?.unitFormViewModel?.jumlahParkir = parking_type
            formActivity?.unitFormViewModel?.electricityType = electricity_type
            formActivity?.unitFormViewModel?.waterType = water_type
            formActivity?.unitFormViewModel?.interiorType = interior_type
            formActivity?.unitFormViewModel?.roadAccessType = road_access_type



            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataApartemenFragment", "Next button clicked")

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
            luasBangunan = if (binding.editLuasBangunanApartemen.text.toString().isEmpty()) "0" else binding.editLuasBangunanApartemen.text.toString()
            jumlahKamarTidur = if (binding.editKamarApartemen.text.toString().isEmpty()) "0" else binding.editKamarApartemen.text.toString()
            jumlahKamarMandi = if (binding.editKamarMandiApartemen.text.toString().isEmpty()) "0" else binding.editKamarMandiApartemen.text.toString()
            jumlahParkir = binding.spinnerTempatParkirApartemen.text.toString()
            electricityType = binding.spinnerDayaListrikApartemen.text.toString()
            waterType = binding.spinnerJenisAirApartemen.text.toString()
            interiorType = binding.spinnerInteriorApartemen.text.toString()
            roadAccessType = binding.spinnerAksesJalanApartemen.text.toString()
        }
    }
    
    private fun createUnitRequest() : UnitRequest {
        return UnitRequest(
            title = unitFormViewModel.namaUnit ?: "",
            price = unitFormViewModel.hargaUnit ?: "",
            description = unitFormViewModel.deskripsiUnit ?: "",
            stock = unitFormViewModel.stokUnit ?: "",
            surfaceArea = unitFormViewModel.luasBangunan ?: "",
            buildingArea = null,
            floor = null,
            bedroom = unitFormViewModel.jumlahKamarTidur ?: "",
            bathroom = unitFormViewModel.jumlahKamarMandi ?: "",
            garage = unitFormViewModel.jumlahParkir ?: "",
            powerSupply = unitFormViewModel.electricityType ?: "",
            waterType = unitFormViewModel.waterType ?: "",
            interior = unitFormViewModel.interiorType ?: "",
            roadAccess = unitFormViewModel.roadAccessType ?: "",
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
                        Log.d("UnitDataApartemenFragment", "onResponse: $responseData")

                        //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                        formActivity.unitId = responseData.id
                        if (formActivity.unitId != null || formActivity.unitId != 0) {
                            Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            formActivity.onNextButtonUnitManagementClick()
                        } else {
                            Log.e("UnitDataApartemenFragment", "onResponse: Unit ID is null or 0")
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
                    Log.w("UnitDataApartemenFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                    if (response.code() == 500) {
                        Log.e("UnitDataApartemenFragment", "Server error: Something went wrong on the server side.")
                    }
                }
            }

            override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                Log.e("UnitDataApartemenFragment", "onFailure: ${t.message}", t)
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
                        Log.d("UnitDataApartemenFragment", "onResponse: ${response.body()}")
                        Toast.makeText(requireContext(), "Unit berhasil diupdate", Toast.LENGTH_SHORT).show()
                        formActivity.onNextButtonUnitManagementClick()
                    } else {
                        Log.e("UnitDataApartemenFragment", "onResponse: ${response.errorBody()}")
                        Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                Log.e("UnitDataApartemenFragment", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirApartemen.setOnClickListener {
            binding.spinnerTempatParkirApartemen.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataApartemenFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirApartemen.text = it.toUser
            Log.d("UnitDataApartemenFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikApartemen.setOnClickListener {
            binding.spinnerDayaListrikApartemen.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataApartemenFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikApartemen.text = it.toUser
            Log.d("UnitDataApartemenFragment", "electricityTypeSpinner: $electricityTypeSpinnerSelected")
            electricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirApartemen.setOnClickListener {
            binding.spinnerJenisAirApartemen.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataApartemenFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirApartemen.text = it.toUser
            Log.d("UnitDataApartemenFragment", "waterTypeSpinner: $waterTypeSpinnerSelected")
            waterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorApartemen.setOnClickListener {
            binding.spinnerInteriorApartemen.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataApartemenFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorApartemen.text = it.toUser
            Log.d("UnitDataApartemenFragment", "interiorTypeSpinner: $interiorTypeSpinnerSelected")
            interiorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanApartemen.setOnClickListener {
            binding.spinnerAksesJalanApartemen.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanApartemen.text = it.toUser
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: $roadAccessTypeSpinnerSelected")
            roadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        UnitFormViewModel().printLog()
        binding.editLuasBangunanApartemen.setText(unitFormViewModel.luasBangunan)
        binding.editKamarApartemen.setText(unitFormViewModel.jumlahKamarTidur)
        binding.editKamarMandiApartemen.setText(unitFormViewModel.jumlahKamarMandi)
        binding.spinnerTempatParkirApartemen.setText(unitFormViewModel.jumlahParkir)
        binding.spinnerDayaListrikApartemen.setText(unitFormViewModel.electricityType)
        binding.spinnerJenisAirApartemen.setText(unitFormViewModel.waterType)
        binding.spinnerInteriorApartemen.setText(unitFormViewModel.interiorType)
        binding.spinnerAksesJalanApartemen.setText(unitFormViewModel.roadAccessType)
    }
}