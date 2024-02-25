package com.propertio.developer.unit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse
import com.propertio.developer.carousel.CarouselAdapter
import com.propertio.developer.carousel.ImageData
import com.propertio.developer.database.MasterData
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.database.MasterDataDeveloperPropertio.searchByDb
import com.propertio.developer.databinding.ActivityUnitDetailBinding
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ADDRESS
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_DETAIL_PID
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_DETAIL_UID
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_MAPS_LINK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnitDetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityUnitDetailBinding.inflate(layoutInflater)
    }

    private var unitId : Int = 0
    private var projectId : Int = 0

    private lateinit var carouselAdapter: CarouselAdapter
    private val carouselList = ArrayList<ImageData>()
    private lateinit var dots : ArrayList<TextView>

    private val developerApi by lazy {
        Retro(TokenManager(this).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarContainer.textViewTitle.text = "Detail Unit"

        binding.floatingButtonBack.setOnClickListener {
            Log.d("UnitDetailActivity", "onCreate: back")
            finish()
        }

        // Check if is it valid unit id
        unitId = intent.getIntExtra(PROJECT_DETAIL_UID, 0)
        projectId = intent.getIntExtra(PROJECT_DETAIL_PID, 0)
        val mapsLink = intent.getStringExtra(PROJECT_MAPS_LINK)
        val address = intent.getStringExtra(PROJECT_ADDRESS)

        Log.d("UnitDetailActivity", "onCreate: unitId $unitId")
        Log.d("UnitDetailActivity", "onCreate projectId $projectId")
        intent.removeExtra(PROJECT_DETAIL_UID)
        intent.removeExtra(PROJECT_DETAIL_PID)
        intent.removeExtra(PROJECT_MAPS_LINK)
        intent.removeExtra(PROJECT_ADDRESS)
        if (unitId == 0 && projectId == 0) {
            Log.w("UnitDetailActivity", "onCreate: unitId is 0")
            finish()
        }

        lifecycleScope.launch {
            fecthData()
        }

        binding.buttonOpenMaps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsLink))
            startActivity(intent)
        }

        binding.textViewAddressUnit.text = address

    }

    private suspend fun fecthData() {
        developerApi.getUnitDetail(projectId ,unitId).enqueue(object : Callback<UnitDetailResponse> {
            override fun onResponse(
                call: Call<UnitDetailResponse>,
                response: Response<UnitDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    Log.d("UnitDetailActivity", "onResponse: $data")

                    if (data != null) {
                        lifecycleScope.launch {
                            loadData(data)

                            if (data.unitPhotos.isNullOrEmpty()) {
                                Log.w("UnitDetailActivity", "projectPhotos is null")
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
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UnitDetailResponse>, t: Throwable) {
                Log.e("UnitDetailActivity", "onFailure: ${t.message}")
            }

        })

    }

    private suspend fun loadData(data: UnitDetailResponse.Unit) {
        loadTextBased(data)
        withContext(Dispatchers.IO) {
            loadImage(data.unitPhotos)
        }
        withContext(Dispatchers.Main) {
            Log.d("UnitDetailActivity", "loadVideo: ${data.unitVideo?.link}")
            setupYoutubePlayerVideo(data.unitVideo?.link)
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
        dots.clear()
        binding.dotsIndicator.removeAllViews()
        for (i in 0 until carouselList.size) {
            dots.add(TextView(this))
            dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private fun loadImage(photos: List<UnitDetailResponse.Unit.UnitPhoto>?) {
        if (!photos.isNullOrEmpty()) {
            for (photo in photos) {
                Log.d("UnitDetailActivity", "photo: ${photo.filename}")

                val imageURL: String = DomainURL.DOMAIN + photo.filename
                Log.d("UnitDetailActivity", "imageURL: $imageURL")

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
            Log.w("UnitDetailActivity", "photos is null")

            val drawableUri = Uri.parse("android.resource://$packageName/${R.drawable.placeholder}")
            val drawableUriString = drawableUri.toString()

            carouselList.add(
                ImageData(
                    "Locale_PlaceHolder_DU",
                    drawableUriString
                )
            )
        }
    }


    private fun setupYoutubePlayerVideo(videoUrl: String?) {
        val youtubePlayer = binding.unitDetailYoutubePlayer

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

    private fun String.getUserText(data : List<MasterData>) : String {
        return data.searchByDb(this)?.toUser ?: "Tidak Ada Informasi"
    }

    private fun loadTextBased(data: UnitDetailResponse.Unit) {
        with(binding) {
            val buildingArea = data.buildingArea
            val surfaceArea = data.surfaceArea
            val price = data.price
            val floor = data.floor
            val interior = data.interior?.getUserText(MasterDataDeveloperPropertio.interior)
            val powerSupply = data.powerSupply?.getUserText(MasterDataDeveloperPropertio.electricity)
            val garage = data.garage?.getUserText(MasterDataDeveloperPropertio.parking)
            val roadAccess = data.roadAccess?.getUserText(MasterDataDeveloperPropertio.roadAccess)
            val water = data.waterType?.getUserText(MasterDataDeveloperPropertio.water)

            textViewUnitCode.text = data.unitCode ?: "Tidak Ada Informasi"
            textViewTitleUnitDetail.text = data.title ?: "Tidak Ada Informasi"
            textViewDescriptionUnit.text = data.description ?: "Tidak Ada Informasi"
            textViewPropertyType.text = data.propertyType ?: "Tidak Ada Informasi"

            textViewBuildingArea.text = if (buildingArea == null || buildingArea == "0" || buildingArea.startsWith("Pilih")) "Tidak Ada Informasi" else NumericalUnitConverter.meterSquareFormatter(buildingArea)
            textViewSurfaceArea.text = if (surfaceArea == null || surfaceArea == "0" || surfaceArea.startsWith("Pilih")) "Tidak Ada Informasi" else NumericalUnitConverter.meterSquareFormatter(surfaceArea)
            textViewStaircase.text = if (floor == null || floor == "0" || floor.startsWith("Pilih")) "Tidak Ada Informasi" else floor
            textViewBed.text = data.bedroom ?: "Tidak Ada Informasi"
            textViewBath.text = data.bathroom ?: "Tidak Ada Informasi"
            textViewGarage.text = if (garage == null || garage == "0" || garage.startsWith("Pilih")) "Tidak Ada Informasi" else garage
            textViewPower.text = if (powerSupply == null || powerSupply == "0" || powerSupply.startsWith("Pilih")) "Tidak Ada Informasi" else powerSupply
            textViewWater.text = if (water == null || water == "0" || water.startsWith("Pilih")) "Tidak Ada Informasi" else water
            textViewInterior.text = if (interior == null || interior == "0" || interior.startsWith("Pilih")) "Tidak Ada Informasi" else interior
            textViewRoad.text = if (roadAccess == null || roadAccess == "0" || roadAccess.startsWith("Pilih")) "Tidak Ada Informasi" else roadAccess

            textViewPriceUnit.text = if (price == null || price == "0" || price.startsWith("Pilih")) "Tidak Ada Informasi" else NumericalUnitConverter.unitFormatter(price, true)
        }
    }
}