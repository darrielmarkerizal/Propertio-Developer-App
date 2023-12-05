package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.databinding.FragmentUnitDataRumahBinding
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


class UnitDataRumahFragment : Fragment() {
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

    private val binding by lazy { FragmentUnitDataRumahBinding.inflate(layoutInflater) }
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

        unitFormViewModel = ViewModelProvider(requireActivity())[UnitFormViewModel::class.java]
        Log.d("UnitDataRumahFragment", "onViewCreated: $unitFormViewModel")

        observeLiveData(unitFormViewModel.projectId) { projectId ->
            Log.d("UnitDataRumahFragment", "Observed projectId in ViewModel: $projectId")
        }

        observeLiveData(unitFormViewModel.luasTanah) { value ->
            binding.editLuasTanahRumah.setText(value)
        }

        observeLiveData(unitFormViewModel.luasBangunan) { value ->
            binding.editLuasBangunanRumah.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahLantai) { value ->
            binding.editJumlahLantaiRumah.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahKamarTidur) { value ->
            binding.editKamarRumah.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) { value ->
            binding.editKamarMandiRumah.setText(value)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) { value ->
            binding.spinnerTempatParkirRumah.text = value
        }

        observeLiveData(unitFormViewModel.electricityType) { value ->
            binding.spinnerDayaListrikRumah.text = value
        }

        observeLiveData(unitFormViewModel.waterType) { value ->
            binding.spinnerJenisAirRumah.text = value
        }

        observeLiveData(unitFormViewModel.interiorType) { value ->
            binding.spinnerInteriorRumah.text = value
        }

        observeLiveData(unitFormViewModel.roadAccessType) { value ->
            binding.spinnerAksesJalanRumah.text = value
        }

        parkingTypeSpinner()
        electricityTypeSpinner()
        waterTypeSpinner()
        interiorTypeSpinner()
        roadAccessTypeSpinner()

        activityBinding.floatingButtonBack.setOnClickListener {
            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataRumahFragment", "Next button clicked")

            val projectId = unitFormViewModel.projectId.value ?: 0
            val title = unitFormViewModel.namaUnit.value ?: ""
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value ?: ""
            val luas_tanah = binding.editLuasTanahRumah.text.toString()
            val luas_bangunan = binding.editLuasBangunanRumah.text.toString()
            val jumlah_lantai = binding.editJumlahLantaiRumah.text.toString()
            val jumlah_kamar_tidur = binding.editKamarMandiRumah.text.toString()
            val jumlah_kamar_mandi = binding.editKamarMandiRumah.text.toString()
            val parking_type = binding.spinnerTempatParkirRumah.text.toString()
            val electricity_type = binding.spinnerDayaListrikRumah.text.toString()
            val water_type = binding.spinnerJenisAirRumah.text.toString()
            val interior_type = binding.spinnerInteriorRumah.text.toString()
            val road_access_type = binding.spinnerAksesJalanRumah.text.toString()

            formActivity.unitFormViewModel.updateLuasTanah(luas_tanah)
            formActivity.unitFormViewModel.updateLuasBangunan(luas_bangunan)
            formActivity.unitFormViewModel.updateJumlahLantai(jumlah_lantai)
            formActivity.unitFormViewModel.updateJumlahKamar(jumlah_kamar_tidur)
            formActivity.unitFormViewModel.updateJumlahKamarMandi(jumlah_kamar_mandi)
            formActivity.unitFormViewModel.updateParkingType(parking_type)
            formActivity.unitFormViewModel.updateElectricityType(electricity_type)
            formActivity.unitFormViewModel.updateWaterType(water_type)
            formActivity.unitFormViewModel.updateInteriorType(interior_type)
            formActivity.unitFormViewModel.updateRoadAccessType(road_access_type)

            val retro = Retro(TokenManager(requireActivity()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)

            val unitRequest = UnitRequest(
                title = title,
                price = price,
                description = description,
                stock = stock,
                surfaceArea = luas_tanah,
                buildingArea = luas_bangunan,
                floor = jumlah_lantai,
                bedroom = jumlah_kamar_tidur,
                bathroom = jumlah_kamar_mandi,
                garage = parking_type,
                powerSupply = electricity_type,
                waterType = water_type,
                interior = interior_type,
                roadAccess = road_access_type,
                order = null
            )

            Log.d("UnitDataRumahFragment", "onViewCreated: $unitRequest")

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
                            Log.d("UnitDataRumahFragment", "onResponse: $responseData")




                            //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                            formActivity.unitId = responseData.id
                            if (formActivity.unitId != null || formActivity.unitId != 0) {
                                Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                formActivity.onNextButtonUnitManagementClick()
                            } else {
                                Log.e("UnitDataRumahFragment", "onResponse: Unit ID is null or 0")
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
                        Log.w("UnitDataRumahFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                        if (response.code() == 500) {
                            Log.e("UnitDataRumahFragment", "Server error: Something went wrong on the server side.")
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataRumahFragment", "onFailure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirRumah.setOnClickListener {
            binding.spinnerTempatParkirRumah.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataRumahFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRumahFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirRumah.text = it.toUser
            Log.d("UnitDataRumahFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikRumah.setOnClickListener {
            binding.spinnerDayaListrikRumah.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataRumahFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRumahFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikRumah.text = it.toUser
            Log.d("UnitDataRumahFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirRumah.setOnClickListener {
            binding.spinnerJenisAirRumah.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataRumahFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRumahFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirRumah.text = it.toUser
            Log.d("UnitDataRumahFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorRumah.setOnClickListener {
            binding.spinnerInteriorRumah.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataRumahFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRumahFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorRumah.text = it.toUser
            Log.d("UnitDataRumahFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanRumah.setOnClickListener {
            binding.spinnerAksesJalanRumah.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataRumahFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRumahFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanRumah.text = it.toUser
            Log.d("UnitDataRumahFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }

}