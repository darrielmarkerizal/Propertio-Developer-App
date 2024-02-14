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

    val parking = listOf(
        MasterData("1 Motor", "1 Motor"),
        MasterData("1 Mobil", "1 Mobil"),
        MasterData("2 Mobil", "2 Mobil"),
        MasterData("3 Mobil", "3 Mobil"),
        MasterData("4 Mobil", "4 Mobil"),
        MasterData("5 Mobil", "5 Mobil"),
        MasterData("6 Mobil", "6 Mobil"),
        MasterData("7 Mobil", "7 Mobil"),
        MasterData("8 Mobil", "8 Mobil"),
        MasterData("9 Mobil", "9 Mobil"),
        MasterData("10 Mobil", "10 Mobil"),
        MasterData("Lebih dari 10 Mobil", "Lebih dari 10 Mobil"),
    )

    val electricity = listOf(
        MasterData("Tidak ada", "Tidak ada"),
        MasterData("Lainnya", "Lainnya"),
        MasterData("450 Watt", "450 Watt"),
        MasterData("900 Watt", "900 Watt"),
        MasterData("1300 Watt", "1300 Watt"),
        MasterData("2200 Watt", "2200 Watt"),
        MasterData("3500 Watt", "3500 Watt"),
        MasterData("4400 Watt", "4400 Watt"),
        MasterData("5500 Watt", "5500 Watt"),
        MasterData("6600 Watt", "6600 Watt"),
        MasterData("7600 Watt", "7600 Watt"),
        MasterData("7700 Watt", "7700 Watt"),
        MasterData("8000 Watt", "8000 Watt"),
        MasterData("9500 Watt", "9500 Watt"),
        MasterData("10000 Watt", "10000 Watt"),
        MasterData("10600 Watt", "10600 Watt"),
        MasterData("11000 Watt", "11000 Watt"),
        MasterData("12700 Watt", "12700 Watt"),
        MasterData("13200 Watt", "13200 Watt"),
        MasterData("13300 Watt", "13300 Watt"),
        MasterData("13900 Watt", "13900 Watt"),
        MasterData("16500 Watt", "16500 Watt"),
        MasterData("17600 Watt", "17600 Watt"),
        MasterData("19000 Watt", "19000 Watt"),
        MasterData("22000 Watt", "22000 Watt"),
        MasterData("23000 Watt", "23000 Watt"),
        MasterData("24000 Watt", "24000 Watt"),
        MasterData("30500 Watt", "30500 Watt"),
        MasterData("33000 Watt", "33000 Watt"),
        MasterData("38100 Watt", "38100 Watt"),
        MasterData("41500 Watt", "41500 Watt"),
        MasterData("47500 Watt", "47500 Watt"),
        MasterData("53000 Watt", "53000 Watt"),
        MasterData("61000 Watt", "61000 Watt"),
        MasterData("66000 Watt", "66000 Watt"),
        MasterData("76000 Watt", "76000 Watt"),
        MasterData("82500 Watt", "82500 Watt"),
        MasterData("85000 Watt", "85000 Watt"),
        MasterData("95000 Watt", "95000 Watt")
    )

    val water = listOf(
        MasterData("PAM", "PAM"),
        MasterData("Sumur", "Sumur"),
        MasterData("PAM dan Sumur", "PAM & Sumur"),
        MasterData("Tidak ada", "Tidak ada")
    )

    val interior = listOf(
        MasterData("Full", "Full"),
        MasterData("Sebagian", "Sebagian"),
        MasterData("Kosong", "Kosong")
    )

    val roadAccess = listOf(
        MasterData("Gang Kecil", "Gang Kecil"),
        MasterData("Persimpangan dua motor", "Persimpangan dua motor"),
        MasterData("Mobil Kecil dapat masuk", "Mobil Kecil dapat masuk"),
        MasterData("Persimpangan dua mobil", "Persimpangan dua mobil"),
        MasterData("Jalan Desa", "Jalan Desa"),
        MasterData("Jalan Kota", "Jalan Kota"),
        MasterData("Jalan Provinsi", "Jalan Provinsi"),
        MasterData("Jalan Nasional", "Jalan Nasional"),
        MasterData("Jalan Industri", "Jalan Industri")
    )


}


