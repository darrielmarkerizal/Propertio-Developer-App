package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.FragmentCreateProjectInformasiUmumBinding
import com.propertio.developer.dialog.CertificateTypeSheetFragment
import com.propertio.developer.dialog.PropertyTypeSheetFragment
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PropertyTypeSpinnerViewModel


class CreateProjectInformasiUmumFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInformasiUmumBinding.inflate(layoutInflater)
    }

    // Spinner
    private var isPropertyTypeSpinnerSelected = false
    private val propertyTypeViewModel by lazy { ViewModelProvider(requireActivity())[PropertyTypeSpinnerViewModel::class.java] }

    private var isCertificateTypeSpinnerSelected = false
    private val certificateTypeViewModel by lazy { ViewModelProvider(requireActivity())[CertificateTypeSpinnerViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner
        propertyTypeSpinner()
        certificateTypeSpinner()

        // TODO: validate posted_at or Tahun Pembangunan !important






        /*
         * TODO: Berikut contoh mendapatkan binding dari ProjectFormActivity
         *  1. Buat variabel activity "activity = activity as? ProjectFormActivity"
         *  2. Buat variabel activityBinding "activityBinding = activity?.binding"
         *  3. Gunakan activityBinding untuk mengakses binding dari ProjectFormActivity, contoh
         *     activityBinding?.floatingButtonBack?.setOnClickListener { ... } <- ini button Back
         *     activityBinding?.floatingButtonNext?.setOnClickListener { ... } <- ini button Next
         *  4. jangan lupa tambahkan "activity.onBackButtonProjectManagementClick()"
         *     pada floatingButtonBack.setOnClickListener pada bagian paling akhir
         *  5. jangan lupa tambahkan "activity.onNextButtonProjectManagementClick()"
         *     pada floatingButtonNext.setOnClickListener pada bagian paling akhir
         *
         *  NB: penambahan ...ProjectManagementClick bertujuan agar halaman dapat berpindah
         *      ke halaman selanjutnya atau sebelumnya. Disarankan untuk ditaruh paling akhir.
         *      boleh dibuat ke dalam if-else
         */
        val activity = activity as? ProjectFormActivity
        val activityBinding = activity?.binding

        activityBinding?.floatingButtonBack?.setOnClickListener {
            Toast.makeText(activity, "Anda Menekan Di Fragment, Bukan Di Activity", Toast.LENGTH_SHORT).show()

            activity.onBackButtonProjectManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            Toast.makeText(activity, "Anda Menekan Di Fragment, Bukan Di Activity", Toast.LENGTH_SHORT).show()

            activity.onNextButtonProjectManagementClick()
        }

    }

    private fun certificateTypeSpinner() {
        binding.spinnerSertifikatProject.setOnClickListener {
            CertificateTypeSheetFragment().show(parentFragmentManager, "PropertyTypeSheetFragment")
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isCertificateTypeSpinnerSelected")
        }

        certificateTypeViewModel.certificateTypeData.observe(viewLifecycleOwner) {
            binding.spinnerSertifikatProject.text = it

            isCertificateTypeSpinnerSelected = true
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isCertificateTypeSpinnerSelected")
        }
    }

    private fun propertyTypeSpinner() {
        binding.spinnerTipeProject.setOnClickListener {
            PropertyTypeSheetFragment().show(parentFragmentManager, "PropertyTypeSheetFragment")
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isPropertyTypeSpinnerSelected")
        }

        propertyTypeViewModel.propertyTypeData.observe(viewLifecycleOwner) {
            binding.spinnerTipeProject.text = it.name

            isPropertyTypeSpinnerSelected = true
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isPropertyTypeSpinnerSelected")
        }
    }


}