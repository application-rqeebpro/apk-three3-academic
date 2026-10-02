package com.example.data.calculator

enum class CalculatorTab(val title: String, val icon: String) {
    SCIENTIFIC("علمية وكسور", "🧮"),
    COMPLEX("أعداد مركبة", "ℂ"),
    EQUATIONS("معادلات", "⚖️"),
    MATRICES("مصفوفات", "▦"),
    STATISTICS("إحصاء", "📊"),
    CONVERTER("تحويل وحدات", "🔄")
}

data class CalculatorHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val explanation: CalculationExplanation? = null,
    val timestamp: Long = System.currentTimeMillis()
)
