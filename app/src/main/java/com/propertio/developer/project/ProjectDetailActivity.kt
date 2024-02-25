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
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.api.developer.unitmanagement.UnitListResponse
import com.propertio.developer.api.developer.unitmanagement.UnitOrderRequest
import com.propertio.developer.carousel.CarouselAdapter
import com.propertio.developer.carousel.ImageData
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.ActivityProjectDetailBinding
import com.propertio.developer.dialog.DialogUnitLaku
import com.propertio.developer.project.form.ProjectFormActivity
import com.propertio.developer.project.list.FileThumbnailAdapter
import com.propertio.developer.unit.UnitDetailActivity
import com.propertio.developer.unit.form.UnitFormActivity
import com.propertio.developer.unit.list.UnitAdapter
import com.propertio.developer.unit_management.UpdateUnitResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            updateUnit()
            Log.d("ProjectDetailActivity", "Unit updated successfully")
        }
    }

    private val launcherToEditProject = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchDetailData(projectId!!)
            Log.d("ProjectDetailActivity", "Project updated successfully")
        }
    }

    private val listOfMonth by lazy {resources.getStringArray(R.array.list_of_months)}

    private lateinit var carouselAdapter: CarouselAdapter
    private val carouselList = ArrayList<ImageData>()
    private lateinit var dots : ArrayList<TextView>
    private lateinit var projectViewModel: ProjectViewModel
    private var mapsLink: String = ""

    private fun createDeleteConfirmationDialog(
        projectId: Int,
        unitId: Int
    ) = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
        .setTitle("Hapus Unit")
        .setMessage("Apakah Anda yakin ingin menghapus unit ini?")
        .setPositiveButton("Ya") { dialog, _ ->
            deleteUnit(projectId, unitId)
            dialog.dismiss()
        }
        .setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

    private val unitAdapter by lazy {
        UnitAdapter(
            onClickUnit = {
                if (projectId != null) {
                    Log.d("onClickUnitCard", "Unit clicked: ${it.id} & ${projectId}")
                    val intentToDetail = Intent(this, UnitDetailActivity::class.java)
                    intentToDetail.putExtra(PROJECT_DETAIL_UID, it.id)
                    intentToDetail.putExtra(PROJECT_DETAIL_PID, projectId)
                    intentToDetail.putExtra(PROJECT_MAPS_LINK, mapsLink)
                    intentToDetail.putExtra(PROJECT_ADDRESS, binding.textViewAddress.text.toString())
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
                createDeleteConfirmationDialog(projectId!!, data.id!!).show()
            },
            onClickUnitLaku = { data ->
                Log.d("onClickUnitLaku", "Unit Laku clicked: ${data.id}")
                val unitStock = data.stock ?: "0"

                DialogUnitLaku(
                    stokUnit = unitStock.toInt(),
                    unitLakuListener = {
                        lifecycleScope.launch {
                            val unitOrderRequest = UnitOrderRequest(
                                unitId = data.id!!,
                                orderCount = it
                            )

                            try {
                                val response = withContext(Dispatchers.IO) {developerApi.updateUnitOrder(projectId!!, unitOrderRequest).execute()}
                                if (response.isSuccessful) {
                                    Toast.makeText(this@ProjectDetailActivity, "Berhasil melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                                    updateUnit()
                                } else {
                                    Toast.makeText(this@ProjectDetailActivity, "Gagal melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                                    Log.e("ProjectDetailActivity", "Failed to update unit order: ${response.errorBody()}")
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@ProjectDetailActivity, "Gagal melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                                Log.e("ProjectDetailActivity", "Failed to update unit order: ${e.message}")
                            }


                        }
                    }
                ).show(supportFragmentManager, "DialogUnitLaku")
            }
        )
    }

    private val launcherToCreateUnit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            updateUnit()
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
                intentToUnitForm.putExtra(PROJECT_DETAIL_PID, projectId)
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

            projectData = withContext(Dispatchers.IO) {projectViewModel.getProjectById(idProject)}

            Log.d("ProjectDetailActivity", "projectData Location Coordinate: ${projectData.addressLatitude}, ${projectData.addressLongitude}")
            if ((projectData.addressLatitude != null) && (projectData.addressLongitude != null)) {
                binding.buttonOpenMaps.setOnClickListener {
                    Log.d("ProjectDetailActivity", "Open Maps button clicked")
                    mapsLink = "https://www.google.com/maps/search/?api=1&query=${projectData.addressLatitude},${projectData.addressLongitude}"
                    val intentToMaps = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(mapsLink)
                    )
                    Log.d("ProjectDetailActivity", "intentToMaps: ${projectData.addressLatitude}, ${projectData.addressLongitude}")
                    startActivity(intentToMaps)

                }
            } else {
                Log.w("ProjectDetailActivity", "addressLatitude or addressLongitude is null")
            }
        }





        unitRecycler(idProject)

        buttonMore()



        binding.swipeRefreshLayoutDetailProject.setOnRefreshListener {
            lifecycleScope.launch {
                refreshPage()
                binding.swipeRefreshLayoutDetailProject.isRefreshing = false
            }
        }



    }

    private fun refreshPage() {
        if (projectId == null) {
            Log.w("ProjectDetailActivity", "projectId is null")
            return
        }
        fetchDetailData(projectId!!)
    }



    private fun buttonMore() {
        binding.toolbarContainer.buttonMore.visibility = View.VISIBLE

        binding.toolbarContainer.buttonMore.setOnClickListener {
            horizontalMoreButtonPopUpDetailProject(binding.toolbarContainer.buttonMore, 0)
        }
    }

    private fun updateUnit() {
        developerApi.getUnitsList(projectId!!).enqueue(object: Callback<UnitListResponse> {
            override fun onResponse(call: Call<UnitListResponse>, response: Response<UnitListResponse>) {

                if (response.isSuccessful) {
                    val unitList = response.body()?.data
                    if (unitList != null) {
                        unitAdapter.submitList(unitList)
                        Log.i("ProjectDetailActivity", "Success adding unit. Units is not null ${unitList.size} ${unitList.size}")
                    }
                    else {
                        Log.w("ProjectDetailActivity", "project is null")
                    }
                }
                else {
                    Log.w("ProjectDetailActivity", "response is not successful")
                }


            }

            override fun onFailure(call: Call<UnitListResponse>, t: Throwable) {
                // TODO: Handle Failure
                Log.e("ProjectDetailActivity", "onFailure: $t")



            }


        })

    }


    private fun unitRecycler(id: Int) {
        Log.d("ProjectDetailActivity", "unitRecycler: $id")


        developerApi.getProjectDetail(id).enqueue(object: Callback<ProjectDetail> {
            override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {

                if (response.isSuccessful) {
                    val project = response.body()?.data
                    if (project != null) {
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
        binding.recyclerViewUnit.apply {
            adapter = unitAdapter
            layoutManager = LinearLayoutManager(this@ProjectDetailActivity)
        }
        updateUnit()
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
                        setupYoutubePlayerVideo(project.projectVideos?.linkVideoURL)
                        loadImage(project.projectPhotos)
                        loadDocument(project.projectDocuments)

                        updateTable(project)

                        if (project.address?.latitude != null && project.address?.longitude != null) {
                            mapsLink = "https://www.google.com/maps/search/?api=1&query=${project.address!!.latitude!!},${project.address!!.longitude!!}"

                            binding.buttonOpenMaps.setOnClickListener {
                                Log.d("ProjectDetailActivity", "Open Maps button clicked")
                                val intentToMaps = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(mapsLink)
                                )
                                startActivity(intentToMaps)
                            }

                        } else {
                            Log.w("ProjectDetailActivity", "addressLatitude or addressLongitude is null")
                        }

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

                            updateUnit()
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

    private fun setupYoutubePlayerVideo(videoUrl: String?) {
        val youtubePlayer = binding.projectDetailYoutubePlayer

        youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.i("setupYoutubePlayerVideo", "YoutubePlayer ready : $videoUrl")
                val videoId = videoUrl?.extractVideoIdFromUrl()

                if (videoId == null) {
                    Log.w("setupYoutubePlayerVideo", "videoId is null")
                    youtubePlayer.visibility = View.GONE
                    return
                } else {
                    youtubePlayer.visibility = View.VISIBLE
                }



                youtubePlayer.visibility = View.VISIBLE
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                Log.e("ArtikelFragment", "YoutubePlayer error: $error")
                youtubePlayer.visibility = View.GONE
            }
        })
    }


    private fun String.extractVideoIdFromUrl(): String? {
        if (this.isEmpty()) return null

        // Youtube Filter
        val youtubeVideoID = if (this.contains("v=") && this.contains("youtube.com")) {
            val c1 = this.split("v=")[1]

            if (c1.contains("&")) {
                c1.split("&")[0]
            } else {
                c1
            }

        } else if(this.contains("youtube.com/embed")) {
            val c1 = this.split("youtube.com/embed/")[1]

            if (c1.contains("?")) {
                c1.split("?")[0]
            } else {
                c1
            }
        } else if (this.contains("youtu.be")){
            val c1 = this.split("youtu.be/")[1]

            if (c1.contains("?")) {
                c1.split("?")[0]
            } else {
                c1
            }
        }  else {
            null
        }


        return youtubeVideoID
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


    private fun horizontalMoreButtonPopUp(data: ProjectDetail.ProjectDeveloper.ProjectUnit, button: View, dpValue : Int = 111, TAG : String = "PopUpMoreButton") {
        val popupInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = popupInflater.inflate(R.layout.dialog_pop_up_unit, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)


        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Get the menu items
        val buttonEdit = popupView.findViewById<AppCompatButton>(R.id.button_edit_unit_pop_up)

        buttonEdit.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: " +
                    "\nbuttonEdit ${data.title}, " +
                    "\nuid ${data.id}, " +
                    "\npid $projectId")

            val intentToEdit = Intent(this, UnitFormActivity::class.java)
            intentToEdit.putExtra(PROJECT_DETAIL_UID, data.id)
            intentToEdit.putExtra(PROJECT_DETAIL_PID, projectId)
            launcherToEditUnit.launch(intentToEdit)
            popupWindow.dismiss()
        }

        val buttonDelete = popupView.findViewById<AppCompatButton>(R.id.button_delete_unit_pop_up)

        buttonDelete.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: buttonDelete ${data.title}")
            popupWindow.dismiss()

            createDeleteConfirmationDialog(projectId!!, data.id!!).show()
        }

        val isiUnitLaku = popupView.findViewById<TextView>(R.id.button_isi_unit_laku_pop_up)

        isiUnitLaku.setOnClickListener {
            popupWindow.dismiss()

            val unitStock = data.stock ?: "0"

            DialogUnitLaku(
                stokUnit = unitStock.toInt(),
                unitLakuListener = {
                    lifecycleScope.launch {
                        val unitOrderRequest = UnitOrderRequest(
                            unitId = data.id!!,
                            orderCount = it
                        )

                        try {
                            val response = withContext(Dispatchers.IO) {developerApi.updateUnitOrder(projectId!!, unitOrderRequest).execute()}
                            if (response.isSuccessful) {
                                Toast.makeText(this@ProjectDetailActivity, "Berhasil melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                                updateUnit()
                            } else {
                                Toast.makeText(this@ProjectDetailActivity, "Gagal melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                                Log.e("ProjectDetailActivity", "Failed to update unit order: ${response.errorBody()}")
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@ProjectDetailActivity, "Gagal melakukan update Order Unit", Toast.LENGTH_SHORT).show()
                            Log.e("ProjectDetailActivity", "Failed to update unit order: ${e.message}")
                        }


                    }
                }
            ).show(supportFragmentManager, "DialogUnitLaku")

        }


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

    private fun horizontalMoreButtonPopUpDetailProject(button: View, dpValue : Int = 111, TAG : String = "PopUpMoreButton") {
        val popupInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = popupInflater.inflate(R.layout.dialog_pop_up_project, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)


        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupView.findViewById<View>(R.id.divider_status_proyek_pop_up).visibility = View.GONE
        popupView.findViewById<View>(R.id.text_view_status_proyek_pop_up).visibility = View.GONE
        popupView.findViewById<View>(R.id.switch_proyek_pop_up).visibility = View.GONE


        // Get the menu items
        val buttonEdit = popupView.findViewById<AppCompatButton>(R.id.button_edit_proyek_pop_up)

        buttonEdit.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: buttonEdit $projectId")

            val intentToEdit = Intent(this@ProjectDetailActivity, ProjectFormActivity::class.java)
            intentToEdit.putExtra("EDIT_PROJECT", projectId)
            launcherToEditProject.launch(intentToEdit)
            popupWindow.dismiss()
        }

        val buttonRepost = popupView.findViewById<AppCompatButton>(R.id.button_repost_proyek_pop_up)

        buttonRepost.setOnClickListener {
            popupWindow.dismiss()
            projectId?.let {

                developerApi.repostProject(projectId!!).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(
                        call: Call<UpdateProjectResponse>,
                        response: Response<UpdateProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "onResponse: ${response.body()?.message}")
                        } else {
                            Log.d(TAG, "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ", t)
                    }

                })

            }


        }


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
        const val PROJECT_MAPS_LINK = "project_maps_link"
        const val PROJECT_ADDRESS = "project_address"
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