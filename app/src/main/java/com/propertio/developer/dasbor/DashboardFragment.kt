package com.propertio.developer.dasbor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import com.propertio.developer.R
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.dashboard.DashboardApi
import com.propertio.developer.api.common.dashboard.DashboardResponse
import com.propertio.developer.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }


    private fun fetchData() {
        val retro = Retro(requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE).getString("token", null))
            .getRetroClientInstance()
            .create(DashboardApi::class.java)

        retro.getDashboard().enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                val dashboardResponse = response.body()

                if (response.isSuccessful) {
                    if (dashboardResponse?.data != null) {
                        val unitCount : Int = dashboardResponse.data?.unitCount ?: 0
                        val projectCount : Int = dashboardResponse.data?.projectCount ?: 0
                        val viewCount: Int = dashboardResponse.data?.viewCount ?: 0
                        val leadCount: Int = dashboardResponse.data?.leadCount ?: 0
                        val messageCount: Int = dashboardResponse.data?.messageCount ?: 0


                        setCards(unitCount, projectCount, viewCount, leadCount, messageCount)
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                //TODO: Handle failure
            }

        })
    }

    private fun setCards(unitCount: Int, projectCount: Int, viewCount: Int, leadCount: Int, messageCount: Int) {
        with(binding) {
            textViewNumberJumlahListingProyek.text = unitCount.toString()
            textViewNumberJumlahTipeUnit.text = projectCount.toString()
            textViewNumberJumlahMelihatListing.text = viewCount.toString()
            textViewNumberJumlahTertarik.text = leadCount.toString()
            textViewNumberJumlahPesanMasuk.text = messageCount.toString()
        }
    }


}