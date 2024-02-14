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
import com.propertio.developer.databinding.FragmentUnitDataVillaBinding
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


class UnitDataVillaFragment : Fragment() {
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
        FragmentUnitDataVillaBinding.inflate(layoutInflater)
    }
    private val formActivity by lazy { activity as UnitFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
                Log.d("UnitDataVillaFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }

        activityBinding.floatingButtonBack.setOnClickListener {
            val luas_tanah = binding.editLuasTanahVilla.text.toString()
            val luas_bangunan = binding.editLuasBangunanVilla.text.toString()
            val jumlah_lantai = binding.editJumlahLantaiVilla.text.toString()
            val jumlah_kamar_tidur = binding.editKamarVilla.text.toString()
            val jumlah_kamar_mandi = binding.editKamarMandiVilla.text.toString()

            val parking_type = binding.spinnerTempatParkirVilla.text.toString()
            val electricity_type = binding.spinnerDayaListrikVilla.text.toString()
            val water_type = binding.spinnerJenisAirVilla.text.toString()
            val interior_type = binding.spinnerInteriorVilla.text.toString()
            val road_access_type = binding.spinnerAksesJalanVilla.text.toString()

            formActivity?.unitFormViewModel?.luasTanah = luas_tanah
            formActivity?.unitFormViewModel?.luasBangunan = luas_bangunan
            formActivity?.unitFormViewModel?.jumlahLantai = jumlah_lantai
            formActivity?.unitFormViewModel?.jumlahKamarTidur = jumlah_kamar_tidur
            formActivity?.unitFormViewModel?.jumlahKamarMandi = jumlah_kamar_mandi

            formActivity?.unitFormViewModel?.jumlahParkir = MasterDataDeveloperPropertio.parking.searchByUser(parking_type)
            formActivity?.unitFormViewModel?.electricityType = MasterDataDeveloperPropertio.electricity.searchByUser(electricity_type)
            formActivity?.unitFormViewModel?.waterType = MasterDataDeveloperPropertio.water.searchByUser(water_type)
            formActivity?.unitFormViewModel?.interiorType = MasterDataDeveloperPropertio.interior.searchByUser(interior_type)
            formActivity?.unitFormViewModel?.roadAccessType = MasterDataDeveloperPropertio.roadAccess.searchByUser(road_access_type)

            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataVillaFragment", "Next button clicked")

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
            luasTanah = if (binding.editLuasTanahVilla.text.toString().isEmpty()) "0" else binding.editLuasTanahVilla.text.toString()
            luasBangunan = if (binding.editLuasBangunanVilla.text.toString().isEmpty()) "0" else binding.editLuasBangunanVilla.text.toString()
            jumlahLantai = if (binding.editJumlahLantaiVilla.text.toString().isEmpty()) "0" else binding.editJumlahLantaiVilla.text.toString()
            jumlahKamarTidur = if (binding.editKamarVilla.text.toString().isEmpty()) "0" else binding.editKamarVilla.text.toString()
            jumlahKamarMandi = if (binding.editKamarMandiVilla.text.toString().isEmpty()) "0" else binding.editKamarMandiVilla.text.toString()

            val parking_type = binding.spinnerTempatParkirVilla.text.toString()
            val electricity_type = binding.spinnerDayaListrikVilla.text.toString()
            val water_type = binding.spinnerJenisAirVilla.text.toString()
            val interior_type = binding.spinnerInteriorVilla.text.toString()
            val road_access_type = binding.spinnerAksesJalanVilla.text.toString()

            jumlahParkir = MasterDataDeveloperPropertio.parking.searchByUser(parking_type)
            electricityType = MasterDataDeveloperPropertio.electricity.searchByUser(electricity_type)
            waterType = MasterDataDeveloperPropertio.water.searchByUser(water_type)
            interiorType = MasterDataDeveloperPropertio.interior.searchByUser(interior_type)
            roadAccessType = MasterDataDeveloperPropertio.roadAccess.searchByUser(road_access_type)
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
                        Log.d("UnitDataVillaFragment", "onResponse: $responseData")

                        //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                        formActivity.unitId = responseData.id
                        if (formActivity.unitId != null || formActivity.unitId != 0) {
                            Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            formActivity.onNextButtonUnitManagementClick()
                        } else {
                            Log.e("UnitDataVillaFragment", "onResponse: Unit ID is null or 0")
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
                    Log.w("UnitDataVillaFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                    if (response.code() == 500) {
                        Log.e("UnitDataVillaFragment", "Server error: Something went wrong on the server side.")
                    }
                }
            }

            override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                Log.e("UnitDataVillaFragment", "onFailure: ${t.message}", t)
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
                        Log.d("UnitDataVillaFragment", "onResponse: ${response.body()}")
                        formActivity.onNextButtonUnitManagementClick()
                    } else {
                        Log.e("UnitDataVillaFragment", "onResponse: ${response.errorBody()}")
                        Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                Log.e("UnitDataVillaFragment", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirVilla.setOnClickListener {
            binding.spinnerTempatParkirVilla.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataVillaFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataVillaFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirVilla.text = it.toUser
            Log.d("UnitDataVillaFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikVilla.setOnClickListener {
            binding.spinnerDayaListrikVilla.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataVillaFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataVillaFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikVilla.text = it.toUser
            Log.d("UnitDataVillaFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirVilla.setOnClickListener {
            binding.spinnerJenisAirVilla.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataVillaFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataVillaFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirVilla.text = it.toUser
            Log.d("UnitDataVillaFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorVilla.setOnClickListener {
            binding.spinnerInteriorVilla.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataVillaFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataVillaFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorVilla.text = it.toUser
            Log.d("UnitDataVillaFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanVilla.setOnClickListener {
            binding.spinnerAksesJalanVilla.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataVillaFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataVillaFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanVilla.text = it.toUser
            Log.d("UnitDataVillaFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        unitFormViewModel.printLog()
        binding.editLuasTanahVilla.setText(unitFormViewModel.luasTanah)
        binding.editLuasBangunanVilla.setText(unitFormViewModel.luasBangunan)
        binding.editJumlahLantaiVilla.setText(unitFormViewModel.jumlahLantai)
        binding.editKamarVilla.setText(unitFormViewModel.jumlahKamarTidur)
        binding.editKamarMandiVilla.setText(unitFormViewModel.jumlahKamarMandi)
        binding.spinnerTempatParkirVilla.setText(unitFormViewModel.jumlahParkir?.toUser)
        binding.spinnerDayaListrikVilla.setText(unitFormViewModel.electricityType?.toUser)
        binding.spinnerJenisAirVilla.setText(unitFormViewModel.waterType?.toUser)
        binding.spinnerInteriorVilla.setText(unitFormViewModel.interiorType?.toUser)
        binding.spinnerAksesJalanVilla.setText(unitFormViewModel.roadAccessType?.toUser)
    }
}