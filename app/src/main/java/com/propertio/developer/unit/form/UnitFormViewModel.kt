package com.propertio.developer.unit.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UnitFormViewModel : ViewModel() {

    private val _projectId = MutableLiveData<Int>()
    val projectId: LiveData<Int> get() = _projectId

    private val _namaUnit = MutableLiveData<String>()
    val namaUnit: LiveData<String> get() = _namaUnit

    private val _deskripsiUnit = MutableLiveData<String>()
    val deskripsiUnit: LiveData<String> get() = _deskripsiUnit

    private val _stokUnit = MutableLiveData<String>()
    val stokUnit: LiveData<String> get() = _stokUnit

    private val _hargaUnit = MutableLiveData<String>()
    val hargaUnit: LiveData<String> get() = _hargaUnit

    fun updateProjectId(projectId: Int) {
        _projectId.value = projectId
    }

    fun updateNamaUnit(nama: String) {
        _namaUnit.value = nama
    }

    fun updateDeskripsiUnit(deskripsi: String) {
        _deskripsiUnit.value = deskripsi
    }

    fun updateStokUnit(stok: String) {
        _stokUnit.value = stok
    }

    fun updateHargaUnit(harga: String) {
        _hargaUnit.value = harga
    }
}