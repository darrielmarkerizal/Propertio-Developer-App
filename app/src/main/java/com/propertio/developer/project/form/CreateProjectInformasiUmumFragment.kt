package com.propertio.developer.project.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.propertio.developer.R
import com.propertio.developer.databinding.FragmentCreateProjectInformasiUmumBinding
import com.propertio.developer.project_management.ButtonNavigationProjectManagementClickListener


class CreateProjectInformasiUmumFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInformasiUmumBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         * TODO: Berikut contoh mendapatkan binding dari ProjectFormActivity
         *  1. Buat variabel activity "activity = activity as? ProjectFormActivity"
         *  2. Buat variabel activityBinding "activityBinding = activity?.binding"
         *  3. Gunakan activityBinding untuk mengakses binding dari ProjectFormActivity, contoh
         *     activityBinding?.floatingButtonBack?.setOnClickListener { ... } <- ini button Back
         *     activityBinding?.floatingButtonNext?.setOnClickListener { ... } <- ini button Next
         *  4. jangan lupa tambahkan "activity.onBackButtonProjectManagementClick()"
         *     pada floatingButtonBack.setOnClickListener pada bagian paling akhir
         *  5. jangan lupa tambahkan "activity.onNextButtonProjectManagementClick()"
         *     pada floatingButtonNext.setOnClickListener pada bagian paling akhir
         *
         *  NB: penambahan ...ProjectManagementClick bertujuan agar halaman dapat berpindah
         *      ke halaman selanjutnya atau sebelumnya. Disarankan untuk ditaruh paling akhir.
         *      boleh dibuat ke dalam if-else
         */
        val activity = activity as? ProjectFormActivity
        val activityBinding = activity?.binding

        activityBinding?.floatingButtonBack?.setOnClickListener {
            Toast.makeText(activity, "Anda Menekan Di Fragment, Bukan Di Activity", Toast.LENGTH_SHORT).show()

            activity.onBackButtonProjectManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            Toast.makeText(activity, "Anda Menekan Di Fragment, Bukan Di Activity", Toast.LENGTH_SHORT).show()

            activity.onNextButtonProjectManagementClick()
        }

    }


}