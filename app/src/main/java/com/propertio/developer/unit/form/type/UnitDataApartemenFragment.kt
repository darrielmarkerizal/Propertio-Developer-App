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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnitDataApartemenFragment : Fragment() {
    private lateinit var unitFormViewModel: UnitFormViewModel

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

        observeLiveData(unitFormViewModel.projectId) { projectId ->
            Log.d("UnitDataApartemenFragment", "Observed projectId in ViewModel: $projectId")
        }

        observeLiveData(unitFormViewModel.luasBangunan) { value ->
            binding.editLuasBangunanApartemen.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahKamarTidur) { value ->
            binding.editKamarApartemen.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) { value ->
            binding.editKamarMandiApartemen.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) { value ->
            binding.spinnerTempatParkirApartemen.text = value
        }

        observeLiveData(unitFormViewModel.electricityType) { value ->
            binding.spinnerDayaListrikApartemen.text = value
        }

        observeLiveData(unitFormViewModel.waterType) { value ->
            binding.spinnerJenisAirApartemen.text = value
        }

        observeLiveData(unitFormViewModel.interiorType) { value ->
            binding.spinnerInteriorApartemen.text = value
        }

        observeLiveData(unitFormViewModel.roadAccessType) { value ->
            binding.spinnerAksesJalanApartemen.text = value
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            val projectId = unitFormViewModel.projectId.value ?: 0
            val title = unitFormViewModel.namaUnit.value ?: ""
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value ?: ""
            val luas_bangunan = binding.editLuasBangunanApartemen.text.toString()
            val kamar = binding.editKamarApartemen.text.toString()
            val kamar_mandi = binding.editKamarMandiApartemen.text.toString()
            val parking_type = binding.spinnerTempatParkirApartemen.text.toString()
            val electricity_type = binding.spinnerDayaListrikApartemen.text.toString()
            val water_type = binding.spinnerJenisAirApartemen.text.toString()
            val interior_type = binding.spinnerInteriorApartemen.text.toString()
            val road_access_type = binding.spinnerAksesJalanApartemen.text.toString()

            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateJumlahKamar(kamar)
            formActivity?.unitFormViewModel?.updateJumlahKamarMandi(kamar_mandi)
            formActivity?.unitFormViewModel?.updateParkingType(parking_type)
            formActivity?.unitFormViewModel?.updateElectricityType(electricity_type)
            formActivity?.unitFormViewModel?.updateWaterType(water_type)
            formActivity?.unitFormViewModel?.updateInteriorType(interior_type)
            formActivity?.unitFormViewModel?.updateRoadAccessType(road_access_type)

            val retro = Retro(TokenManager(requireActivity()).token)
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
                order = null,
                floor = null,
                surfaceArea = null
            )

            Log.d("UnitDataApartemenFragment", "onViewCreated: $unitRequest")

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
                            Log.d("UnitDataApartemenFragment", "onResponse: $responseData")

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

                        if(errorMessage != null) {
                            for (error in errorMessage.split(",")) {
                                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                            }
                        }

                        Log.w("UnitDataApartemenFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                        if (response.code() == 500) {
                            Log.e("UnitDataApartemenFragment", "Server error: Something went wrong on the server side.")
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataApartemenFragment", "onFailure: ${t.message}")
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
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

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }


}