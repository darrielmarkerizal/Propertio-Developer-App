package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
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
            val projectId = unitFormViewModel.projectId.value
            val title = unitFormViewModel.namaUnit.value
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value
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

            formActivity.onNextButtonUnitManagementClick()
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