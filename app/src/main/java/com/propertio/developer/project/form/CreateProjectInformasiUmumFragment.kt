package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.databinding.FragmentCreateProjectInformasiUmumBinding
import com.propertio.developer.dialog.CertificateTypeSheetFragment
import com.propertio.developer.dialog.PropertyTypeSheetFragment
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PropertyTypeSpinnerViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectInformasiUmumFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInformasiUmumBinding.inflate(layoutInflater)
    }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    // ViewModels
    private val projectInformationLocationViewModel : ProjectInformationLocationViewModel by activityViewModels()

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

        projectInformationLocationViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                loadTextData()
                projectInformationLocationViewModel.isUploaded = it
            }
        }




        activityBinding.floatingButtonBack.setOnClickListener {
            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
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


            formActivity.onNextButtonProjectManagementClick()
        }

    }


    private fun loadTextData() {
        printLog()
        binding.editHeadlineProject.setText(projectInformationLocationViewModel.headline)
        binding.editJudulProject.setText(projectInformationLocationViewModel.title)
        binding.editDeskripsiProject.setText(projectInformationLocationViewModel.description)
        binding.editTahunProject.setText(projectInformationLocationViewModel.completedAt)
        if (projectInformationLocationViewModel.propertyTypeName != null) {
            getPropertyTypeId(projectInformationLocationViewModel.propertyTypeName!!)
        }
        if (projectInformationLocationViewModel.certificate != null) {
            val listCertificate = MasterDataDeveloperPropertio.certificate
            val certificate = listCertificate.find { it.toDb == projectInformationLocationViewModel.certificate }
            certificateTypeViewModel.certificateTypeData.postValue(certificate)
        }
    }

    private fun getPropertyTypeId(name: String) {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.getPropertyType().enqueue(object : Callback<GeneralTypeResponse> {
            override fun onResponse(
                call: Call<GeneralTypeResponse>,
                response: Response<GeneralTypeResponse>
            ) {
                if (response.isSuccessful) {
                    val typeList = response.body()?.data
                    if (typeList != null) {
                        val id = typeList.find { it.name == name }?.id
                        if (id != null) {
                            propertyTypeViewModel.propertyTypeData.postValue(
                                GeneralType(
                                    id = id,
                                    name = name
                                )
                            )
                        }

                    }
                }
            }

            override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {

            }

        })
    }


    private fun printLog() {
        Log.d( "ViewModel",
            "loadTextData:" +
                    "\n ${projectInformationLocationViewModel.headline} " +
                    "\n ${projectInformationLocationViewModel.title} " +
                    "\n ${projectInformationLocationViewModel.description} " +
                    "\n ${projectInformationLocationViewModel.completedAt} " +
                    "\n ${projectInformationLocationViewModel.propertyTypeName} " +
                    "\n ${projectInformationLocationViewModel.propertyTypeId} " +
                    "\n ${projectInformationLocationViewModel.certificate} "
        )
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