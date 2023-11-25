package com.propertio.developer.project

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
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
import com.propertio.developer.unit.list.UnitAdapter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.regex.Pattern

class ProjectDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityProjectDetailBinding.inflate(layoutInflater)
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

        if (idProject == 0) {
            Log.e("ProjectDetailActivity", "projectData is null")
            finishActivity(RESULT_CANCELED)
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
            val adapter = UnitAdapter(this, unitList)
            binding.recyclerViewUnit.adapter = adapter
        }
    }


    private fun unitRecycler(id: Int) {
        Log.d("ProjectDetailActivity", "unitRecycler: $id")
        val retro = Retro(TokenManager(this).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.getProjectDetail(id).enqueue(object: Callback<ProjectDetail> {
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
        unitAdapter = UnitAdapter(this, unitList)
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
        val retro = Retro(TokenManager(this).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.getProjectDetail(id).enqueue(object : retrofit2.Callback<ProjectDetail> {
            override fun onResponse(
                call: retrofit2.Call<ProjectDetail>,
                response: retrofit2.Response<ProjectDetail>
            ) {
                if (response.isSuccessful) {
                    val project = response.body()?.data
                    if (project != null) {
                        Log.i("ProjectDetailActivity", "project: $project")

                        loadTextBasedData(project)
                        loadTagsData(project)
                        loadVideo(project.projectVideos)
                        loadImage(project.projectPhotos)

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
                call: retrofit2.Call<ProjectDetail>,
                t: Throwable
            ) {
                Log.e("ProjectDetailActivity", "onFailure: $t")
            }
        })
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

                carouselList.add(
                    ImageData(imageURL)
                )
            }

        } else {
            Log.w("ProjectDetailActivity", "photos is null")

            val drawableUri = Uri.parse("android.resource://$packageName/${R.drawable.placeholder}")
            val drawableUriString = drawableUri.toString()

            carouselList.add(
                ImageData(drawableUriString)
            )
        }

    }


    private fun loadVideo(videos: List<ProjectDetail.ProjectDeveloper.ProjectVideo>?) {
        if (videos != null) {
            var found = false
            for (video in videos) {
                Log.d("ProjectDetailActivity", "video: ${video.linkVideoURL}")

                if (video.linkVideoURL != null) {
                    val webView: WebView = binding.webViewVideo

                    val videoId = extractVideoIdFromUrl(video.linkVideoURL!!)
                    if (videoId == "") {
                        Log.w("ProjectDetailActivity", "videoId is empty")
                        break
                    }

                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl("https://www.youtube.com/embed/$videoId")
                    found = true
                    break
                }
            }
            if (!found) {
                Log.w("ProjectDetailActivity", "No video found")
                binding.groupVideoDetailProject.visibility = TextView.GONE
            }
        } else {
            Log.w("ProjectDetailActivity", "videos is null")
            binding.groupVideoDetailProject.visibility = TextView.GONE
        }

    }

    fun extractVideoIdFromUrl(url: String): String {
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
            textViewPropertyType.text = project.propertyType
            textViewCertificateType.text = project.certificate

            // TODO: Buat fungsi untuk mengganti icon property type
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
            textViewPublished.text = datesFormatter(project.postedAt)
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

        val date = dates!!.split(" ")[0].split("-")
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
        for (i in 0 until carouselList.size) {
            dots.add(TextView(this))
            dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    companion object {
        // NAME
        const val PROJECT_ID = "project_id"
    }



}