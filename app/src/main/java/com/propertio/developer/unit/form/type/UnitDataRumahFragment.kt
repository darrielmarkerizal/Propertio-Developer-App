package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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
import androidx.lifecycle.Observer


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

    private val binding by lazy {
        FragmentUnitDataRumahBinding.inflate(layoutInflater)
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

        unitFormViewModel = ViewModelProvider(requireActivity())[UnitFormViewModel::class.java]
        Log.d("UnitDataRumahFragment", "onViewCreated: $unitFormViewModel")

        val activity = activity as? UnitFormActivity
        val activityBinding = activity?.binding

        unitFormViewModel.projectId.observe(viewLifecycleOwner, Observer<Int> { projectId ->
            Log.d("UnitDataRumahFragment", "Observed projectId in ViewModel: $projectId")
        })

        parkingTypeSpinner()
        electricityTypeSpinner()
        waterTypeSpinner()
        interiorTypeSpinner()
        roadAccessTypeSpinner()

        activityBinding?.floatingButtonBack?.setOnClickListener {
            activity.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            Log.d("UnitDataRumahFragment", "Next button clicked")

            val projectId = unitFormViewModel.projectId.value
            val title = unitFormViewModel.namaUnit.value
            val description = unitFormViewModel.deskripsiUnit.value
            val stock = unitFormViewModel.stokUnit.value
            val price = unitFormViewModel.hargaUnit.value
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

            activity.onNextButtonUnitManagementClick()
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

}