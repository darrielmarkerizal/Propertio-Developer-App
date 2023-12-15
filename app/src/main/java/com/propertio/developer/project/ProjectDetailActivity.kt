package com.propertio.developer.project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.carousel.CarouselAdapter
import com.propertio.developer.carousel.ImageData
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.ActivityProjectDetailBinding
import com.propertio.developer.project.list.FileThumbnailAdapter
import com.propertio.developer.unit.UnitDetailActivity
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.list.UnitAdapter
import com.propertio.developer.unit_management.UpdateUnitResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ProjectDetailActivity : AppCompatActivity() {
    private var projectId: Int? = null

    private val binding by lazy {
        ActivityProjectDetailBinding.inflate(layoutInflater)
    }

    private val developerApi by lazy {
        Retro(TokenManager(this).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private val launcherToEditUnit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchDetailData(projectId!!)
            unitRecycler(projectId!!)
            Log.d("ProjectDetailActivity", "Unit updated successfully")
        }
    }

    private val listOfMonth by lazy {resources.getStringArray(R.array.list_of_months)}

    private lateinit var carouselAdapter: CarouselAdapter
    private val carouselList = ArrayList<ImageData>()
    private lateinit var dots : ArrayList<TextView>
    private lateinit var projectViewModel: ProjectViewModel

    private lateinit var unitAdapter: UnitAdapter
    private val unitList : MutableLiveData<List<ProjectDetail.ProjectDeveloper.ProjectUnit>> by lazy {
        MutableLiveData<List<ProjectDetail.ProjectDeveloper.ProjectUnit>>()
    }

    private val launcherToCreateUnit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("ProjectDetailActivity", "Unit created successfully")
        }
    }

    private val launcherToDetailUnit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("ProjectDetailActivity", "Unit updated successfully")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarContainer.textViewTitle.text = "Detail Proyek"


        binding.floatingButtonBack.setOnClickListener {
            Log.d("ProjectDetailActivity", "Back button clicked")
            finish()
        }

        val factory = ProjectViewModelFactory(
            (application as PropertioDeveloperApplication).repository
        )
        projectViewModel = ViewModelProvider(this, factory)[ProjectViewModel::class.java]


        val idProject = intent.getIntExtra(PROJECT_ID, 0)
        Log.d("ProjectDetailActivity", "idProject: $idProject")

        val propertyType = intent.getStringExtra("Property Type")
        Log.d("ProjectDetailActivity", "Property Type: $propertyType")

        binding.buttonAddUnit.setOnClickListener {
            if (projectId != null && projectId != 0) {
                val intentToUnitForm = Intent(this, UnitFormActivity::class.java)
                intentToUnitForm.putExtra(PROJECT_ID, projectId)
                intentToUnitForm.putExtra("Property Type", propertyType)
                launcherToCreateUnit.launch(intentToUnitForm)
            } else {
                Log.w("ProjectDetailActivity", "projectId is null")
            }
        }

        if (idProject == 0) {
            Log.e("ProjectDetailActivity", "projectData is null")
            finishActivity(RESULT_CANCELED)
        } else {
            projectId = idProject
        }


        lifecycleScope.launch {
            var projectData =  projectViewModel.getProjectById(idProject)

            Log.i("ProjectDetailActivity", projectData.toString())

            loadLocalData(projectData)

            fetchDetailData(projectData.id)
            // Refresh ProjectData

            projectData =  projectViewModel.getProjectById(idProject)

            if ((projectData.addressLatitude != null) && (projectData.addressLongitude != null)) {
                binding.buttonOpenMaps.setOnClickListener {
                    Log.d("ProjectDetailActivity", "Open Maps button clicked")

                    val intentToMaps = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${projectData.addressLatitude},${projectData.addressLongitude}")
                    )
                    Log.d("ProjectDetailActivity", "intentToMaps: ${projectData.addressLatitude}, ${projectData.addressLongitude}")
                    startActivity(intentToMaps)

                }
            } else {
                Log.w("ProjectDetailActivity", "addressLatitude or addressLongitude is null")
            }
        }





        unitRecycler(idProject)
        observeUnits()








    }

    private fun observeUnits() {
        Log.d("ProjectDetailActivity", "observeUnits: $unitList")
        unitList.observe(this) {
            val adapter = unitAdapter
            binding.recyclerViewUnit.adapter = adapter
        }
    }


    private fun unitRecycler(id: Int) {
        Log.d("ProjectDetailActivity", "unitRecycler: $id")


        developerApi.getProjectDetail(id).enqueue(object: Callback<ProjectDetail> {
            override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                // TODO: Handle Response

                if (response.isSuccessful) {
                    val project = response.body()?.data
                    if (project != null) {
                        unitList.postValue(project.units)
                        setUnitRecycler()
                        Log.i("ProjectDetailActivity", "Success adding unit. Units is not null")
                    }
                    else {
                        Log.w("ProjectDetailActivity", "project is null")
                    }
                }
                else {
                    Log.w("ProjectDetailActivity", "response is not successful")
                }


            }

            override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                // TODO: Handle Failure
                Log.e("ProjectDetailActivity", "onFailure: $t")



            }


        })


    }


    private fun setUnitRecycler() {
        Log.d("ProjectDetailActivity", "setUnitRecycler: $unitList")
        unitAdapter = UnitAdapter(
            unitList,
            onClickUnit = {
                if (projectId != null) {
                    Log.d("onClickUnitCard", "Unit clicked: ${it.id} & ${projectId}")
                    val intentToDetail = Intent(this, UnitDetailActivity::class.java)
                    intentToDetail.putExtra(PROJECT_DETAIL_UID, it.id)
                    intentToDetail.putExtra(PROJECT_DETAIL_PID, projectId)
                    launcherToDetailUnit.launch(intentToDetail)
                } else {
                    Log.w("onClickUnitCard", "projectId is null")
                }
            },
            onClickMore = { data, buttonBinding ->
                Log.d("onClickMore", "More clicked: ${data.id}")
                horizontalMoreButtonPopUp(data, buttonBinding)
            },
            onDelete = { data ->
                Log.d("onClickDelete", "Delete clicked: ${data.id}")
                deleteUnit(projectId!!, data.id!!)
            }
        )
        binding.recyclerViewUnit.apply {
            adapter = unitAdapter
            layoutManager = LinearLayoutManager(this@ProjectDetailActivity)
        }
    }


    private fun loadLocalData(project: ProjectTable) {
        with(binding) {
            textViewHeadline.text = project.headline
            textViewProjectTitle.text = project.title
            textViewCode.text = project.projectCode
            textViewDescription.text = project.description
            textViewAddress.text = addressFormatter(project)
        }
    }

    private fun fetchDetailData(id: Int) {
        Log.d("ProjectDetailActivity", "fetchDetailData: $id")
        developerApi.getProjectDetail(id).enqueue(object : Callback<ProjectDetail> {
            override fun onResponse(
                call: Call<ProjectDetail>,
                response: Response<ProjectDetail>
            ) {
                if (response.isSuccessful) {
                    val project = response.body()?.data
                    if (project != null) {
                        Log.i("ProjectDetailActivity", "project: $project")

                        loadTextBasedData(project)
                        loadTagsData(project)
                        loadVideo(project.projectVideos?.linkVideoURL)
                        loadImage(project.projectPhotos)
                        loadDocument(project.projectDocuments)

                        updateTable(project)

                        if (project.projectPhotos.isNullOrEmpty()) {
                            Log.w("ProjectDetailActivity", "projectPhotos is null")
                        } else {
                            binding.viewPagerCarousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    selectedDot(position)
                                    super.onPageSelected(position)
                                }
                            })

                            carouselAdapter = CarouselAdapter(carouselList)
                            binding.viewPagerCarousel.adapter = carouselAdapter
                            dots = ArrayList<TextView>()
                            setIndicator()

                        }

                    } else {
                        Log.w("ProjectDetailActivity", "project is null")
                    }
                } else {
                    Log.w("ProjectDetailActivity", "response is not successful")
                }
            }

            override fun onFailure(
                call: Call<ProjectDetail>,
                t: Throwable
            ) {
                Log.e("ProjectDetailActivity", "onFailure: $t")
            }
        })
    }

    private fun loadDocument(projectDocuments: List<ProjectDetail.ProjectDeveloper.ProjectDocument>?) {
        binding.containerCardFileThumbnail.apply {
            if (projectDocuments.isNullOrEmpty()) {
                Log.w("ProjectDetailActivity", "projectDocuments is null")
            } else {
                Log.d("ProjectDetailActivity", "projectDocuments: $projectDocuments")
                layoutManager = LinearLayoutManager(this@ProjectDetailActivity, LinearLayoutManager.VERTICAL, false)
                adapter = FileThumbnailAdapter(
                    projectDocuments = projectDocuments,
                    onClickFile = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(DomainURL.DOMAIN + it.filename)
                        startActivity(intent)
                    }
                )

            }
        }
    }

    private fun updateTable(project: ProjectDetail.ProjectDeveloper) {
        Log.w("ProjectDetailActivity", "updateTable: $project")
        projectViewModel.updateLocalProject(
            project.id!!,
            project.headline!!,
            project.description!!,
            project.certificate!!,
            project.address?.postalCode ?: "",
            project.address!!.latitude!!,
            project.address!!.longitude!!
        )


    }


    private fun loadImage(photos: List<ProjectDetail.ProjectDeveloper.ProjectPhoto>?) {
        if (!photos.isNullOrEmpty()) {
            for (photo in photos) {
                Log.d("ProjectDetailActivity", "photo: ${photo.filename}")

                val imageURL: String = DomainURL.DOMAIN + photo.filename
                Log.d("ProjectDetailActivity", "imageURL: $imageURL")

                if (!carouselList.any { it.id == photo.id.toString() }) {
                    carouselList.add(
                        ImageData(
                            photo.id.toString(),
                            imageURL
                        )
                    )
                }
            }

        } else {
            Log.w("ProjectDetailActivity", "photos is null")

            val drawableUri = Uri.parse("android.resource://$packageName/${R.drawable.placeholder}")
            val drawableUriString = drawableUri.toString()

            carouselList.add(
                ImageData(
                    "Locale_PlaceHolder_DP",
                    drawableUriString
                )
            )
        }

    }


    private fun loadVideo(videoUrl: String?) {
        Log.d("ProjectDetailActivity", "loadVideo: $videoUrl")
        if (videoUrl != null) {
            Log.d("ProjectDetailActivity", "video: ${videoUrl}")

            val webView: WebView = binding.webViewVideo

            val videoId = extractVideoIdFromUrl(videoUrl)
            if (videoId == "") {
                Log.w("ProjectDetailActivity", "videoId is empty")
                return@loadVideo
            }
            webView.settings.javaScriptEnabled = true
            webView.loadUrl("https://www.youtube.com/embed/$videoId")

            binding.groupVideoDetailProject.visibility = TextView.VISIBLE

        } else {
            Log.w("ProjectDetailActivity", "videos is null")
            binding.groupVideoDetailProject.visibility = TextView.GONE
        }

    }

    private fun extractVideoIdFromUrl(url: String): String {
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)

        return if (matcher.find()) {
            matcher.group()
        } else {
            Log.w("ProjectDetailActivity", "No video id found")
            ""
        }
    }

    private fun loadTagsData(project: ProjectDetail.ProjectDeveloper) {
        with(binding) {
            textViewPropertyType.text = project.propertyType?.name ?: textViewPropertyType.text.toString()
            textViewCertificateType.text = project.certificate

            val imageUrl = if (project.propertyType?.icon != null) {
                if (project.propertyType?.icon!!.startsWith("http")) {
                    project.propertyType?.icon
                } else {
                    DomainURL.DOMAIN + project.propertyType?.icon
                }
            } else {
                null
            }

            iconPropertyType.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.home)
                error(R.drawable.home)
            }
        }
    }


    private fun loadTextBasedData(project: ProjectDetail.ProjectDeveloper) {
        with(binding) {
            textViewHeadline.text = project.headline ?: textViewHeadline.text.toString()
            textViewProjectTitle.text = project.title ?: textViewProjectTitle.text.toString()
            textViewCode.text = project.projectCode ?: textViewCode.text.toString()
            textViewDescription.text = project.description ?: textViewDescription.text.toString()
            textViewCompletedAt.text = project.completedAt ?: textViewCompletedAt.text.toString()
            Log.i("ProjectDetailActivity", "loadTextBasedData Without Formatter Success")
            textViewViews.text = NumericalUnitConverter.unitFormatter(project.countViews ?: 0, false)
            textViewPublished.text = datesFormatter(project.createdAt)
            textViewAddress.text = addressFormatter(project.address)

        }
        Log.i("ProjectDetailActivity", "loadTextBasedData Success")

    }

    private fun addressFormatter(address: ProjectDetail.ProjectDeveloper.ProjectAddress?): String {
        return if (address != null) {
            "${address.address}, ${address.district}, ${address.city}, ${address.province}, ${address.postalCode}"
        } else {
            Log.w("ProjectDetailActivity", "address is null")
            "Address not found"
        }
    }
    private fun addressFormatter(project: ProjectTable) : String {
        return "${project.addressAddress}, ${project.addressDistrict}, ${project.addressCity}, ${project.addressProvince}, ${project.addressPostalCode}"
    }

    private fun datesFormatter(createDate: String?): String {
        val dates = createDate ?: "0000-01-01 00:00:00"
        Log.d("ProjectDetailActivity", "dates: $dates")

        val date = dates.split(" ", "T")[0].split("-")
        val year = date[0]
        val month = listOfMonth[date[1].toInt() -1]
        val day = date[2]

        return "$day $month $year"

    }

    private fun selectedDot(position: Int) {
        for (i in 0 until carouselList.size) {
            if (i == position) {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.black))
            } else {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.grey_500))
            }
        }
    }

    private fun setIndicator() {
        dots.clear()
        binding.dotsIndicator.removeAllViews()
        for (i in 0 until carouselList.size) {
            dots.add(TextView(this))
            dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }


    private fun horizontalMoreButtonPopUp(data: ProjectDetail.ProjectDeveloper.ProjectUnit, button: View, TAG : String = "PopUpMoreButton") {
        val popupInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = popupInflater.inflate(R.layout.dialog_pop_up_unit, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)


        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Get the menu items
        val buttonEdit = popupView.findViewById<AppCompatButton>(R.id.button_edit_unit_pop_up)

        buttonEdit.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: buttonEdit ${data.title}")

            val intentToEdit = Intent(this, UnitFormActivity::class.java)
            intentToEdit.putExtra(UnitFormActivity.PROJECT_DETAIL_UID, data.id)
            intentToEdit.putExtra(UnitFormActivity.PROJECT_DETAIL_PID, projectId)
            launcherToEditUnit.launch(intentToEdit)
            popupWindow.dismiss()
        }

        val buttonDelete = popupView.findViewById<AppCompatButton>(R.id.button_delete_unit_pop_up)

        buttonDelete.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: buttonDelete ${data.title}")
            deleteUnit(projectId!!, data.id!!)
            popupWindow.dismiss()
        }


        val dpValue = 111 // width in dp
        val scale = resources.displayMetrics.density
        val px =0 - (dpValue * scale + 0.5f).toInt()
        Log.d(TAG, "horizontalMoreButtonPopUp: px: $px")
        popupWindow.showAsDropDown(button, px, 0)

        popupView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the global layout listener
                popupView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Calculate the x-offset
                val xOffset = button.width - popupView.width

                popupWindow.update(button, xOffset, 0, -1, -1)
            }
        })
    }


    companion object {
        // NAME
        const val PROJECT_ID = "project_id"
        const val PROJECT_DETAIL_UID = "project_detail_uid"
        const val PROJECT_DETAIL_PID = "project_detail_pid"
    }

    private fun deleteUnit(projectId: Int, unitId: Int) {
        developerApi.deleteUnit(projectId, unitId).enqueue(object : Callback<UpdateUnitResponse> {
            override fun onResponse(call: Call<UpdateUnitResponse>, response: Response<UpdateUnitResponse>) {
                if (response.isSuccessful) {
                    Log.d("ProjectDetailActivity", "Unit deleted successfully")
                    Toast.makeText(this@ProjectDetailActivity, "Unit deleted successfully", Toast.LENGTH_SHORT).show()

                    fetchDetailData(projectId)
                    unitRecycler(projectId)
                } else {
                    Log.e("ProjectDetailActivity", "Failed to delete unit: ${response.errorBody()}")
                    Toast.makeText(this@ProjectDetailActivity, "Failed to delete unit", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateUnitResponse>, t: Throwable) {
                Log.e("ProjectDetailActivity", "Failed to delete unit: ${t.message}")
                Toast.makeText(this@ProjectDetailActivity, "Failed to delete unit", Toast.LENGTH_SHORT).show()
            }
        })
    }



}