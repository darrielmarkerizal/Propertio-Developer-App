package com.propertio.developer.pesan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityDetailChatBinding

class DetailChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Toolbar
        val bindingToolbar = binding.toolbarContainer
        with(bindingToolbar) {
            textViewTitle.text = "Pesan"
            buttonBack.visibility = android.view.View.VISIBLE
            buttonBack.setOnClickListener {
                finish()
            }
            buttonDelete.visibility = android.view.View.INVISIBLE
            buttonDelete.setOnClickListener {
                // TODO: Create Delete Message
                Toast.makeText(this@DetailChatActivity, "Delete", Toast.LENGTH_SHORT).show()
            }
        }

        // Body
        with(binding) {
            textViewName.text = intent.getStringExtra(EXTRA_NAME)
            textViewEmail.text = intent.getStringExtra(EXTRA_EMAIL)
            textViewPhone.text = intent.getStringExtra(EXTRA_PHONE)
            textViewSubject.text = intent.getStringExtra(EXTRA_SUBJECT)
            textViewMessage.text = intent.getStringExtra(EXTRA_MESSAGE)

            val _date = intent.getStringExtra(EXTRA_TIME)?.split("T")
            val _time = _date?.get(1)?.split(".")?.get(0)?.split(":") // hh:mm:ss

            val dateString = _date?.get(0)?.split("-") // yyyy-mm-dd
            val time = "${_time?.get(0)}:${_time?.get(1)}" // hh:mm

            val months = resources.getStringArray(R.array.list_of_months)

            val datetime = "${(dateString?.get(2)?.toInt() ?: 1)} ${months[(dateString?.get(1)?.toInt() ?: 1 ) - 1]} ${dateString?.get(0)}, ${resources.getString(R.string.hours)} $time"

            textViewTime.text = datetime

            buttonWhatsapp.setOnClickListener {
                val intentToWhatsapp = Intent(Intent.ACTION_VIEW)
                intentToWhatsapp.data = Uri.parse("https://wa.me/${textViewPhone.text}")
                startActivity(intentToWhatsapp)
            }

            setPreviewHTMLMessage(textViewMessage.text.toString())
        }


    }

    private fun setPreviewHTMLMessage(message: String) {
        binding.webViewPesanDetail.loadData(message, "text/html", "UTF-8")

    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_SUBJECT = "extra_subject"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_TIME = "extra_time"
    }
}