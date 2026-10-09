package com.example.data.calculator

enum class CalculatorTab(val title: String) {
    SCIENTIFIC("علمية"),
    COMPLEX("مركبة"),
    EQUATIONS("معادلات"),
    MATRICES("مصفوفات"),
    STATISTICS("إحصاء"),
    CONVERTER("تحويل وحدات")
}

enum class AngleMode {
    DEGREE,
    RADIAN
}

data class CalculationResult(
    val primaryDisplay: String = "0",
    val expression: String = "",
    val naturalMathText: String = "",
    val fractionForm: String? = null,
    val decimalForm: String = "0",
    val scientificForm: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null
) {
    val formattedResult: String get() = primaryDisplay
}

data class CalculatorHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
