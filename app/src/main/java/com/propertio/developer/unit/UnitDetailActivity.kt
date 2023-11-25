package com.propertio.developer.unit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.propertio.developer.R
import com.propertio.developer.carousel.CarouselAdapter
import com.propertio.developer.carousel.ImageData
import com.propertio.developer.databinding.ActivityUnitDetailBinding

class UnitDetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityUnitDetailBinding.inflate(layoutInflater)
    }

    private lateinit var carouselAdapter: CarouselAdapter
    private val carouselList = ArrayList<ImageData>()
    private lateinit var dots : ArrayList<TextView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarContainer.textViewTitle.text = "Detail Unit"

        binding.floatingButtonBack.setOnClickListener {
            Log.d("UnitDetailActivity", "onCreate: back")
            finish()
        }

        //TODO: get data from intent

    }
}