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
import com.propertio.developer.databinding.FragmentUnitDataGudangBinding
import com.propertio.developer.dialog.ElectricitySheetFragment
import com.propertio.developer.dialog.ParkingSheetFragment
import com.propertio.developer.dialog.RoadAccessSheetFragment
import com.propertio.developer.dialog.viewmodel.ElectricityTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ParkingTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.RoadAccessTypeSpinnerViewModel
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.form.UnitFormViewModel


class UnitDataGudangFragment : Fragment() {
    private lateinit var unitFormViewModel: UnitFormViewModel
    private var isParkingTypeSpinnerSelected = false
    private val parkingTypeViewModel by lazy { ViewModelProvider(requireActivity())[ParkingTypeSpinnerViewModel::class.java] }
    
    private var isElectricityTypeSpinnerSelected = false
    private val electricityTypeViewModel by lazy { ViewModelProvider(requireActivity())[ElectricityTypeSpinnerViewModel::class.java] }
    
    private var isRoadAccessTypeSpinnerSelected = false
    private val roadAccessTypeViewModel by lazy { ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java] }
    
    private val binding by lazy {
        FragmentUnitDataGudangBinding.inflate(layoutInflater)
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
        
        val activity = activity as? UnitFormActivity
        val activityBinding = activity?.binding

        parkingTypeSpinner()
        electricityTypeSpinner()
        roadAccessTypeSpinner()

        observeLiveData(unitFormViewModel.projectId) {
            Log.d("UnitDataGudangFragment", "onViewCreated: $it")
        }

        observeLiveData(unitFormViewModel.luasBangunan) {
            binding.editLuasBangunanGudang.setText(it)
        }

        observeLiveData(unitFormViewModel.luasTanah) {
            binding.editLuasTanahGudang.setText(it)
        }

        observeLiveData(unitFormViewModel.jumlahParkir) {
            binding.spinnerTempatParkirGudang.text = it
        }

        observeLiveData(unitFormViewModel.electricityType) {
            binding.spinnerDayaListrikGudang.text = it
        }

        observeLiveData(unitFormViewModel.roadAccessType) {
            binding.spinnerAksesJalanGudang.text = it
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
            val luas_bangunan = binding.editLuasBangunanGudang.text.toString()
            val luas_tanah = binding.editLuasTanahGudang.text.toString()
            val parking_type = binding.spinnerTempatParkirGudang.text.toString()
            val electricity_type = binding.spinnerDayaListrikGudang.text.toString()
            val road_access_type = binding.spinnerAksesJalanGudang.text.toString()

            activity?.unitFormViewModel?.updateLuasBangunan(luas_bangunan)
            activity?.unitFormViewModel?.updateLuasTanah(luas_tanah)
            activity?.unitFormViewModel?.updateParkingType(parking_type)
            activity?.unitFormViewModel?.updateElectricityType(electricity_type)
            activity?.unitFormViewModel?.updateRoadAccessType(road_access_type)

            activity.onNextButtonUnitManagementClick()
        }
    }

    private fun parkingTypeSpinner() {
        binding.spinnerTempatParkirGudang.setOnClickListener {
            binding.spinnerTempatParkirGudang.isEnabled = true
            ParkingSheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataGudangFragment", "parkingTypeSpinner: clicked")
        }

        parkingTypeViewModel.parkingTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataGudangFragment", "parkingTypeSpinner: $it")
            binding.spinnerTempatParkirGudang.text = it.toUser
            Log.d("UnitDataGudangFragment", "parkingTypeSpinner: $isParkingTypeSpinnerSelected")
            isParkingTypeSpinnerSelected = true
        }
    }

    private fun electricityTypeSpinner() {
        binding.spinnerDayaListrikGudang.setOnClickListener {
            binding.spinnerDayaListrikGudang.isEnabled = true
            ElectricitySheetFragment().show(childFragmentManager, "ParkingSheetFragment")
            Log.d("UnitDataGudangFragment", "electricityTypeSpinner: clicked")
        }

        electricityTypeViewModel.electricityTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataGudangFragment", "electricityTypeSpinner: $it")
            binding.spinnerDayaListrikGudang.text = it.toUser
            Log.d("UnitDataGudangFragment", "electricityTypeSpinner: $isElectricityTypeSpinnerSelected")
            isElectricityTypeSpinnerSelected = true
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanGudang.setOnClickListener {
            binding.spinnerAksesJalanGudang.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "InteriorSheetFragment")
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanGudang.text = it.toUser
            Log.d("UnitDataApartemenFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            updateUI(value)
        }
    }


}