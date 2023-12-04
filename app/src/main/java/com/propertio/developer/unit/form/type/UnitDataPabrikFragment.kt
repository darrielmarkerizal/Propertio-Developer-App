package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
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


class UnitDataPabrikFragment : Fragment() {
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

        observeLiveData(unitFormViewModel.projectId) { projectId ->
            Log.d("UnitDataApartemenFragment", "Observed projectId in ViewModel: $projectId")
        }

        observeLiveData(unitFormViewModel.luasBangunan) {
            binding.editLuasBangunanPabrik.setText(it)
        }

        observeLiveData(unitFormViewModel.luasTanah) {
            binding.editLuasTanahPabrik.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) {
            binding.edtKamarMandiPabrik.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirPabrik.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikPabrik.text = it
        }

        observeLiveData(unitFormViewModel.waterType) {
            binding.spinnerJenisAirPabrik.text = it
        }

        observeLiveData(unitFormViewModel.interiorType) {
            binding.spinnerInteriorPabrik.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanPabrik.text = it
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
            val luas_tanah = binding.editLuasTanahPabrik.text.toString()
            val luas_bangunan = binding.editLuasBangunanPabrik.text.toString()
            val kamar_mandi = binding.edtKamarMandiPabrik.text.toString()
            val parking_type = binding.spinnerTempatParkirPabrik.text.toString()
            val electricity_type = binding.spinnerDayaListrikPabrik.text.toString()
            val water_type = binding.spinnerJenisAirPabrik.text.toString()
            val interior_type = binding.spinnerInteriorPabrik.text.toString()
            val road_access_type = binding.spinnerAksesJalanPabrik.text.toString()

            formActivity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
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

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }

}