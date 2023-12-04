package com.propertio.developer.project.form

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectPhotoResponse
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.databinding.FragmentCreateProjectMediaBinding
import com.propertio.developer.model.Caption
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.project.list.UnggahFotoAdapter
import com.propertio.developer.project.viewmodel.ProjectMediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class CreateProjectMediaFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectMediaBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as? ProjectFormActivity }
    private val activityBinding by lazy { formActivity?.binding }

    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    // Activity ViewModels
    private val projectMediaViewModel : ProjectMediaViewModel by activityViewModels()

    private lateinit var photosAdapter : UnggahFotoAdapter


    // Document
    private var documentUri: Uri? = null
    private val documentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            documentUri = result.data?.data

            if (documentUri == null) {
                Log.e("CreateProjectMedia", "documentUri is null")
                return@registerForActivityResult
            }
            Log.d("CreateProjectMedia", "documentUri: $documentUri")
            binding.cardDocumentProyekPropertyThumbnail.apply{
                this.root.visibility = View.VISIBLE
                textViewFilename.text = documentUri?.lastPathSegment?.split("/")?.last() ?: documentUri?.lastPathSegment
            }


        }

    }

    // Image
    private var imageUri : Uri? = null
    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data

            if (imageUri == null) {
                Log.e("CreateProjectMedia", "imageUri is null")
                return@registerForActivityResult
            }
            Log.d("CreateProjectMedia", "imageUri: $imageUri")

            uploadPhotos(imageUri!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        photosAdapter = UnggahFotoAdapter(
            photosList = projectMediaViewModel.projectPhotos.value ?: listOf(),
            onClickButtonCover = {
                if (it.id != null) {
                    updateCoverPhoto(it.id)
                } else {
                    Toast.makeText(context, "Gagal Mengubah Cover Photo", Toast.LENGTH_SHORT).show()
                }
            },
            onClickDelete = {
                if (it.id != null) {
                    deletePhoto(it.id)
                } else {
                    Toast.makeText(context, "Gagal Menghapus Foto", Toast.LENGTH_SHORT).show()
                }
            },
            onClickSaveCaption = {
                if (it.id != null && it.caption != null) {
                    updateCaption(it.id, it.caption!!)
                } else {
                    Toast.makeText(context, "Gagal Menyimpan Caption", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return binding.root
    }

    private fun updateCaption(id: Int, caption: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.updateCaptionProjectPhoto(id, Caption(caption)).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(call: Call<UpdateProjectResponse>, response: Response<UpdateProjectResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateProjectMedia", "onResponse id $id: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchProjectPhotos(formActivity?.projectId ?: 0)
                            }

                        } else {
                            Log.e("CreateProjectMedia", "onResponse id $id: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Gagal Menyimpan Caption", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                    }

                })
            }
        }
    }

    private fun updateCoverPhoto(id: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.updateCoverProjectPhoto(id).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(call: Call<UpdateProjectResponse>, response: Response<UpdateProjectResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateProjectMedia", "onResponse: ${response.body()}")
                            lifecycleScope.launch {
                                fetchProjectPhotos(formActivity?.projectId ?: 0)
                            }
                        } else {
                            Log.e("CreateProjectMedia", "onResponse: ${response.body()}")
                            Toast.makeText(context, "Gagal Mengubah Cover Photo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                    }

                })
            }
        }
    }

    private fun deletePhoto(id: Int, isMessage: Boolean = true) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.deleteProjectPhoto(id).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(call: Call<UpdateProjectResponse>, response: Response<UpdateProjectResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateProjectMedia", "onResponse: ${response.body()}")
                            lifecycleScope.launch {
                                fetchProjectPhotos(formActivity?.projectId ?: 0)
                            }
                        } else {
                            Log.e("CreateProjectMedia", "onResponse: ${response.body()}")
                            Toast.makeText(context, "Gagal Menghapus Foto", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                    }

                })
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Check if there is a document
        if (documentUri == null) {
            binding.cardDocumentProyekPropertyThumbnail.root.visibility = View.GONE
        }


        // Image
        binding.buttonUnggahPhotoProject.setOnClickListener {
            pickPhoto()
        }
        setListUnggahFotoRecycler()
        photosPreviewObserver()

        // Detele All Photos
        binding.linkTextViewProjectPhotoDelete.setOnClickListener {
            deleteAllPhotos()
        }


        // Validate Youtube Link
        validateYoutubeLinkObserver()


        // Tutorial How To Get Youtube Link
        binding.linkTextLinkVideoTutorial.setOnClickListener {
            openTutorialVideo()
        }

        // Contact Us Virtual Tour Project
        binding.buttonHubungiKamiMediaProyek.setOnClickListener {
            openContactUs()
        }

        // Add Dokument
        binding.buttonTambahkanDokumenProject.setOnClickListener {
            pickDocument()
        }



        // Navgiation Button


        activityBinding?.floatingButtonBack?.setOnClickListener {
            // TODO: do something here


            formActivity?.onBackButtonProjectManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            //TODO: Delete this two line below
            formActivity?.onNextButtonProjectManagementClick()
            return@setOnClickListener


            val retro = Retro(TokenManager(requireContext()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)


            val projectId = formActivity?.projectId
            val youtubeLink = binding.editTextLinkYoutubeMediaProject.text.toString()
            val virtualTour = binding.editTextNamaVirtualTour.text.toString()
            val virtualTourLink = binding.textLinkVirtualTourLabel.text.toString()


            val projectIdBody = projectId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val youtubeLinkBody = youtubeLink.toRequestBody("text/plain".toMediaTypeOrNull())
            val virtualTourBody = virtualTour.toRequestBody("text/plain".toMediaTypeOrNull())
            val virtualTourLinkBody = virtualTourLink.toRequestBody("text/plain".toMediaTypeOrNull())

            var documentBody : MultipartBody.Part? = null

            if (documentUri != null) {
                Log.d("CreateProjectMedia", "onViewCreated: $documentUri")
                val fileDir = requireContext().applicationContext.filesDir
                val file = File(fileDir, "project_document.pdf")
                val fileInputStream = requireContext().contentResolver.openInputStream(documentUri!!)
                val fileOutputStream = FileOutputStream(file)
                fileInputStream!!.copyTo(fileOutputStream)
                fileInputStream.close()
                fileOutputStream.close()

                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                val maxFileSizeInMB = 4 // MB

                if (fileSizeInMB > maxFileSizeInMB) {
                    Toast.makeText(requireContext(), "Ukuran file terlalu besar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                documentBody = MultipartBody.Part.createFormData(
                    "document_file",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )

            } else {
                Log.d("CreateProjectMedia", "onViewCreated: documentUri is null")
                val file = File(requireContext().applicationContext.filesDir, "project_document.pdf")

                documentBody = MultipartBody.Part.createFormData(
                    "document_file",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
            }


            retro.uploadAnotherProjectMedia(
                projectId = projectIdBody,
                videoLink = youtubeLinkBody,
                virtualTourName = virtualTourBody,
                virtualTourLink = virtualTourLinkBody,
                document_file = documentBody
            ).enqueue(object : Callback<UpdateProjectResponse> {
                override fun onResponse(
                    call: Call<UpdateProjectResponse>,
                    response: Response<UpdateProjectResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CreateProjectMedia", "onResponse: ${response.body()}")
                        Toast.makeText(requireActivity(), "Berhasil Menambahkan Media Proyek", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("CreateProjectMedia", "onResponse: ${response.body()}")
                        Toast.makeText(requireActivity(), "Gagal Menambahkan Media Proyek", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                    Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                    Toast.makeText(requireActivity(), "Gagal Menambahkan Media Proyek", Toast.LENGTH_SHORT).show()
                }

            })



            formActivity?.onNextButtonProjectManagementClick()
        }



    }

    private fun uploadPhotos(uri: Uri) {
        Log.d("CreateProjectMedia", "uploadPhotos: $uri")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val fileDir = requireContext().applicationContext.filesDir
                val file = File(fileDir, "project_photo.jpg")
                val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                val fileOutputStream = FileOutputStream(file)
                fileInputStream!!.copyTo(fileOutputStream)
                fileInputStream.close()
                fileOutputStream.close()

                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                val maxFileSizeInMB = 5 // MB

                if (fileSizeInMB > maxFileSizeInMB) {
                    Toast.makeText(requireContext(), "Ukuran gambar terlalu besar", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val files = MultipartBody.Part.createFormData(
                    "photo_file[]",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                val projectId = formActivity?.projectId.toString()
                val projectIdBody = projectId.toRequestBody("text/plain".toMediaTypeOrNull())

                developerApi.uploadMultipleFiles(projectIdBody, listOf(files) ).enqueue(object : Callback<PostStoreProjectPhotoResponse> {
                    override fun onResponse(call: Call<PostStoreProjectPhotoResponse>, response: Response<PostStoreProjectPhotoResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateProjectMedia", "onResponse: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchProjectPhotos(projectId.toInt())
                                imageUri = null
                            }
                        } else {
                            Log.e("CreateProjectMedia", "onResponse: ${response.body()?.message}, ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Gagal Mengunggah Foto", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PostStoreProjectPhotoResponse>, t: Throwable) {
                        Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                        Toast.makeText(context, "Gagal Mengunggah Foto", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

    }



    private suspend fun fetchProjectPhotos(projectId: Int) {
        withContext(Dispatchers.IO) {
            developerApi.getProjectDetail(projectId).enqueue(object : Callback<ProjectDetail> {
                override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                    if (response.isSuccessful) {
                        Log.d("CreateProjectMedia", "onResponse: ${response.body()}")
                        val data = response.body()?.data
                        val photos = data?.projectPhotos
                        if (photos != null) {
                            projectMediaViewModel.projectPhotos.value = photos.map {
                                LitePhotosModel(
                                    id = it.id,
                                    projectId = it.projectId,
                                    filePath = it.filename,
                                    isCover = it.isCover!!.toInt(),
                                    caption = it.caption,
                                )
                            }
                            Log.d("CreateProjectMedia", "onResponse Success: ${projectMediaViewModel.projectPhotos.value}")
                        }

                    } else {
                        Log.e("CreateProjectMedia", "onResponse: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                    Log.e("CreateProjectMedia", "onFailure: ${t.message}")
                }

            })

        }
    }



    private fun deleteAllPhotos() {
        val photos = projectMediaViewModel.projectPhotos.value?.toMutableList()
        var i = 0

        lifecycleScope.launch {
            if (photos != null) {
                while (photos.size > 1) {
                    val photo = photos[i]
                    if (photo.isCover != 1) {
                        deletePhoto(photo.id!!, false)
                        continue
                    }
                    i++
                }
            }

        }
    }



    private fun photosPreviewObserver() {
        projectMediaViewModel.projectPhotos.observe(viewLifecycleOwner) {
            Log.d("CreateProjectMedia", "photosPreviewObserver: $it")


            photosAdapter.photosList = it ?: emptyList()
            binding.recyclerViewListUnggahFoto.adapter = photosAdapter

            if (it != null && it.isNotEmpty()) {
                binding.recyclerViewListUnggahFoto.visibility = View.VISIBLE
            }
            else {
                binding.recyclerViewListUnggahFoto.visibility = View.GONE
            }
        }
    }

    private fun setListUnggahFotoRecycler() {
        binding.recyclerViewListUnggahFoto.apply{
            adapter = photosAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


    private fun pickPhoto() {
        val imageStoreIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        imageLauncher.launch(imageStoreIntent)
    }

    private fun pickDocument() {
        val documentStoreIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        documentLauncher.launch(documentStoreIntent)
    }

    private fun openContactUs() {
        // TOOD: Do something here
        Toast.makeText(context, "Open Contact Us : Belum Tersedia", Toast.LENGTH_SHORT).show()
    }

    private fun openTutorialVideo() {
        // TOOD: Do something here
        Toast.makeText(context, "Open Tutorial Video : Belum Tersedia", Toast.LENGTH_SHORT).show()
    }

    private fun validateYoutubeLinkObserver() {
        binding.editTextLinkYoutubeMediaProject.doAfterTextChanged { inputUrl ->
            if (inputUrl != null) {
                if (inputUrl.contains("youtube.com") || inputUrl.contains("youtu.be")) {
                    binding.editTextLinkYoutubeMediaProject.error = null
                } else {
                    binding.editTextLinkYoutubeMediaProject.error = "Link Youtube tidak valid"
                }
            }
        }

    }



}