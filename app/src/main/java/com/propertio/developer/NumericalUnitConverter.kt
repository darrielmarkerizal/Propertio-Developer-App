package com.propertio.developer

import android.util.Log

object NumericalUnitConverter {

    private val listOfNumericalUnit = listOf(
        "Ribu",
        "Juta",
        "Miliar",
        "Triliun"
    )

    private val priceCodeList = mapOf(
        "idr" to "Rp.",
        "usd" to "$",
        "eur" to "€",
        "gbp" to "£",
    )


    fun meterSquareFormatter(meterSquareValue: String) : String {
        return "$meterSquareValue m\u00B2"
    }


    fun unitFormatter(unitValue: Int, isAPrice : Boolean = false) : String {
        return  unitFormatter(unitValue.toString(), isAPrice)
    }

    fun unitFormatter(unitValue: String, isAPrice: Boolean = false) : String {
        if (isAPrice) {
            val CODE_PRICE = "idr"
            return if (unitValue.length > 12) {
                prefixPriceCode(CODE_PRICE, unitValue.substring(0, unitValue.length - 12) + " " + listOfNumericalUnit[3])
            } else if (unitValue.length > 9) {
                prefixPriceCode(CODE_PRICE, unitValue.substring(0, unitValue.length - 9) + " " + listOfNumericalUnit[2])
            } else if (unitValue.length > 6) {
                prefixPriceCode(CODE_PRICE, unitValue.substring(0, unitValue.length - 6) + " " + listOfNumericalUnit[1])
            } else if (unitValue.length > 3) {
                prefixPriceCode(CODE_PRICE, unitValue.substring(0, unitValue.length - 3) + " " + listOfNumericalUnit[0])
            } else {
                prefixPriceCode(CODE_PRICE, unitValue)
            }
        }

        return if (unitValue.length > 12) {
            unitValue.substring(0, unitValue.length - 12) + " " + listOfNumericalUnit[3]
        } else if (unitValue.length > 9) {
            unitValue.substring(0, unitValue.length - 9) + " " + listOfNumericalUnit[2]
        } else if (unitValue.length > 6) {
            unitValue.substring(0, unitValue.length - 6) + " " + listOfNumericalUnit[1]
        } else if (unitValue.length > 3) {
            unitValue.substring(0, unitValue.length - 3) + " " + listOfNumericalUnit[0]
        } else {
            unitValue
        }

    }

    private fun prefixPriceCode(priceCode: String, price : String) : String {
        return (priceCodeList[priceCode] ?: "Price/") + price
    }


}