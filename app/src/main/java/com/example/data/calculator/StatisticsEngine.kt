package com.example.data.calculator

import kotlin.math.sqrt

data class StatisticsReport(
    val count: Int,
    val sum: Double,
    val mean: Double,
    val median: Double,
    val modes: List<Double> = emptyList(),
    val range: Double = 0.0,
    val variance: Double = 0.0,
    val stdDev: Double = 0.0,
    val steps: List<String> = emptyList()
)

object StatisticsEngine {
    fun calculate(values: List<Double>): StatisticsReport? {
        if (values.isEmpty()) return null
        val n = values.size
        val sum = values.sum()
        val mean = sum / n
        val sorted = values.sorted()
        val median = if (n % 2 == 1) sorted[n / 2] else (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        val range = sorted.last() - sorted.first()

        // Mode
        val freqMap = values.groupingBy { it }.eachCount()
        val maxFreq = freqMap.values.maxOrNull() ?: 1
        val modes = if (maxFreq > 1) freqMap.filter { it.value == maxFreq }.keys.toList().sorted() else emptyList()

        val variance = values.map { (it - mean) * (it - mean) }.sum() / n
        val stdDev = sqrt(variance)

        val steps = listOf(
            "1. حساب المجموع: مجـ س = $sum",
            "2. حساب المتوسط: س̄ = $sum / $n = ${String.format(java.util.Locale.US, "%.3f", mean)}",
            "3. ترتيب القيم تصاعدياً لحساب الوسيط والمدى: ${sorted.take(6).joinToString(", ")}${if (sorted.size > 6) "..." else ""}",
            "4. حساب التباين: ع² = مجـ(س - س̄)² / ن = ${String.format(java.util.Locale.US, "%.3f", variance)}",
            "5. الانحراف المعياري: ع = √ع² = ${String.format(java.util.Locale.US, "%.3f", stdDev)}"
        )

        return StatisticsReport(n, sum, mean, median, modes, range, variance, stdDev, steps)
    }

    fun factorial(n: Long): Long {
        if (n < 0) return 0L
        if (n <= 1) return 1L
        var res = 1L
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    fun nPr(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0L
        var res = 1L
        for (i in 0 until r) {
            res *= (n - i)
        }
        return res
    }

    fun nCr(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0L
        val k = if (r > n - r) n - r else r
        var num = 1L
        var den = 1L
        for (i in 1..k) {
            num *= (n - i + 1)
            den *= i
        }
        return num / den
    }
}
