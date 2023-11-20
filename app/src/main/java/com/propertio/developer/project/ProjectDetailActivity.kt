package com.propertio.developer.project

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL
import com.propertio.developer.carousel.CarouselAdapter
import com.propertio.developer.carousel.ImageData
import com.propertio.developer.databinding.ActivityProjectDetailBinding
import com.propertio.developer.model.Project
import com.propertio.developer.model.ProjectAddress
import com.propertio.developer.model.ProjectPhoto
import com.propertio.developer.model.ProjectVideo
import java.util.regex.Pattern

class ProjectDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityProjectDetailBinding.inflate(layoutInflater)
    }
    private val listOfNumericalUnit by lazy {resources.getStringArray(R.array.list_of_numerical_unit)}
    private val listOfMonth by lazy {resources.getStringArray(R.array.list_of_months)}

    private lateinit var carouselAdapter: CarouselAdapter
    private val carouselList = ArrayList<ImageData>()
    private lateinit var dots : ArrayList<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarContainer.textViewTitle.text = "Detail Proyek"







        binding.floatingButtonBack.setOnClickListener {
            Log.d("ProjectDetailActivity", "Back button clicked")
            finish()
        }




        val projectJson = intent.getStringExtra(PROJECT_DATA)
        val projectData = Gson().fromJson(projectJson, Project::class.java)

        if (projectData == null) {
            Log.e("ProjectDetailActivity", "projectData is null")
            finishActivity(RESULT_CANCELED)
        }


        Log.i("ProjectDetailActivity", projectData.toString())
        loadTextBasedData(projectData)
        loadTagsData(projectData)
        loadVideo(projectData.projectVideos)
        loadImage(projectData.projectPhotos)

        if (projectData.projectPhotos.isNullOrEmpty()) {
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


        binding.buttonOpenMaps.setOnClickListener {
            Log.d("ProjectDetailActivity", "Open Maps button clicked")

            projectData.address?.let { addressData ->
                val intentToMaps = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${addressData.addressLatitude},${addressData.addressLongitude}")
                )
                Log.d("ProjectDetailActivity", "intentToMaps: $addressData")
                startActivity(intentToMaps)
            }
        }








    }

    private fun loadImage(photos: List<ProjectPhoto>?) {
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


    private fun loadVideo(videos: List<ProjectVideo>?) {
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

    private fun loadTagsData(project: Project) {
        with(binding) {
            textViewPropertyType.text = project.propertyType
            textViewCertificateType.text = project.certificate

            // TODO: Buat fungsi untuk mengganti icon property type
        }
    }


    private fun loadTextBasedData(project: Project) {
        with(binding) {
            textViewHeadline.text = project.headline
            textViewProjectTitle.text = project.title
            textViewCode.text = project.projectCode
            textViewDescription.text = project.description
            textViewCompletedAt.text = project.completedAt
            Log.i("ProjectDetailActivity", "loadTextBasedData Without Formatter Success")

            textViewViews.text = viewsFormatter(project.countViews)
            textViewPublished.text = datesFormatter(project.postedAt)
            textViewAddress.text = addressFormatter(project.address)

        }
        Log.i("ProjectDetailActivity", "loadTextBasedData Success")

    }

    private fun addressFormatter(address: ProjectAddress?): String {
        return if (address != null) {
            "${address.addressAddress}, ${address.addressDistrict}, ${address.addressCity}, ${address.addressProvince}"
        } else {
            Log.w("ProjectDetailActivity", "address is null")
            "Address not found"
        }
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

    private fun viewsFormatter(viewsIntUnSure: Int?) : String {
        val viewsInt = viewsIntUnSure ?: 0
        val views = viewsInt.toString()
        Log.i("ProjectDetailActivity", "views: $views")

        return if (views.length > 12) {
            views.substring(0, views.length - 12) + listOfNumericalUnit[3]
        } else if (views.length > 9) {
            views.substring(0, views.length - 9) + listOfNumericalUnit[2]
        } else if (views.length > 6) {
            views.substring(0, views.length - 6) + listOfNumericalUnit[1]
        } else if (views.length > 3) {
            views.substring(0, views.length - 3) + listOfNumericalUnit[0]
        } else {
            views
        }

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
        const val PROJECT_DATA = "project_data"
    }



}