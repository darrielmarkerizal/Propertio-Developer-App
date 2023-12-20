package com.propertio.developer.dasbor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.propertio.developer.R
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.dashboard.DashboardApi
import com.propertio.developer.api.common.dashboard.DashboardResponse
import com.propertio.developer.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

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
                        setupViewChart(dashboardResponse.data?.views?.weekly ?: emptyList())
                        setupLeadChart(dashboardResponse.data?.leads?.weekly ?: emptyList())
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                //TODO: Handle failure
            }

        })
    }

    private fun setupViewChart(views: List<DashboardResponse.Data.Views.Weekly>) {
        val entries = ArrayList<Entry>()
        views.forEachIndexed { index, view ->
            entries.add(Entry(index.toFloat(), view.total?.toFloat() ?: 0f))
        }

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.RED, Color.TRANSPARENT)
        )

        val dataSet = LineDataSet(entries, "Views")
        dataSet.valueTextSize = 10f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillFormatter = IFillFormatter { _, _ -> binding.chartDilihat.axisLeft.axisMinimum }
        dataSet.fillColor = Color.RED
        dataSet.fillDrawable = gradientDrawable
        dataSet.fillAlpha = 85
        dataSet.color = Color.RED
        dataSet.valueFormatter = IntegerValueFormatter()

        val lineData = LineData(dataSet)

        binding.chartDilihat.data = lineData
        binding.chartDilihat.invalidate()
        binding.chartDilihat.description.isEnabled = false

        // Set chart properties
        val xAxis: XAxis = binding.chartDilihat.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM


        val yAxisLeft: YAxis = binding.chartDilihat.axisLeft
        val yAxisRight: YAxis = binding.chartDilihat.axisRight

        xAxis.valueFormatter = IndexAxisValueFormatter(views.map { formatDate(it.date ?: "") })
        yAxisLeft.valueFormatter = IntegerAxisValueFormatter()
        yAxisRight.setDrawLabels(false)

        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        yAxisLeft.setDrawGridLines(false)
        yAxisRight.setDrawGridLines(false)
        yAxisRight.setDrawAxisLine(false)
    }

    private fun setupLeadChart(leads: List<DashboardResponse.Data.Leads.Weekly>) {
        val entries = ArrayList<Entry>()
        leads.forEachIndexed { index, lead ->
            entries.add(Entry(index.toFloat(), lead.total?.toFloat() ?: 0f))
        }

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.BLUE, Color.TRANSPARENT)
        )

        val dataSet = LineDataSet(entries, "Leads")
        dataSet.valueTextSize = 10f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillFormatter = IFillFormatter { _, _ -> binding.chartMenarik.axisLeft.axisMinimum }
        dataSet.fillColor = Color.BLUE
        dataSet.fillDrawable = gradientDrawable
        dataSet.fillAlpha = 85
        dataSet.color = Color.BLUE
        dataSet.valueFormatter = IntegerValueFormatter()

        val lineData = LineData(dataSet)

        binding.chartMenarik.data = lineData
        binding.chartMenarik.invalidate()
        binding.chartMenarik.description.isEnabled = false

        // Set chart properties
        val xAxis: XAxis = binding.chartMenarik.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM


        val yAxisLeft: YAxis = binding.chartMenarik.axisLeft
        val yAxisRight: YAxis = binding.chartMenarik.axisRight

        xAxis.valueFormatter = IndexAxisValueFormatter(leads.map { formatDate(it.date ?: "") })
        yAxisLeft.valueFormatter = IntegerAxisValueFormatter()
        yAxisRight.setDrawLabels(false)

        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        yAxisLeft.setDrawGridLines(false)
        yAxisRight.setDrawGridLines(false)
        yAxisRight.setDrawAxisLine(false)
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

    inner class IntegerValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    inner class IntegerAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}