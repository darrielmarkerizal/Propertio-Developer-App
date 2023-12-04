package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.R
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
        
        val activity = activity as UnitFormActivity
        val activityBinding = activity.binding

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
            activity.onBackButtonUnitManagementClick()
        }
        
        activityBinding.floatingButtonNext.setOnClickListener {

            val projectId = unitFormViewModel.projectId.value
            val title = unitFormViewModel.namaUnit.value
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value
            val luas_bangunan = binding.editLuasBangunanKondominium.text.toString()
            val kamar = binding.edtJumlahKamarKondominium.text.toString()
            val kamar_mandi = binding.edtJumlahKamarMandiKondominium.text.toString()
            val parking_type = binding.spinnerTempatParkirKondominium.text.toString()
            val electricity_type = binding.spinnerDayaListrikKondominium.text.toString()
            val water_type = binding.spinnerJenisAirKondominium.text.toString()
            val interior_type = binding.spinnerInteriorKondominium.text.toString()
            val road_access_type = binding.spinnerAksesJalanKondominium.text.toString()

            activity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            activity?.unitFormViewModel?.updateJumlahKamar(kamar)
            activity?.unitFormViewModel?.updateJumlahKamarMandi(kamar_mandi)
            activity?.unitFormViewModel?.updateParkingType(parking_type)
            activity?.unitFormViewModel?.updateElectricityType(electricity_type)
            activity?.unitFormViewModel?.updateWaterType(water_type)
            activity?.unitFormViewModel?.updateInteriorType(interior_type)
            activity?.unitFormViewModel?.updateRoadAccessType(road_access_type)


            activity.onNextButtonUnitManagementClick()
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