package com.propertio.developer.unit.form.type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.databinding.FragmentUnitDataTanahBinding
import com.propertio.developer.dialog.RoadAccessSheetFragment
import com.propertio.developer.dialog.viewmodel.RoadAccessTypeSpinnerViewModel
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.form.UnitFormViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnitDataTanahFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

    private var isRoadAccessTypeSpinnerSelected = false
    private val roadAccessTypeViewModel by lazy { ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java] }

    private val binding by lazy {
        FragmentUnitDataTanahBinding.inflate(layoutInflater)
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


        roadAccessTypeSpinner()

        activityBinding.floatingButtonBack.setOnClickListener {
            val luas_tanah = binding.editLuasTanahTanah.text.toString()
            val road_access_type = binding.spinnerAksesJalanTanah.text.toString()

            formActivity.unitFormViewModel.luasTanah = luas_tanah
            formActivity.unitFormViewModel.roadAccessType = road_access_type

            formActivity.onBackButtonUnitManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {

            val projectId = unitFormViewModel.projectId ?: 0
            val title = unitFormViewModel.namaUnit ?: ""
            val description = unitFormViewModel.deskripsiUnit ?: ""
            val stock = unitFormViewModel.stokUnit ?: ""
            val price = unitFormViewModel.hargaUnit ?: ""
            val luas_tanah = binding.editLuasTanahTanah.text.toString()
            val road_access_type = binding.spinnerAksesJalanTanah.text.toString()

            formActivity.unitFormViewModel.luasTanah = luas_tanah
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
                buildingArea = null,
                floor = null,
                bedroom = null,
                bathroom = null,
                garage = null,
                powerSupply = null,
                waterType = null,
                interior = null,
                roadAccess = road_access_type,
                order = null
            )

            Log.d("UnitDataTanahFragment", "onViewCreated: $unitRequest")

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
                            Log.d("UnitDataTanahFragment", "onResponse: $responseData")




                            //TODO: Tambahkan kode seperti ini untuk setiap tipe unit
                            formActivity.unitId = responseData.id
                            if (formActivity.unitId != null || formActivity.unitId != 0) {
                                Toast.makeText(requireContext(), "Unit berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                formActivity.onNextButtonUnitManagementClick()
                            } else {
                                Log.e("UnitDataTanahFragment", "onResponse: Unit ID is null or 0")
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
                        Log.w("UnitDataTanahFragment", "onResponse: Error code: ${response.code()}, message: ${response.message()}, error body: $errorMessage")

                        if (response.code() == 500) {
                            Log.e("UnitDataTanahFragment", "Server error: Something went wrong on the server side.")
                            // You can add more actions here, for example, show an error dialog or a specific message to the user
                        }
                    }
                }

                override fun onFailure(call: Call<PostUnitResponse>, t: Throwable) {
                    Log.e("UnitDataTanahFragment", "onFailure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Gagal menambahkan unit", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun roadAccessTypeSpinner() {
        binding.spinnerAksesJalanTanah.setOnClickListener {
            binding.spinnerAksesJalanTanah.isEnabled = true
            RoadAccessSheetFragment().show(childFragmentManager, "RoadAccessSheetFragment")
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: clicked")
        }

        roadAccessTypeViewModel.roadAccessTypeData.observe(viewLifecycleOwner) {
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: $it")
            binding.spinnerAksesJalanTanah.text = it.toUser
            Log.d("UnitDataTanahFragment", "roadAccessTypeSpinner: $isRoadAccessTypeSpinnerSelected")
            isRoadAccessTypeSpinnerSelected = true
        }
    }

    private fun loadTextData() {
        unitFormViewModel.printLog()
        binding.editLuasTanahTanah.setText(unitFormViewModel.luasTanah)
        binding.spinnerAksesJalanTanah.setText(unitFormViewModel.roadAccessType)
    }

}