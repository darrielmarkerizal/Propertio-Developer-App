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
            activity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {

            val projectId = unitFormViewModel.projectId.value
            val title = unitFormViewModel.namaUnit.value
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value
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

            activity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            activity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            activity?.unitFormViewModel?.updateJumlahLantai(jumlah_lantai)
            activity?.unitFormViewModel?.updateJumlahKamar(jumlah_kamar_tidur)
            activity?.unitFormViewModel?.updateJumlahKamarMandi(jumlah_kamar_mandi)
            activity?.unitFormViewModel?.updateParkingType(parking_type)
            activity?.unitFormViewModel?.updateElectricityType(electricity_type)
            activity?.unitFormViewModel?.updateWaterType(water_type)
            activity?.unitFormViewModel?.updateInteriorType(interior_type)
            activity?.unitFormViewModel?.updateRoadAccessType(road_access_type)

            activity.onNextButtonUnitManagementClick()
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