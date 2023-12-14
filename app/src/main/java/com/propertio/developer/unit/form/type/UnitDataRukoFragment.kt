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
import com.propertio.developer.databinding.FragmentUnitDataRukoBinding
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


class UnitDataRukoFragment : Fragment() {
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
        FragmentUnitDataRukoBinding.inflate(layoutInflater)
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
                Log.d("UnitDataRukoFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            val luas_tanah = binding.editLuasTanahRuko.text.toString()
            val luas_bangunan = binding.editLuasBangunanRuko.text.toString()
            val lantai = binding.edtJumlahLantaiRuko.text.toString()
            val kamar = binding.editKamarRuko.text.toString()
            val kamar_mandi = binding.edtKamarMandiRuko.text.toString()
            val parking_type = binding.spinnerTempatParkirRuko.text.toString()
            val electricity_type = binding.spinnerDayaListrikRuko.text.toString()
            val water_type = binding.spinnerJenisAirRuko.text.toString()
            val interior_type = binding.spinnerInteriorRuko.text.toString()
            val road_access_type = binding.spinnerAksesJalanRuko.text.toString()

            formActivity.unitFormViewModel.luasTanah = luas_tanah
            formActivity.unitFormViewModel.luasBangunan = luas_bangunan
            formActivity.unitFormViewModel.jumlahLantai = lantai
            formActivity.unitFormViewModel.jumlahKamarTidur = kamar
            formActivity.unitFormViewModel.jumlahKamarMandi = kamar_mandi
            formActivity.unitFormViewModel.jumlahParkir = parking_type
            formActivity.unitFormViewModel.electricityType = electricity_type
            formActivity.unitFormViewModel.waterType = water_type
            formActivity.unitFormViewModel.interiorType = interior_type
            formActivity.unitFormViewModel.roadAccessType = road_access_type


            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataRukoFragment", "Next button clicked")

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
            luasTanah = binding.editLuasTanahRuko.text.toString()
            luasBangunan = binding.editLuasBangunanRuko.text.toString()
            jumlahParkir = binding.spinnerTempatParkirRuko.text.toString()
            electricityType = binding.spinnerDayaListrikRuko.text.toString()
            waterType = binding.spinnerJenisAirRuko.text.toString()
            interiorType = binding.spinnerInteriorRuko.text.toString()
            roadAccessType = binding.spinnerAksesJalanRuko.text.toString()
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
            garage = unitFormViewModel.jumlahParkir,
            powerSupply = unitFormViewModel.electricityType,
            waterType = unitFormViewModel.waterType,
            interior = unitFormViewModel.interiorType,
            roadAccess = unitFormViewModel.roadAccessType,
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
                        Log.d("UnitDataRukoFragment", "onResponse: $responseData")

                        //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                        formActivity.unitId = responseData.id
                        if (formActivity.unitId != null || formActivity.unitId != 0) {
                            Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            formActivity.onNextButtonUnitManagementClick()
                        } else {
                            Log.e("UnitDataRukoFragment", "onResponse: Unit ID is null or 0")
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
                    Log.w("UnitDataRukoFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                    if (response.code() == 500) {
                        Log.e("UnitDataRukoFragment", "Server error: Something went wrong on the server side.")
                    }
                }
            }

            override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                Log.e("UnitDataRukoFragment", "onFailure: ${t.message}", t)
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
                        Log.d("UnitDataRukoFragment", "onResponse: ${response.body()}")
                        Toast.makeText(requireContext(), "Unit berhasil diupdate", Toast.LENGTH_SHORT).show()
                        formActivity.onNextButtonUnitManagementClick()
                    } else {
                        Log.e("UnitDataRukoFragment", "onResponse: ${response.errorBody()}")
                        Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                Log.e("UnitDataRukoFragment", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirRuko.setOnClickListener {
            binding.spinnerTempatParkirRuko.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataRukoFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRukoFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirRuko.text = it.toUser
            Log.d("UnitDataRukoFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikRuko.setOnClickListener {
            binding.spinnerDayaListrikRuko.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataRukoFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRukoFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikRuko.text = it.toUser
            Log.d("UnitDataRukoFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirRuko.setOnClickListener {
            binding.spinnerJenisAirRuko.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataRukoFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRukoFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirRuko.text = it.toUser
            Log.d("UnitDataRukoFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorRuko.setOnClickListener {
            binding.spinnerInteriorRuko.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataRukoFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRukoFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorRuko.text = it.toUser
            Log.d("UnitDataRukoFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanRuko.setOnClickListener {
            binding.spinnerAksesJalanRuko.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataRukoFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRukoFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanRuko.text = it.toUser
            Log.d("UnitDataRukoFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        UnitFormViewModel().printLog()
        binding.editLuasTanahRuko.setText(unitFormViewModel.luasTanah)
        binding.editLuasBangunanRuko.setText(unitFormViewModel.luasBangunan)
        binding.edtJumlahLantaiRuko.setText(unitFormViewModel.jumlahLantai)
        binding.editKamarRuko.setText(unitFormViewModel.jumlahKamarTidur)
        binding.edtKamarMandiRuko.setText(unitFormViewModel.jumlahKamarMandi)
        binding.spinnerTempatParkirRuko.setText(unitFormViewModel.jumlahParkir)
        binding.spinnerDayaListrikRuko.setText(unitFormViewModel.electricityType)
        binding.spinnerJenisAirRuko.setText(unitFormViewModel.waterType)
        binding.spinnerInteriorRuko.setText(unitFormViewModel.interiorType)
        binding.spinnerAksesJalanRuko.setText(unitFormViewModel.roadAccessType)
    }
}