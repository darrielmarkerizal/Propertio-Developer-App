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
import com.propertio.developer.databinding.FragmentUnitDataRuangUsahaBinding
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


class UnitDataRuangUsahaFragment : Fragment() {
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
        FragmentUnitDataRuangUsahaBinding.inflate(layoutInflater)
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
            binding.editLuasBangunanRuangUsaha.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahKamarMandi) {
            binding.editKamarMandiRuangUsaha.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirRuangUsaha.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikRuangUsaha.text = it
        }

        observeLiveData(unitFormViewModel.waterType) {
            binding.spinnerJenisAirRuangUsaha.text = it
        }

        observeLiveData(unitFormViewModel.interiorType) {
            binding.spinnerInteriorRuangUsaha.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanRuangUsaha.text = it
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            activity.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {

            val projectId = unitFormViewModel.projectId.value
            val title = unitFormViewModel.namaUnit.value
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value
            val luas_bangunan = binding.editLuasBangunanRuangUsaha.text.toString()
            val kamar_mandi = binding.editKamarMandiRuangUsaha.text.toString()
            val parking_type = binding.spinnerTempatParkirRuangUsaha.text.toString()
            val electricity_type = binding.spinnerDayaListrikRuangUsaha.text.toString()
            val water_type = binding.spinnerJenisAirRuangUsaha.text.toString()
            val interior_type = binding.spinnerInteriorRuangUsaha.text.toString()
            val road_access_type = binding.spinnerAksesJalanRuangUsaha.text.toString()

            activity.unitFormViewModel.updateLuasBangunan(luas_bangunan)
            activity.unitFormViewModel.updateJumlahKamarMandi(kamar_mandi)
            activity.unitFormViewModel.updateParkingType(parking_type)
            activity.unitFormViewModel.updateElectricityType(electricity_type)
            activity.unitFormViewModel.updateWaterType(water_type)
            activity.unitFormViewModel.updateInteriorType(interior_type)
            activity.unitFormViewModel.updateRoadAccessType(road_access_type)


            activity.onNextButtonUnitManagementClick()
        }
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirRuangUsaha.setOnClickListener {
            binding.spinnerTempatParkirRuangUsaha.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataRuangUsahaFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRuangUsahaFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirRuangUsaha.text = it.toUser
            Log.d("UnitDataRuangUsahaFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikRuangUsaha.setOnClickListener {
            binding.spinnerDayaListrikRuangUsaha.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ElectricitySheetFragment")
            Log.d("UnitDataRuangUsahaFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRuangUsahaFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikRuangUsaha.text = it.toUser
            Log.d("UnitDataRuangUsahaFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun waterTypeSpinner() {
        binding.spinnerJenisAirRuangUsaha.setOnClickListener {
            binding.spinnerJenisAirRuangUsaha.isEnabled = true
            WaterSheetFragment().show(childFragmentManager, "WaterSheetFragment")
            Log.d("UnitDataRuangUsahaFragment", "waterTypeSpinner: clicked")
        }

        waterTypeViewModel.waterTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRuangUsahaFragment", "waterTypeSpinner: $it")
            binding.spinnerJenisAirRuangUsaha.text = it.toUser
            Log.d("UnitDataRuangUsahaFragment", "waterTypeSpinner: $isWaterTypeSpinnerSelected")
            isWaterTypeSpinnerSelected = true
        }
    }

    private fun interiorTypeSpinner() {
        binding.spinnerInteriorRuangUsaha.setOnClickListener {
            binding.spinnerInteriorRuangUsaha.isEnabled = true
            InteriorSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataRuangUsahaFragment", "interiorTypeSpinner: clicked")
        }

        interiorTypeViewModel.interiorTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRuangUsahaFragment", "interiorTypeSpinner: $it")
            binding.spinnerInteriorRuangUsaha.text = it.toUser
            Log.d("UnitDataRuangUsahaFragment", "interiorTypeSpinner: $isInteriorTypeSpinnerSelected")
            isInteriorTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanRuangUsaha.setOnClickListener {
            binding.spinnerAksesJalanRuangUsaha.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataRuangUsahaFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataRuangUsahaFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanRuangUsaha.text = it.toUser
            Log.d("UnitDataRuangUsahaFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }
}