package com.propertio.developer.database


data class MasterData(val toUser : String, val toDb : String)
object MasterDataDeveloperPropertio {

    val certificate = listOf(
        MasterData("Hak Milik", "SHM"),
        MasterData("Hak Guna Bangunan", "HGB"),
        MasterData("Girik / Petok D", "Girik"),
        MasterData("Letter C", "Letter C"),
        MasterData("Lainnya", "Lainnya"),
        MasterData("Strata", "Strata")
    )


}


