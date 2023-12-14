package com.propertio.developer.dasbor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.propertio.developer.R
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.dashboard.DashboardApi
import com.propertio.developer.api.common.dashboard.DashboardResponse
import com.propertio.developer.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                        setupViewChart(viewCount)
                        setupLeadChart(leadCount)
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                //TODO: Handle failure
            }

        })
    }

    private fun setupViewChart(viewCount: Int) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, viewCount.toFloat()))

        val dataSet = LineDataSet(entries, "Views")
        dataSet.color = Color.RED

        val lineData = LineData(dataSet)

        binding.chartDilihat.data = lineData
        binding.chartDilihat.invalidate()

        // Set chart properties
        val xAxis: XAxis = binding.chartDilihat.xAxis
        val yAxisRight: YAxis = binding.chartDilihat.axisRight

        xAxis.setDrawGridLines(false)
        yAxisRight.setDrawLabels(false)
    }

    private fun setupLeadChart(leadCount: Int) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, leadCount.toFloat()))

        val dataSet = LineDataSet(entries, "Leads")
        dataSet.color = Color.BLUE

        val lineData = LineData(dataSet)

        binding.chartMenarik.data = lineData
        binding.chartMenarik.invalidate()

        // Set chart properties
        val xAxis: XAxis = binding.chartMenarik.xAxis
        val yAxisRight: YAxis = binding.chartMenarik.axisRight

        xAxis.setDrawGridLines(false)
        yAxisRight.setDrawLabels(false)
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