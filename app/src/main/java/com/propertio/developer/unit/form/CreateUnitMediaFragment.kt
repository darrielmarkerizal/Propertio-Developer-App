package com.propertio.developer.unit.form

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.PostStoreUnitPhotoResponse
import com.propertio.developer.databinding.FragmentCreateUnitMediaBinding
import com.propertio.developer.model.Caption
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
    private val activityBinding by lazy { formActivity?.binding }

    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private val unitMediaViewModdel : UnitMediaViewModel by activityViewModels()

    private lateinit var photosAdapter: UnggahFotoAdapter

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
                                fetchUnitPhotos(formActivity?.unitId ?: 0)
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
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(formActivity?.unitId ?: 0)
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

    private fun deletePhoto(projectId: String, id: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.deleteUnitPhoto(projectId, id).enqueue(object :
                    Callback<UpdateUnitResponse> {
                    override fun onResponse(call: Call<UpdateUnitResponse>, response: Response<UpdateUnitResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateUnitMediaFragment", "onResponse id $id: ${response.body()?.message}")
                            lifecycleScope.launch {
                                fetchUnitPhotos(formActivity?.unitId ?: 0)
                            }
                        } else {
                            Log.e("CreateUnitMediaFragment", "onResponse id $id: ${response.errorBody()?.string()}")
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

    }

    private fun uploadPhotos(uri: Uri) {
        Log.d("CreateUnitMediaFragment", "uploadPhotos: $uri")
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
                    "photo_file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                val unitId = formActivity?.unitId ?: 0
                val unitIdBody = unitId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val projectId = formActivity?.unitFormViewModel.projectId.value ?: 0
                val projectIdBody = projectId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }
    }
}