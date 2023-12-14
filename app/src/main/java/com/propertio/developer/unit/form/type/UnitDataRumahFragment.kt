package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.api.developer.unitmanagement.UpdateUnitRequest
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
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.form.UnitFormViewModel
import com.propertio.developer.unit_management.UpdateUnitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnitDataRumahFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

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

        Log.d("UnitDataRumahFragment", "onViewCreated: ${unitFormViewModel.printLog()}")
        Log.d("UnitDataRumahFragment", "onViewCreated: $unitFormViewModel")

        parkingTypeSpinner()
        electricityTypeSpinner()
        waterTypeSpinner()
        interiorTypeSpinner()
        roadAccessTypeSpinner()

        unitFormViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("UnitDataRumahFragment", "onViewCreated Updated: $it")
                loadTextData()
                unitFormViewModel.isUploaded = it
                activityBinding?.toolbarContainerUnitForm?.textViewTitle?.text = "Edit Unit"
            }
        }

        activityBinding.floatingButtonBack.setOnClickListener {
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

            formActivity.unitFormViewModel.luasTanah = luas_tanah
            formActivity.unitFormViewModel.luasBangunan = luas_bangunan
            formActivity.unitFormViewModel.jumlahLantai = jumlah_lantai
            formActivity.unitFormViewModel.jumlahKamarTidur = jumlah_kamar_tidur
            formActivity.unitFormViewModel.jumlahKamarMandi = jumlah_kamar_mandi
            formActivity.unitFormViewModel.jumlahKamarTidur = parking_type
            formActivity.unitFormViewModel.electricityType = electricity_type
            formActivity.unitFormViewModel.waterType = water_type
            formActivity.unitFormViewModel.interiorType = interior_type
            formActivity.unitFormViewModel.roadAccessType = road_access_type

            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            Log.d("UnitDataRumahFragment", "Next button clicked")

            val projectId = unitFormViewModel.projectId ?: 0
            val title = unitFormViewModel.namaUnit ?: ""
            val description = unitFormViewModel.deskripsiUnit ?: ""
            val stock = unitFormViewModel.stokUnit ?: ""
            val price = unitFormViewModel.hargaUnit ?: ""
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

            formActivity.unitFormViewModel.luasTanah = luas_tanah
            formActivity.unitFormViewModel.luasBangunan = luas_bangunan
            formActivity.unitFormViewModel.jumlahLantai = jumlah_lantai
            formActivity.unitFormViewModel.jumlahKamarTidur = jumlah_kamar_tidur
            formActivity.unitFormViewModel.jumlahKamarMandi = jumlah_kamar_mandi
            formActivity.unitFormViewModel.jumlahParkir = parking_type
            formActivity.unitFormViewModel.electricityType = electricity_type
            formActivity.unitFormViewModel.waterType = water_type
            formActivity.unitFormViewModel.interiorType = interior_type
            formActivity.unitFormViewModel.roadAccessType = road_access_type


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

            Log.d("UnitDataRumahFragment", "onViewCreated: $unitRequest")

            if (unitFormViewModel.isUploaded == false) {
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
            } else {
                val updateUnitRequest = UpdateUnitRequest(
                    unitId = unitFormViewModel.unitId ?: 0,
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

                val unitId = unitFormViewModel.unitId ?: 0
                retro.updateUnit(projectId, updateUnitRequest).enqueue(object : Callback<UpdateUnitResponse> {
                    override fun onResponse(
                        call: Call<UpdateUnitResponse>,
                        response: Response<UpdateUnitResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("UnitDataRumahFragment", "onResponse: ${response.body()}")
                            Toast.makeText(requireContext(), "Unit berhasil diupdate", Toast.LENGTH_SHORT).show()
                            formActivity.onNextButtonUnitManagementClick()
                        } else {
                            Log.e("UnitDataRumahFragment", "onResponse: ${response.errorBody()}")
                            Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                        Log.e("UnitDataRumahFragment", "onFailure: ${t.message}")
                        Toast.makeText(requireContext(), "Unit gagal diupdate", Toast.LENGTH_SHORT).show()
                    }
                })
            }
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

    private fun loadTextData() {
        unitFormViewModel.printLog()
        binding.editLuasTanahRumah.setText(unitFormViewModel.luasTanah)
        binding.editLuasBangunanRumah.setText(unitFormViewModel.luasBangunan)
        binding.editJumlahLantaiRumah.setText(unitFormViewModel.jumlahLantai)
        binding.editKamarRumah.setText(unitFormViewModel.jumlahKamarTidur)
        binding.editKamarMandiRumah.setText(unitFormViewModel.jumlahKamarMandi)
        binding.spinnerTempatParkirRumah.setText(unitFormViewModel.jumlahParkir)
        binding.spinnerDayaListrikRumah.setText(unitFormViewModel.electricityType)
        binding.spinnerJenisAirRumah.setText(unitFormViewModel.waterType)
        binding.spinnerInteriorRumah.setText(unitFormViewModel.interiorType)
        binding.spinnerAksesJalanRumah.setText(unitFormViewModel.roadAccessType)
    }

}