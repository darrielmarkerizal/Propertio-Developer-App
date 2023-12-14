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
import com.propertio.developer.databinding.FragmentUnitDataPabrikBinding
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnitDataPabrikFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

    private var isParkingTypeSpinnerSelected = false
    private val parkingTypeViewModel by lazy { ViewModelProvider(requireActivity())[ParkingTypeSpinnerViewModel::class.java] }

    private var isElectricityTypeSpinnerSelected = false
    private val electricityTypeViewModel by lazy { ViewModelProvider(requireActivity())[ElectricityTypeSpinnerViewModel::class.java] }

    private var isWaterTypeSpinnerSelected = false
    private val waterTypeViewModel by lazy { ViewModelProvider(requireActivity())[WaterTypeSpinnerViewModel::class.java] }

    private var isInteriorTypeSpinnerSelected = false
    private val interiorTypeViewModel by lazy { ViewModelProvider(requireActivity())[InteriorTypeSpinnerViewModel::class.java] }

    private var isRoadAccessTypeSpinnerSelected = false
    private val roadAccessTypeViewModel by lazy { ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java] }
    
    private val binding by lazy {
        FragmentUnitDataPabrikBinding.inflate(layoutInflater)
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

        parkingTypeSpinner()
        electricityTypeSpinner()
        waterTypeSpinner()
        interiorTypeSpinner()
        roadAccessTypeSpinner()

        unitFormViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("UnitDataRumahFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }
        
        activityBinding?.floatingButtonBack?.setOnClickListener {
            val luas_tanah = binding.editLuasTanahPabrik.text.toString()
            val luas_bangunan = binding.editLuasBangunanPabrik.text.toString()
            val kamar_mandi = binding.edtKamarMandiPabrik.text.toString()
            val parking_type = binding.spinnerTempatParkirPabrik.text.toString()
            val electricity_type = binding.spinnerDayaListrikPabrik.text.toString()
            val water_type = binding.spinnerJenisAirPabrik.text.toString()
            val interior_type = binding.spinnerInteriorPabrik.text.toString()
            val road_access_type = binding.spinnerAksesJalanPabrik.text.toString()

            formActivity?.unitFormViewModel?.luasTanah = luas_tanah
            formActivity?.unitFormViewModel?.luasBangunan = luas_bangunan
            formActivity?.unitFormViewModel?.jumlahKamarMandi = kamar_mandi
            formActivity?.unitFormViewModel?.jumlahParkir = parking_type
            formActivity?.unitFormViewModel?.electricityType = electricity_type
            formActivity?.unitFormViewModel?.waterType = water_type
            formActivity?.unitFormViewModel?.interiorType = interior_type
            formActivity?.unitFormViewModel?.roadAccessType = road_access_type


            formActivity.onBackButtonUnitManagementClick()
        }
        
        activityBinding?.floatingButtonNext?.setOnClickListener {

            val projectId = unitFormViewModel.projectId ?: 0
            val title = unitFormViewModel.namaUnit ?: ""
            val description = unitFormViewModel.deskripsiUnit ?: ""
            val stock = unitFormViewModel.stokUnit ?: ""
            val price = unitFormViewModel.hargaUnit ?: ""
            val luas_tanah = binding.editLuasTanahPabrik.text.toString()
            val luas_bangunan = binding.editLuasBangunanPabrik.text.toString()
            val kamar_mandi = binding.edtKamarMandiPabrik.text.toString()
            val parking_type = binding.spinnerTempatParkirPabrik.text.toString()
            val electricity_type = binding.spinnerDayaListrikPabrik.text.toString()
            val water_type = binding.spinnerJenisAirPabrik.text.toString()
            val interior_type = binding.spinnerInteriorPabrik.text.toString()
            val road_access_type = binding.spinnerAksesJalanPabrik.text.toString()

            formActivity?.unitFormViewModel?.luasTanah = luas_tanah
            formActivity?.unitFormViewModel?.luasBangunan = luas_bangunan
            formActivity?.unitFormViewModel?.jumlahKamarMandi = kamar_mandi
            formActivity?.unitFormViewModel?.jumlahParkir = parking_type
            formActivity?.unitFormViewModel?.electricityType = electricity_type
            formActivity?.unitFormViewModel?.waterType = water_type
            formActivity?.unitFormViewModel?.interiorType = interior_type
            formActivity?.unitFormViewModel?.roadAccessType = road_access_type

            val retro = Retro(TokenManager(requireContext()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)

            val unitRequest = UnitRequest(
                title = title,
                price = price,
                description = description,
                stock = stock,
                surfaceArea = luas_tanah,
                buildingArea = luas_bangunan,
                floor = null,
                bedroom = null,
                bathroom = null,
                garage = parking_type,
                powerSupply = electricity_type,
                waterType = water_type,
                interior = interior_type,
                roadAccess = road_access_type,
                order = null
            )

            Log.d("UnitDataPabrikFragment", "onViewCreated: $unitRequest")

            retro.postStoreUnit(
                id = projectId,
                unitRequest = unitRequest
            ).enqueue(object : Callback<PostUnitResponse> {
                override fun onResponse(
                    call: Call<PostUnitResponse>,
                    response: Response<PostUnitResponse>
                ) {
                    if(response.isSuccessful) {
                        val responseData = response.body()?.data
                        if (responseData != null) {
                            Log.d("UnitDataPabrikFragment", "onResponse: $responseData")




                            //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                            formActivity.unitId = responseData.id
                            if (formActivity.unitId != null || formActivity.unitId != 0) {
                                Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                formActivity.onNextButtonUnitManagementClick()
                            } else {
                                Log.e("UnitDataPabrikFragment", "onResponse: Unit ID is null or 0")
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
                        Log.w("UnitDataPabrikFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                        if (response.code() == 500) {
                            Log.e("UnitDataPabrikFragment", "Server error: Something went wrong on the server side.")
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataPabrikFragment", "onFailure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirPabrik.setOnClickListener {
            binding.spinnerTempatParkirPabrik.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataPabrikFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataPabrikFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirPabrik.text = it.toUser
            Log.d("UnitDataPabrikFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikPabrik.setOnClickListener {
            binding.spinnerDayaListrikPabrik.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataPabrikFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataPabrikFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikPabrik.text = it.toUser
            Log.d("UnitDataPabrikFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirPabrik.setOnClickListener {
            binding.spinnerJenisAirPabrik.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataPabrikFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataPabrikFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirPabrik.text = it.toUser
            Log.d("UnitDataPabrikFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorPabrik.setOnClickListener {
            binding.spinnerInteriorPabrik.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataPabrikFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataPabrikFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorPabrik.text = it.toUser
            Log.d("UnitDataPabrikFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanPabrik.setOnClickListener {
            binding.spinnerAksesJalanPabrik.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataPabrikFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataPabrikFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanPabrik.text = it.toUser
            Log.d("UnitDataPabrikFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        unitFormViewModel.printLog()
        binding.editLuasTanahPabrik.setText(unitFormViewModel.luasTanah)
        binding.editLuasBangunanPabrik.setText(unitFormViewModel.luasBangunan)
        binding.edtKamarMandiPabrik.setText(unitFormViewModel.jumlahKamarMandi)
        binding.spinnerTempatParkirPabrik.setText(unitFormViewModel.jumlahParkir)
        binding.spinnerDayaListrikPabrik.setText(unitFormViewModel.electricityType)
        binding.spinnerJenisAirPabrik.setText(unitFormViewModel.waterType)
        binding.spinnerInteriorPabrik.setText(unitFormViewModel.interiorType)
        binding.spinnerAksesJalanPabrik.setText(unitFormViewModel.roadAccessType)
    }

}