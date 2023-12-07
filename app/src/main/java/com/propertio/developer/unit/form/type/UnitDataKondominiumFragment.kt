package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.databinding.FragmentUnitDataKondominiumBinding
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


class UnitDataKondominiumFragment : Fragment() {
    private lateinit var unitFormViewModel: UnitFormViewModel

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
        FragmentUnitDataKondominiumBinding.inflate(layoutInflater)
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

        observeLiveData(unitFormViewModel.projectId) { projectId ->
            Log.d("UnitDataApartemenFragment", "Observed projectId in ViewModel: $projectId")
        }

        observeLiveData(unitFormViewModel.luasBangunan) {
            binding.editLuasBangunanKondominium.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarTidur) {
            binding.edtJumlahKamarKondominium.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) {
            binding.edtJumlahKamarMandiKondominium.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirKondominium.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikKondominium.text = it
        }

        observeLiveData(unitFormViewModel.waterType) {
            binding.spinnerJenisAirKondominium.text = it
        }

        observeLiveData(unitFormViewModel.interiorType) {
            binding.spinnerInteriorKondominium.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanKondominium.text = it
        }
        
        activityBinding.floatingButtonBack.setOnClickListener {
            val luas_bangunan = binding.editLuasBangunanKondominium.text.toString()
            val kamar = binding.edtJumlahKamarKondominium.text.toString()
            val kamar_mandi = binding.edtJumlahKamarMandiKondominium.text.toString()
            val parking_type = binding.spinnerTempatParkirKondominium.text.toString()
            val electricity_type = binding.spinnerDayaListrikKondominium.text.toString()
            val water_type = binding.spinnerJenisAirKondominium.text.toString()
            val interior_type = binding.spinnerInteriorKondominium.text.toString()
            val road_access_type = binding.spinnerAksesJalanKondominium.text.toString()

            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateJumlahKamar(kamar)
            formActivity?.unitFormViewModel?.updateJumlahKamarMandi(kamar_mandi)
            formActivity?.unitFormViewModel?.updateParkingType(parking_type)
            formActivity?.unitFormViewModel?.updateElectricityType(electricity_type)
            formActivity?.unitFormViewModel?.updateWaterType(water_type)
            formActivity?.unitFormViewModel?.updateInteriorType(interior_type)
            formActivity?.unitFormViewModel?.updateRoadAccessType(road_access_type)

            formActivity.onBackButtonUnitManagementClick()
        }
        
        activityBinding.floatingButtonNext.setOnClickListener {

            val projectId = unitFormViewModel.projectId.value ?: 0
            val title = unitFormViewModel.namaUnit.value ?: ""
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value ?: ""
            val luas_bangunan = binding.editLuasBangunanKondominium.text.toString()
            val kamar = binding.edtJumlahKamarKondominium.text.toString()
            val kamar_mandi = binding.edtJumlahKamarMandiKondominium.text.toString()
            val parking_type = binding.spinnerTempatParkirKondominium.text.toString()
            val electricity_type = binding.spinnerDayaListrikKondominium.text.toString()
            val water_type = binding.spinnerJenisAirKondominium.text.toString()
            val interior_type = binding.spinnerInteriorKondominium.text.toString()
            val road_access_type = binding.spinnerAksesJalanKondominium.text.toString()

            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateJumlahKamar(kamar)
            formActivity?.unitFormViewModel?.updateJumlahKamarMandi(kamar_mandi)
            formActivity?.unitFormViewModel?.updateParkingType(parking_type)
            formActivity?.unitFormViewModel?.updateElectricityType(electricity_type)
            formActivity?.unitFormViewModel?.updateWaterType(water_type)
            formActivity?.unitFormViewModel?.updateInteriorType(interior_type)
            formActivity?.unitFormViewModel?.updateRoadAccessType(road_access_type)


            val retro = Retro(TokenManager(requireContext()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)
            
            val unitRequest = UnitRequest(
                title = title,
                price = price,
                description = description,
                stock = stock,
                buildingArea = luas_bangunan,
                bedroom = kamar,
                bathroom = kamar_mandi,
                garage = parking_type,
                powerSupply = electricity_type,
                waterType = water_type,
                interior = interior_type,
                roadAccess = road_access_type,
                floor = null,
                order = null,
                surfaceArea = null,
            )

            Log.d("UnitDataKondominiumFragment", "onViewCreated: $unitRequest")

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
                            Log.d("UnitDataKondominiumFragment", "onResponse: $responseData")




                            //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                            formActivity.unitId = responseData.id
                            if (formActivity.unitId != null || formActivity.unitId != 0) {
                                Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                formActivity.onNextButtonUnitManagementClick()
                            } else {
                                Log.e("UnitDataKondominiumFragment", "onResponse: Unit ID is null or 0")
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
                        Log.w("UnitDataKondominiumFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                        if (response.code() == 500) {
                            Log.e("UnitDataKondominiumFragment", "Server error: Something went wrong on the server side.")
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataKondominiumFragment", "onFailure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirKondominium.setOnClickListener {
            binding.spinnerTempatParkirKondominium.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataKondominiumFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKondominiumFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirKondominium.text = it.toUser
            Log.d("UnitDataKondominiumFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikKondominium.setOnClickListener {
            binding.spinnerDayaListrikKondominium.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataKondominiumFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKondominiumFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikKondominium.text = it.toUser
            Log.d("UnitDataKondominiumFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirKondominium.setOnClickListener {
            binding.spinnerJenisAirKondominium.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataKondominiumFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKondominiumFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirKondominium.text = it.toUser
            Log.d("UnitDataKondominiumFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorKondominium.setOnClickListener {
            binding.spinnerInteriorKondominium.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataKondominiumFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKondominiumFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorKondominium.text = it.toUser
            Log.d("UnitDataKondominiumFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanKondominium.setOnClickListener {
            binding.spinnerAksesJalanKondominium.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataKondominiumFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKondominiumFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanKondominium.text = it.toUser
            Log.d("UnitDataKondominiumFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }

}