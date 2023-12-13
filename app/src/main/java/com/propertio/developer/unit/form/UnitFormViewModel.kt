package com.propertio.developer.unit.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse

class UnitFormViewModel : ViewModel() {

    var namaUnit: String? = null
    var deskripsiUnit: String? = null
    var stokUnit: String? = null
    var hargaUnit: String? = null
    var projectId: Int? = null
    var unitId: Int? = null
    var luasTanah: String? = null
    var luasBangunan: String? = null
    var jumlahKamarTidur: String? = null
    var jumlahKamarMandi: String? = null
    var jumlahLantai: String? = null
    var interiorType: String? = null
    var roadAccessType: String? = null
    var jumlahParkir: String? = null
    var electricityType: String? = null
    var waterType: String? = null

    fun printLog(msg : String = "") {
        Log.d( "ViewModel",
            "loadTextData $msg:" +
                    "\n namaUnit     : $namaUnit " +
                    "\n deskripsiUnit: $deskripsiUnit " +
                    "\n stokUnit     : $stokUnit " +
                    "\n hargaUnit    : $hargaUnit " +
                    "\n luasTanah    : $luasTanah " +
                    "\n luasBangunan : $luasBangunan " +
                    "\n jumlahKamarTidur: $jumlahKamarTidur " +
                    "\n jumlahKamarMandi: $jumlahKamarMandi " +
                    "\n jumlahLantai: $jumlahLantai " +
                    "\n interiorType: $interiorType " +
                    "\n roadAccessType: $roadAccessType " +
                    "\n jumlahParkir: $jumlahParkir " +
                    "\n electricityType: $electricityType " +
                    "\n waterType: $waterType " +
                    "\n projectId: $projectId " +
                    "\n unitId: $unitId "
        )

    }

    fun add(
        namaUnit: String?,
        deskripsiUnit: String?,
        stokUnit: String?,
        hargaUnit: String?,
        projectId: Int?,
        unitId: Int?,
        luasTanah: String?,
        luasBangunan: String?,
        jumlahKamar: String?,
        jumlahKamarMandi: String?,
        jumlahLantai: String?,
        interiorType: String?,
        roadAccessType: String?,
        parkingType: String?,
        electricityType: String?,
        waterType: String?
    ) {
        this.namaUnit = namaUnit
        this.deskripsiUnit = deskripsiUnit
        this.stokUnit = stokUnit
        this.hargaUnit = hargaUnit
        this.projectId = projectId
        this.unitId = unitId
        this.luasTanah = luasTanah
        this.luasBangunan = luasBangunan
        this.jumlahKamarTidur = jumlahKamar
        this.jumlahKamarMandi = jumlahKamarMandi
        this.jumlahLantai = jumlahLantai
        this.interiorType = interiorType
        this.roadAccessType = roadAccessType
        this.jumlahParkir = parkingType
        this.electricityType = electricityType
        this.waterType = waterType
    }
}