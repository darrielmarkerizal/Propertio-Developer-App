package com.propertio.developer.unit.form

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.database.MasterData

class UnitFormViewModel : ViewModel() {

    var namaUnit: String? = null
    var propertyType: String? = null
    var deskripsiUnit: String? = null
    var isUploaded = false
    var isAlreadyUploaded : MutableLiveData<Boolean> = MutableLiveData(false)
    var stokUnit: String? = null
    var hargaUnit: String? = null
    var projectId: Int? = null
    var unitId: Int? = null
    var luasTanah: String? = null
    var luasBangunan: String? = null
    var jumlahKamarTidur: String? = null
    var jumlahKamarMandi: String? = null
    var jumlahLantai: String? = null

    var interiorType: MasterData? = null
    var roadAccessType: MasterData? = null
    var jumlahParkir: MasterData? = null
    var electricityType: MasterData? = null
    var waterType: MasterData? = null

    fun printLog(msg : String = "") {
        Log.d( "ViewModel",
            "loadTextData $msg:" +
                    "\n namaUnit     : $namaUnit " +
                    "\n propertyType : $propertyType " +
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
        isAlreadyUploaded: Boolean = true,
        namaUnit: String?,
        deskripsiUnit: String?,
        propertyType: String?,
        stokUnit: String?,
        hargaUnit: String?,
        projectId: Int?,
        unitId: Int?,
        luasTanah: String?,
        luasBangunan: String?,
        jumlahKamar: String?,
        jumlahKamarMandi: String?,
        jumlahLantai: String?,

        interiorType: MasterData?,
        roadAccessType: MasterData?,
        parkingType: MasterData?,
        electricityType: MasterData?,
        waterType: MasterData?
    ) {
        this.isAlreadyUploaded.postValue(isAlreadyUploaded)
        this.namaUnit = namaUnit
        this.deskripsiUnit = deskripsiUnit
        this.stokUnit = stokUnit
        this.propertyType = propertyType
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