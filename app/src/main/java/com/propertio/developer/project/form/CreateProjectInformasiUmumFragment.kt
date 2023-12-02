package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.FragmentCreateProjectInformasiUmumBinding
import com.propertio.developer.dialog.CertificateTypeSheetFragment
import com.propertio.developer.dialog.PropertyTypeSheetFragment
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PropertyTypeSpinnerViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel


class CreateProjectInformasiUmumFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInformasiUmumBinding.inflate(layoutInflater)
    }

    // ViewModels
    val projectInformationLocationViewModel : ProjectInformationLocationViewModel by activityViewModels()

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
            // TODO : Tambahkan kode untuk mengecek apakah sudah dikirim atau belum, jika sudah maka berikan alert bahwa data akan disimpan sebagai draft
            Toast.makeText(activity, "Anda Menekan Di Fragment, Bukan Di Activity", Toast.LENGTH_SHORT).show()

            activity.onBackButtonProjectManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            if (binding.editHeadlineProject.text.toString().isEmpty()) {
                binding.editHeadlineProject.error = "Harap isi Headline Project"
                return@setOnClickListener
            }
            if (binding.editJudulProject.text.toString().isEmpty()) {
                binding.editJudulProject.error = "Harap isi Deskripsi Project"
                return@setOnClickListener
            }
            if (propertyTypeViewModel.propertyTypeData.value?.id == null) {
                binding.spinnerTipeProject.error = "Harap isi Deskripsi Project"
                return@setOnClickListener
            }
            if (certificateTypeViewModel.certificateTypeData.value?.toDb == null) {
                binding.spinnerSertifikatProject.error = "Harap isi Luas Tanah"
                return@setOnClickListener
            }

            // save data in viewModel
            projectInformationLocationViewModel.headline = binding.editHeadlineProject.text.toString()
            projectInformationLocationViewModel.title = binding.editJudulProject.text.toString()
            projectInformationLocationViewModel.propertyTypeId = propertyTypeViewModel.propertyTypeData.value?.id!!
            projectInformationLocationViewModel.description = binding.editDeskripsiProject.text.toString()
            projectInformationLocationViewModel.completedAt = binding.editTahunProject.text.toString()
            projectInformationLocationViewModel.certificate = certificateTypeViewModel.certificateTypeData.value?.toDb!!


            activity.onNextButtonProjectManagementClick()
        }

    }

    private fun certificateTypeSpinner() {
        binding.spinnerSertifikatProject.setOnClickListener {
            CertificateTypeSheetFragment().show(parentFragmentManager, "PropertyTypeSheetFragment")
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isCertificateTypeSpinnerSelected")
        }

        certificateTypeViewModel.certificateTypeData.observe(viewLifecycleOwner) {
            binding.spinnerSertifikatProject.text = it.toUser
            binding.spinnerSertifikatProject.error = null

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
            binding.spinnerTipeProject.error = null

            isPropertyTypeSpinnerSelected = true
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isPropertyTypeSpinnerSelected")
        }
    }


}