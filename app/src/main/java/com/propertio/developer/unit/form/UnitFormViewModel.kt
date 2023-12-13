package com.propertio.developer.unit.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse

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

    private val _luasTanah = MutableLiveData<String>()
    val luasTanah: LiveData<String> get() = _luasTanah

    private val _luasBangunan = MutableLiveData<String>()
    val luasBangunan: LiveData<String> get() = _luasBangunan

    private val _jumlahLantai = MutableLiveData<String>()
    val jumlahLantai: LiveData<String> get() = _jumlahLantai

    private val _jumlahKamarTidur = MutableLiveData<String>()
    val jumlahKamarTidur: LiveData<String> get() = _jumlahKamarTidur

    private val _jumlahKamarMandi = MutableLiveData<String>()
    val jumlahKamarMandi: LiveData<String> get() = _jumlahKamarMandi

    private val _jumlahParkir = MutableLiveData<String>()
    val jumlahParkir: LiveData<String> get() = _jumlahParkir

    private val _electricityType = MutableLiveData<String>()
    val electricityType: LiveData<String> get() = _electricityType

    private val _waterType = MutableLiveData<String>()
    val waterType: LiveData<String> get() = _waterType

    private val _interiorType = MutableLiveData<String>()
    val interiorType: LiveData<String> get() = _interiorType

    private val _roadAccessType = MutableLiveData<String>()
    val roadAccessType: LiveData<String> get() = _roadAccessType

    fun updateProjectId(projectId: Int) {
        _projectId.value = projectId
        Log.d("UnitFormViewModel", "updateProjectId: $projectId")
    }

    fun updateNamaUnit(nama: String) {
        _namaUnit.value = nama
        Log.d("UnitFormViewModel", "updateNamaUnit: $nama")
    }

    fun updateDeskripsiUnit(deskripsi: String) {
        _deskripsiUnit.value = deskripsi
        Log.d("UnitFormViewModel", "updateDeskripsiUnit: $deskripsi")
    }

    fun updateStokUnit(stok: String) {
        _stokUnit.value = stok
        Log.d("UnitFormViewModel", "updateStokUnit: $stok")
    }

    fun updateHargaUnit(harga: String) {
        _hargaUnit.value = harga
        Log.d("UnitFormViewModel", "updateHargaUnit: $harga")
    }

    fun updateLuasTanah(luasTanah: String) {
        _luasTanah.value = luasTanah
        Log.d("UnitFormViewModel", "updateLuasTanah: $luasTanah")
    }

    fun updateLuasBangunan(luasBangunan: String) {
        _luasBangunan.value = luasBangunan
        Log.d("UnitFormViewModel", "updateLuasBangunan: $luasBangunan")
    }

    fun updateJumlahLantai(jumlahLantai: String) {
        _jumlahLantai.value = jumlahLantai
        Log.d("UnitFormViewModel", "updateJumlahLantai: $jumlahLantai")
    }

    fun updateJumlahKamar(jumlahKamarTidur: String) {
        _jumlahKamarTidur.value = jumlahKamarTidur
        Log.d("UnitFormViewModel", "updateJumlahKamar: $jumlahKamarTidur")
    }

    fun updateJumlahKamarMandi(jumlahKamarMandi: String) {
        _jumlahKamarMandi.value = jumlahKamarMandi
        Log.d("UnitFormViewModel", "updateJumlahKamarMandi: $jumlahKamarMandi")
    }

    fun updateParkingType(jumlahParkir: String) {
        _jumlahParkir.value = jumlahParkir
        Log.d("UnitFormViewModel", "updateParkingType: $jumlahParkir")
    }

    fun updateElectricityType(electricityType: String) {
        _electricityType.value = electricityType
        Log.d("UnitFormViewModel", "updateElectricityType: $electricityType")
    }

    fun updateWaterType(waterType: String) {
        _waterType.value = waterType
        Log.d("UnitFormViewModel", "updateWaterType: $waterType")
    }

    fun updateInteriorType(interiorType: String) {
        _interiorType.value = interiorType
        Log.d("UnitFormViewModel", "updateInteriorType: $interiorType")
    }

    fun updateRoadAccessType(roadAccessType: String) {
        _roadAccessType.value = roadAccessType
        Log.d("UnitFormViewModel", "updateRoadAccessType: $roadAccessType")
    }

    fun updateUnitDetail(data: UnitDetailResponse.Unit) {
        updateNamaUnit(data.title ?: "")
        updateDeskripsiUnit(data.description ?: "")
        updateStokUnit(data.stock ?: "")
        updateHargaUnit(data.price ?: "")
        updateLuasTanah(data.surfaceArea ?: "")
        updateLuasBangunan(data.buildingArea ?: "")
        updateJumlahLantai(data.floor ?: "")
        updateJumlahKamar(data.bedroom ?: "")
        updateJumlahKamarMandi(data.bathroom ?: "")
        updateParkingType(data.garage ?: "")
        updateElectricityType(data.powerSupply ?: "")
        updateWaterType(data.waterType ?: "")
        updateInteriorType(data.interior ?: "")
        updateRoadAccessType(data.roadAccess ?: "")
    }
}