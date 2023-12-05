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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnitDataVillaFragment : Fragment() {
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
            binding.editLuasBangunanVilla.setText(it)
        }

        observeLiveData(unitFormViewModel.luasTanah) {
            binding.editLuasTanahVilla.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahLantai) {
            binding.editJumlahLantaiVilla.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarTidur) {
            binding.editKamarVilla.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) {
            binding.editKamarMandiVilla.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirVilla.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikVilla.text = it
        }

        observeLiveData(unitFormViewModel.waterType) {
            binding.spinnerJenisAirVilla.text = it
        }

        observeLiveData(unitFormViewModel.interiorType) {
            binding.spinnerInteriorVilla.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanVilla.text = it
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

            formActivity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateJumlahLantai(jumlah_lantai)
            formActivity?.unitFormViewModel?.updateJumlahKamar(jumlah_kamar_tidur)
            formActivity?.unitFormViewModel?.updateJumlahKamarMandi(jumlah_kamar_mandi)
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

            formActivity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateJumlahLantai(jumlah_lantai)
            formActivity?.unitFormViewModel?.updateJumlahKamar(jumlah_kamar_tidur)
            formActivity?.unitFormViewModel?.updateJumlahKamarMandi(jumlah_kamar_mandi)
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

            Log.d("UnitDataVillaFragment", "onViewCreated: $unitRequest")

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
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataVillaFragment", "onFailure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
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

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }
}