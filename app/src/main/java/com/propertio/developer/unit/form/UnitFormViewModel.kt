package com.propertio.developer.unit.form

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UnitFormViewModel : ViewModel() {

    val namaUnit = MutableLiveData<String>()
    val deskripsiUnit = MutableLiveData<String>()
    val stokUnit = MutableLiveData<String>()
    val hargaUnit = MutableLiveData<String>()

    fun updateNamaUnit(nama: String) {
        namaUnit.value = nama
        Log.d("UnitFormViewModel", "Nama unit: $nama")
    }

    fun updateDeskripsiUnit(deskripsi: String) {
        deskripsiUnit.value = deskripsi
        Log.d("UnitFormViewModel", "Deskripsi unit: $deskripsi")
    }

    fun updateStokUnit(stok: String) {
        stokUnit.value = stok
        Log.d("UnitFormViewModel", "Stok unit: $stok")
    }

    fun updateHargaUnit(harga: String) {
        hargaUnit.value = harga
        Log.d("UnitFormViewModel", "Harga unit: $harga")
    }
}