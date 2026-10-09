package com.example.data.calculator

enum class UnitCategory(val title: String) {
    LENGTH("الطول"),
    MASS("الكتلة"),
    TEMPERATURE("درجة الحرارة"),
    ENERGY("الطاقة والقدرة")
}

data class ConversionUnit(val name: String, val factorToBase: Double)

object UnitConverter {
    val lengthUnits = listOf(
        ConversionUnit("متر (m)", 1.0),
        ConversionUnit("كيلومتر (km)", 1000.0),
        ConversionUnit("سنتيمتر (cm)", 0.01),
        ConversionUnit("مليمتر (mm)", 0.001)
    )

    fun convertLength(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
        val inMeters = value * from.factorToBase
        return inMeters / to.factorToBase
    }
}
