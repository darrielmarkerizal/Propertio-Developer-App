package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.FragmentUnitDataKantorBinding
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


class UnitDataKantorFragment : Fragment() {
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
        FragmentUnitDataKantorBinding.inflate(layoutInflater)
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

        observeLiveData(unitFormViewModel.projectId) {
            Log.d("UnitDataKantorFragment", "onViewCreated: $it")
        }

        observeLiveData(unitFormViewModel.luasBangunan) {
            binding.editLuasBangunanKantor.setText(it)
        }

        observeLiveData(unitFormViewModel.luasTanah) {
            binding.editLuasTanahKantor.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahLantai) {
            binding.edtJumlahLantaiKantor.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarTidur) {
            binding.edtKamarKantor.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) {
            binding.edtKamarMandiKantor.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirKantor.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikKantor.text = it
        }

        observeLiveData(unitFormViewModel.waterType) {
            binding.spinnerJenisAirKantor.text = it
        }

        observeLiveData(unitFormViewModel.interiorType) {
            binding.spinnerInteriorKantor.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanKantor.text = it
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
            val luas_tanah = binding.editLuasTanahKantor.text.toString()
            val luas_bangunan = binding.editLuasBangunanKantor.text.toString()
            val lantai = binding.edtJumlahLantaiKantor.text.toString()
            val kamar = binding.edtKamarKantor.text.toString()
            val kamar_mandi = binding.edtKamarMandiKantor.text.toString()
            val parking_type = binding.spinnerTempatParkirKantor.text.toString()
            val electricity_type = binding.spinnerDayaListrikKantor.text.toString()
            val water_type = binding.spinnerJenisAirKantor.text.toString()
            val interior_type = binding.spinnerInteriorKantor.text.toString()
            val road_access_type = binding.spinnerAksesJalanKantor.text.toString()

            formActivity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            formActivity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            formActivity?.unitFormViewModel?.updateJumlahLantai(lantai)
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
        binding.spinnerTempatParkirKantor.setOnClickListener {
            binding.spinnerTempatParkirKantor.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataKantorFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKantorFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirKantor.text = it.toUser
            Log.d("UnitDataKantorFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikKantor.setOnClickListener {
            binding.spinnerDayaListrikKantor.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataKantorFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKantorFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikKantor.text = it.toUser
            Log.d("UnitDataKantorFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirKantor.setOnClickListener {
            binding.spinnerJenisAirKantor.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataKantorFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKantorFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirKantor.text = it.toUser
            Log.d("UnitDataKantorFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorKantor.setOnClickListener {
            binding.spinnerInteriorKantor.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataKantorFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKantorFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorKantor.text = it.toUser
            Log.d("UnitDataKantorFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanKantor.setOnClickListener {
            binding.spinnerAksesJalanKantor.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataKantorFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataKantorFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanKantor.text = it.toUser
            Log.d("UnitDataKantorFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }

}