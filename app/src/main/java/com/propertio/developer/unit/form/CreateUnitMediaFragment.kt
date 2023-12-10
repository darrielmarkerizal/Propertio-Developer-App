package com.propertio.developer.unit.form

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostStoreUnitPhotoResponse
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse
import com.propertio.developer.databinding.FragmentCreateUnitMediaBinding
import com.propertio.developer.model.Caption
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.unit.UnitMediaViewModel
import com.propertio.developer.unit.list.UnggahFotoAdapter
import com.propertio.developer.unit_management.UpdateUnitResponse
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

class CreateUnitMediaFragment : Fragment() {

    private val binding by lazy { FragmentCreateUnitMediaBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as UnitFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private val unitMediaViewModdel : UnitMediaViewModel by activityViewModels()

    private lateinit var photosAdapter: UnggahFotoAdapter
    private lateinit var denahAdapter: UnggahFotoAdapter

    private var documentUri : Uri? = null
    private var documentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            documentUri = result.data?.data

            if (documentUri == null) {
               Log.e("CreateUnitMediaFragment", "documentUri is null")
               return@registerForActivityResult
            }
            Log.d("CreateUnitMediaFragment", "documentUri: $documentUri")
            binding.cardDocumentUnitPropertyThumbnail.apply {
                this.root.visibility = View.VISIBLE
                textViewFilename.text = documentUri?.lastPathSegment?.split("/")?.last() ?: documentUri?.lastPathSegment
            }
        }
    }

    private var imageUri : Uri? = null
    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data

            if (imageUri == null) {
                Log.e("CreateUnitMediaFragment", "imageUri is null")
                return@registerForActivityResult
            }

            Log.d("CreateUnitMediaFragment", "imageUri: $imageUri")
            uploadPhotos(imageUri!!)
        }
    }

    private var denahUri : Uri? = null
    private val denahLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            denahUri = result.data?.data

            if (denahUri == null) {
                Log.e("CreateUnitMediaFragment", "denahUri is null")
                return@registerForActivityResult
            }

            Log.d("CreateUnitMediaFragment", "denahUri: $denahUri")
            uploadDenah(denahUri!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photosAdapter = UnggahFotoAdapter(
            photosList = unitMediaViewModdel.unitPhoto.value ?: listOf(),
            onClickButtonCover = {
                if (it.projectId != null && it.id != null) {
                    updateCoverPhoto(it.projectId, it.id)
                } else {
                    Toast.makeText(context, "Gagal Mengubah Cover photo", Toast.LENGTH_SHORT).show()
                }
            },
            onClickDelete = {
                if (it.projectId != null && it.id != null) {
                    Log.d("CreateUnitMediaFragment", "onCreateView: ${it.projectId} and ${it.id}")
                    deletePhoto(it.projectId, it.id)
                } else {
                    Toast.makeText(context, "Gagal Menghapus Photo", Toast.LENGTH_SHORT).show()
                }
            },
            onClickSaveCaption = {
                if (it.projectId != null && it.id != null && it.caption != null) {
                    updateCaption(it.projectId, it.id, it.caption!!)
                } else {
                    Toast.makeText(context, "Gagal Mengubah Caption", Toast.LENGTH_SHORT).show()
                }
            }
        )

        denahAdapter = UnggahFotoAdapter(
            photosList = unitMediaViewModdel.unitDenah.value ?: listOf(),
            onClickButtonCover = {
                if (it.projectId != null && it.id != null) {
                    updateCoverPhoto(it.projectId, it.id)
                } else {
                    Toast.makeText(context, "Gagal Mengubah Cover photo", Toast.LENGTH_SHORT).show()
                }
            },
            onClickDelete = {
                if (it.projectId != null && it.id != null) {
                    Log.d("CreateUnitMediaFragment", "onCreateView: ${it.projectId} and ${it.id}")
                    deletePhoto(it.projectId, it.id)
                } else {
                    Toast.makeText(context, "Gagal Menghapus Photo", Toast.LENGTH_SHORT).show()
                }
            },
            onClickSaveCaption = {
                if (it.projectId != null && it.id != null && it.caption != null) {
                    updateCaption(it.projectId, it.id, it.caption!!)
                } else {
                    Toast.makeText(context, "Gagal Mengubah Caption", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return binding.root
    }

    private fun updateCaption(projectId: String, id: Int, caption: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.updateCaptionUnitPhoto(projectId , id, Caption(caption)).enqueue(object :
                    Callback<UpdateUnitResponse> {
                    override fun onResponse(call: Call<UpdateUnitResponse>, response: Response<UpdateUnitResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(projectId.toInt(), formActivity?.unitId ?: 0)
                                Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Gagal Menyimpan Caption", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                        Log.e("CreateUnitMediaFragment", "onFailure id $id: ${t.message}")
                    }
                })
            }
        }
    }

    private fun updateCoverPhoto(projectId: String, id: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.updateCoverUnitPhoto(projectId, id).enqueue(object :
                    Callback<UpdateUnitResponse> {
                    override fun onResponse(call: Call<UpdateUnitResponse>, response: Response<UpdateUnitResponse>) {
                        Log.d("CreateUnitMediaFragment", "API URL: ${call.request().url}")
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(projectId.toInt(), formActivity?.unitId ?: 0)
                                Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Gagal Mengubah Cover Photo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                        Log.e("CreateUnitMediaFragment", "onFailure id $id: ${t.message}")
                    }
                })
            }
        }
    }

    private fun deletePhoto(projectId: String, id: Int, isMessage: Boolean = true) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.deleteUnitPhoto(projectId, id).enqueue(object :
                    Callback<UpdateUnitResponse> {
                    override fun onResponse(call: Call<UpdateUnitResponse>, response: Response<UpdateUnitResponse>) {
                        Log.d("CreateUnitMediaFragment", "API URL: ${call.request().url}")
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()}")
                            Log.d("CreateUnitMediaFragment", "Photo deletion was successful")
                            lifecycleScope.launch {
                                fetchUnitPhotos(projectId.toInt(), formActivity?.unitId ?: 0)
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.errorBody()?.string()}")
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.message()}")
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.raw()}")
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.code()}")
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.headers()}")
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.errorBody()}")
                            Log.d("CreateUnitMediaFragment", "projectId: $projectId and unitId: $id")
                            Toast.makeText(context, "Gagal Menghapus Photo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                        Log.e("CreateUnitMediaFragment", "onFailure id $id: ${t.message}")
                    }
                })
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (documentUri == null) {
            binding.cardDocumentUnitPropertyThumbnail.root.visibility = View.GONE
        }

        binding.buttonUnggahPhotoUnit.setOnClickListener{
            pickPhoto()
        }

        binding.buttonUnggahDenahUnit.setOnClickListener{
            pickDenah()
        }

        setListUnggahFotoRecycler()
        photosPreviewObserver()
        denahPreviewObserver()

        binding.linkTextViewUnitPhotoDelete.setOnClickListener{
            deleteAllPhotos()
        }

        validateYoutubeLinkObserver()

        binding.linkTextUnitVideoTutorial.setOnClickListener{
            openTutorialVideo()
        }

        binding.buttonHubungiKamiMediaUnit.setOnClickListener{
            openContactUs()
        }

        binding.buttonHubungiKamiMediaUnit2.setOnClickListener {
            openContactUs()
        }

        binding.buttonTambahkanDokumenUnit.setOnClickListener{
            pickDocument()
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            formActivity?.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            val retro = Retro(TokenManager(requireContext()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)

            val projectId = formActivity?.unitFormViewModel?.projectId?.value.toString()
            val unitId = formActivity?.unitId ?: 0
            val youtubeLink = binding.editTextLinkYoutubeMediaUnit.text.toString()
            val virtualTour = binding.editLinkVirtualTourUnit.text.toString()
            val virtualTourLink = binding.editLinkVirtualTourUnit.text.toString()
            val modelLink = binding.editLinkModelUnit.text.toString()

            var unitIdBody = unitId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            var youtubeLinkBody = youtubeLink.toRequestBody("text/plain".toMediaTypeOrNull())
            var virtualTourBody = virtualTour.toRequestBody("text/plain".toMediaTypeOrNull())
            var virtualTourLinkBody = virtualTourLink.toRequestBody("text/plain".toMediaTypeOrNull())
            var modelLinkBody = modelLink.toRequestBody("text/plain".toMediaTypeOrNull())


            var documentBody : MultipartBody.Part? = null

            if (documentUri != null) {
                Log.d("CreateUnitMediaFragment", "onViewCreated: $documentUri")
                val fileDir = requireContext().applicationContext.filesDir
                val file = File(fileDir, "unit_document.pdf")
                val fileInputStream = requireContext().contentResolver.openInputStream(documentUri!!)
                val fileOutputStream = FileOutputStream(file)
                fileInputStream!!.copyTo(fileOutputStream)
                fileInputStream.close()
                fileOutputStream.close()

                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                val maxFileSizeInMB = 4

                if (fileSizeInMB > maxFileSizeInMB) {
                    Toast.makeText(requireContext(), "File terlalu besar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                documentBody = MultipartBody.Part.createFormData(
                    "document_file",
                    file.name,
                    file.asRequestBody("application/pdf".toMediaTypeOrNull())
                )
            } else {
                Log.d("CreateUnitMediaFragment", "onViewCreated: documentUri is null")
                val file = File(requireContext().applicationContext.filesDir, "unit_document.pdf")

                documentBody = MultipartBody.Part.createFormData(
                    "document_file",
                    file.name,
                    file.asRequestBody("application/pdf".toMediaTypeOrNull())
                )
            }

            retro.uploadAnotherUnitMedia(
                projectId = projectId,
                unitId = unitIdBody,
                videoLink = youtubeLinkBody,
                virtualTourName = virtualTourBody,
                virtualTourLink = virtualTourLinkBody,
                document_file = documentBody,
                modelLink = modelLinkBody
            ).enqueue(object : Callback<UpdateUnitResponse> {
                override fun onResponse(
                    call: Call<UpdateUnitResponse>,
                    response: Response<UpdateUnitResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                        Toast.makeText(requireActivity(), "Berhasil menambahkan media unit", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse: ${response.body()}")
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse 2: ${response.errorBody()?.string()}")
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse 3: ${response.message()}")
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse 4: ${response.raw()}")
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse 5: ${response.code()}")
                        Log.e("CreateUnitMediaFragment", "Error nya ada di onResponse 6: ${response.headers()}")

                        Toast.makeText(requireActivity(), "Gagal menambahkan mediaunit", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                    Log.e("CreateUnitMediaFragment", "onFailure: ${t.message}")
                    Toast.makeText(requireActivity(), "Gagal menambahkan media unit", Toast.LENGTH_SHORT).show()
                }
            })

            formActivity?.onNextButtonUnitManagementClick()

        }
    }

    private fun uploadPhotos(uri: Uri) {
        Log.d("CreateUnitMediaFragment", "uploadPhotos: $uri")

        val unitId = formActivity?.unitId ?: 0
        val unitIdBody = unitId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val projectId = formActivity?.unitFormViewModel?.projectId?.value ?: 0
        val projectIdBody = projectId.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val fileDir = requireContext().applicationContext.filesDir
                val file = File(fileDir, "unit_photo.jpg")
                val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                val fileOutputStream = FileOutputStream(file)
                fileInputStream!!.copyTo(fileOutputStream)
                fileInputStream.close()
                fileOutputStream.close()

                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                val maxFileSizeInMb = 5

                if (fileSizeInMB > maxFileSizeInMb) {
                    Toast.makeText(requireContext(), "File terlalu besar", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val files = MultipartBody.Part.createFormData(
                    "photo_file[]",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                developerApi.uploadUnitPhoto(
                    projectId = projectIdBody,
                    unitId = unitIdBody,
                    files = listOf(files)
                ).enqueue(object : Callback<PostStoreUnitPhotoResponse> {
                    override fun onResponse(
                        call: Call<PostStoreUnitPhotoResponse>,
                        response: Response<PostStoreUnitPhotoResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                            Log.d("CreateUnitMediaFragment", "Data yang dikirim unit photo: $files and $projectIdBody and $unitIdBody")
                            Log.d("CreateUnitMediaFragment", "projectId yang dikirim: $projectId and unitId: $unitId")
                            Log.d("CreateUnitMediaFragment", "Response code: ${response.code()}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(projectId, formActivity?.unitId ?: 0)
                                imageUri = null
                                Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse gagal: ${response.body()}")
                            Log.e("CreateUnitMediaFragment", "onResponse gagal 2: ${response.errorBody()?.string()}")
                            Log.e("CreateUnitMediaFragment", "onResponse gagal 3: ${response.message()}")
                            Log.e("CreateUnitMediaFragment", "onResponse gagal 4: ${response.raw()}")
                            Log.e("CreateUnitMediaFragment", "onResponse gagal 5: ${response.code()}")
                            Log.e("CreateUnitMediaFragment", "onResponse gagal 6: ${response.headers()}")
                            Log.d("CreateUnitMediaFragment", "projectId: $projectId and unitId: $unitId")

                            Toast.makeText(context, "Gagal Mengunggah Photo", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PostStoreUnitPhotoResponse>, t: Throwable) {
                        Log.e("CreateUnitMediaFragment", "onFailure: ${t.message}")
                        Toast.makeText(context, "Gagal Mengunggah Photo", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun uploadDenah(uri: Uri) {
        Log.d("CreateUnitMediaFragment", "uploadDenah: $uri")

        val unitId = formActivity?.unitId ?: 0
        val unitIdBody = unitId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val projectId = formActivity?.unitFormViewModel?.projectId?.value ?: 0
        val projectIdBody = projectId.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val fileDir = requireContext().applicationContext.filesDir
                val file = File(fileDir, "unit_denah.jpg")
                val fileInputStream = requireContext().contentResolver.openInputStream(uri)
                val fileOutputStream = FileOutputStream(file)
                fileInputStream!!.copyTo(fileOutputStream)
                fileInputStream.close()
                fileOutputStream.close()

                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                val maxFileSizeInMb = 5

                if (fileSizeInMB > maxFileSizeInMb) {
                    Toast.makeText(requireContext(), "File terlalu besar", Toast.LENGTH_SHORT)
                        .show()
                    return@withContext
                }

                val files = MultipartBody.Part.createFormData(
                    "photo_file[]",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                Log.d("CreateUnitMediaFragment", "uploadDenah: $files and $projectIdBody and $unitIdBody")

                developerApi.uploadUnitPlanImage(
                    projectId = projectIdBody,
                    unitId = unitIdBody,
                    type = "denah",
                    files = listOf(files)
                ).enqueue(object : Callback<PostStoreUnitPhotoResponse> {
                    override fun onResponse(
                        call: Call<PostStoreUnitPhotoResponse>,
                        response: Response<PostStoreUnitPhotoResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                            Log.d("CreateUnitMediaFragment", "Response code: ${response.code()}")
                            Log.d("CreateUnitMediaFragment", "Response headers: ${response.headers()}")
                            Log.d("CreateUnitMediaFragment", "projectId yang dikirim: $projectId and unitId: $unitId")
                            Log.d("CreateUnitMediaFragment", "Data yang dikirim: $files and $projectIdBody and $unitIdBody")
                            Log.d("CreateUnitMediaFragment", "Response raw: ${response.raw()}")
                            Log.d("CreateUnitMediaFragment", "Response message: ${response.message()}")
                            Log.d("CreateUnitMediaFragment", "Response errorBody: ${response.errorBody()}")
                            Log.d("CreateUnitMediaFragment", "unitDenah value: ${unitMediaViewModdel.unitDenah.value}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(projectId, formActivity?.unitId ?: 0)
                                Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse gagal: ${response.body()}")
                            Log.e(
                                "CreateUnitMediaFragment",
                                "onResponse gagal 2: ${response.errorBody()?.string()}"
                            )
                            Log.e(
                                "CreateUnitMediaFragment",
                                "onResponse gagal 3: ${response.message()}"
                            )
                            Log.e(
                                "CreateUnitMediaFragment",
                                "onResponse gagal 4: ${response.raw()}"
                            )
                            Log.e(
                                "CreateUnitMediaFragment",
                                "onResponse gagal 5: ${response.code()}"
                            )
                            Log.e(
                                "CreateUnitMediaFragment",
                                "onResponse gagal 6: ${response.headers()}"
                            )
                            Log.d(
                                "CreateUnitMediaFragment",
                                "projectId: $projectId and unitId: $unitId"
                            )

                            Toast.makeText(context, "Gagal Mengunggah Denah", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<PostStoreUnitPhotoResponse>, t: Throwable) {
                        Log.e("CreateUnitMediaFragment", "onFailure: ${t.message}")
                        Toast.makeText(context, "Gagal Mengunggah Denah", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private suspend fun fetchUnitPhotos(projectId: Int, unitId: Int) {
        withContext(Dispatchers.IO) {
            developerApi.getUnitDetail(projectId, unitId).enqueue(object : Callback<UnitDetailResponse> {
                override fun onResponse(
                    call: Call<UnitDetailResponse>,
                    response: Response<UnitDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CreateUnitMediaFragment", "onResponse: ${response.body()}")
                        val unitId = formActivity?.unitId.toString()
                        val data = response.body()?.data
                        val photos = data?.unitPhotos
                        if (photos != null) {
                            val unitPhotos = photos.filter { it.type == "photo" }
                            val denahPhotos = photos.filter { it.type == "denah" }
                            unitMediaViewModdel.unitPhoto.value = unitPhotos.map {
                                LitePhotosModel(
                                    id = it.id,
                                    projectId = formActivity?.unitFormViewModel?.projectId?.value.toString(),
                                    filePath = it.filename,
                                    isCover = it.isCover!!.toInt(),
                                    type = it.type,
                                    caption = it.caption
                                )
                            }
                            unitMediaViewModdel.unitDenah.value = denahPhotos.map {
                                LitePhotosModel(
                                    id = it.id,
                                    projectId = formActivity?.unitFormViewModel?.projectId?.value.toString(),
                                    filePath = it.filename,
                                    isCover = it.isCover!!.toInt(),
                                    type = it.type,
                                    caption = it.caption
                                )
                            }
                            Log.d("CreateUnitMediaFragment", "Foto dengan tipe photo ditemukan: ${unitMediaViewModdel.unitPhoto.value}")
                            Log.d("CreateUnitMediaFragment", "Foto dengan tipe denah ditemukan: ${unitMediaViewModdel.unitDenah.value}")
                            Log.d(
                                "CreateUnitMediaFragment",
                                "onResponse: ${unitMediaViewModdel.unitPhoto.value}"
                            )
                        }
                    } else {
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini: ${response.body()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 2: ${response.errorBody()?.string()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 3: ${response.message()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 4: ${response.raw()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 5: ${response.code()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 6: ${response.headers()}")
                        Log.e("CreateUnitMediaFragment", "Error onResponse disini 7: ${response.errorBody()}")
                    }
                }
                override fun onFailure(call: Call<UnitDetailResponse>, t: Throwable) {
                    Log.e("CreateUnitMediaFragment", "onFailure: ${t.message}")
                    t.printStackTrace()
                    Log.e("CreateUnitMediaFragment", "onFailure: ${t.stackTrace}")
                }
            })
        }
    }

    private fun deleteAllPhotos() {
        val photos = unitMediaViewModdel.unitPhoto.value?.toMutableList()
        var i = 0

        lifecycleScope.launch {
            if (photos != null) {
                while (photos.size > 1) {
                    val photo = photos[i]
                    if (photo.isCover != 1) {
                        deletePhoto(photo.projectId ?: "", photo.id ?: 0, false)
                        continue
                    }
                    i++
                }
            }
        }
    }

    private fun photosPreviewObserver() {
        unitMediaViewModdel.unitPhoto.observe(viewLifecycleOwner) {
            val sortedPhotos = it.sortedByDescending { photo -> photo.isCover }
            photosAdapter.photosList = sortedPhotos
            binding.recyclerViewListUnggahFoto.adapter = photosAdapter

            if (sortedPhotos.isNotEmpty()) {
                binding.recyclerViewListUnggahFoto.visibility = View.VISIBLE
            } else {
                binding.recyclerViewListUnggahFoto.visibility = View.GONE
            }
        }
    }

    private fun denahPreviewObserver() {
        unitMediaViewModdel.unitDenah.observe(viewLifecycleOwner) {
            val sortedPlan = it.sortedByDescending { plan -> plan.isCover  }
            denahAdapter.photosList = sortedPlan
            binding.recyclerViewListUnggahDenah.adapter = denahAdapter

            if (sortedPlan.isNotEmpty()) {
                binding.recyclerViewListUnggahDenah.visibility = View.VISIBLE
            } else {
                binding.recyclerViewListUnggahDenah.visibility = View.GONE
            }
        }
    }

    private fun setListUnggahFotoRecycler() {
        binding.recyclerViewListUnggahFoto.apply {
            adapter = photosAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.recyclerViewListUnggahDenah.apply {
            adapter = denahAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun pickPhoto() {
        val imageStoreIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        imageLauncher.launch(imageStoreIntent)
    }

    private fun pickDenah() {
        val imageStoreIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        denahLauncher.launch(imageStoreIntent)
    }



    private fun pickDocument() {
        val documentStoreIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        documentLauncher.launch(documentStoreIntent)
    }

    private fun validateYoutubeLinkObserver() {
        binding.editTextLinkYoutubeMediaUnit.doAfterTextChanged { inputUrl ->
            if (inputUrl != null) {
                if (inputUrl.contains("youtube.com") || inputUrl.contains("youtu.be")) {
                    binding.editTextLinkYoutubeMediaUnit.error = null
                } else {
                    binding.editTextLinkYoutubeMediaUnit.error = "Link Youtube tidak valid"
                }
            }
        }
    }

    private fun openContactUs() {
        val phoneNumber = "6285702750455"
        val url = "https://wa.me/$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun openTutorialVideo() {
        // TOOD: Do something here
        Toast.makeText(context, "Open Tutorial Video : Belum Tersedia", Toast.LENGTH_SHORT).show()
    }
}