package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.FragmentCreateProjectFasilitasBinding
import com.propertio.developer.project.list.FacilityTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class CreateProjectFasilitasFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectFasilitasBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRecylcerCategoryFacility()
    }

    private fun setRecylcerCategoryFacility() {
        val retro = Retro(TokenManager(requireContext()).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        lifecycleScope.launch {
            fetchFacilityType(retro)
        }
    }

    private suspend fun fetchFacilityType(developerApi: DeveloperApi) {
        withContext(Dispatchers.IO) {
            developerApi.getFacilityType().enqueue(object : Callback<GeneralTypeResponse> {
                override fun onResponse(
                    call: Call<GeneralTypeResponse>,
                    response: Response<GeneralTypeResponse>
                ) {
                    if (response.isSuccessful) {
                        val failities = response.body()?.data

                        if (failities != null) {
                            Log.d("CreateProjectFasilitasFragment", "onResponse Success: $failities")

                            insertRecyclerList(failities)


                        } else {
                            Log.d("CreateProjectFasilitasFragment", "onResponse Success but data is Null")
                        }

                    } else {
                        Log.e(
                            "CreateProjectFasilitasFragment",
                            "onResponse: ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {
                    Log.e("CreateProjectFasilitasFragment", "onFailure: ${t.message}")

                    // TODO: Peringati user jika internet mati

                }


            })
        }
    }

    private fun insertRecyclerList(typeList: List<GeneralType>) {
        with(binding) {
            recyclerViewFasilitas.apply {
                adapter = FacilityTypeAdapter(
                    context = requireContext(),
                    facilityTypeList = typeList,
                    onClickItemFacilityTypeListener = { facilityType ->
                        Log.d("CreateProjectFasilitasFragment", "facilityType: ${facilityType}")
                    }
                )

                layoutManager = LinearLayoutManager(requireContext())

            }
        }
    }
}