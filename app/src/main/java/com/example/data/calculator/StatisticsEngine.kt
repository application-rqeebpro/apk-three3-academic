package com.example.data.calculator

import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.math.sqrt

data class StatisticsReport(
    val count: Int,
    val sum: Double,
    val mean: Double,
    val median: Double,
    val modes: List<Double>,
    val range: Double,
    val variance: Double,
    val stdDev: Double,
    val min: Double,
    val max: Double,
    val steps: List<String>
)

object StatisticsEngine {

    fun calculate(values: List<Double>): StatisticsReport {
        require(values.isNotEmpty()) { "يرجى إدخال قيمة واحدة على الأقل" }

        val n = values.size
        val sum = values.sum()
        val mean = sum / n
        val sorted = values.sorted()

        val median = if (n % 2 == 1) {
            sorted[n / 2]
        } else {
            (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        }

        // Mode calculation
        val frequencies = values.groupingBy { it }.eachCount()
        val maxFreq = frequencies.values.maxOrNull() ?: 1
        val modes = if (maxFreq > 1) {
            frequencies.filter { it.value == maxFreq }.keys.toList().sorted()
        } else {
            emptyList()
        }

        val min = sorted.first()
        val max = sorted.last()
        val range = max - min

        // Variance: sum((x - mean)^2) / (n) or sample (n-1) -> typically population variance in 3rd secondary Yemeni books
        val sumSquaredDiffs = values.sumOf { (it - mean) * (it - mean) }
        val variance = if (n > 1) sumSquaredDiffs / n else 0.0
        val stdDev = sqrt(variance)

        val steps = listOf(
            "1. عدد القيم (ن) = $n",
            "2. مجموع القيم (مجـ س) = ${fmt(sum)}",
            "3. المتوسط الحسابي (س̄) = مجـ س ÷ ن = ${fmt(sum)} ÷ $n = ${fmt(mean)}",
            "4. ترتيب القيم تصاعدياً: ${sorted.joinToString(", ") { fmt(it) }}",
            "5. الوسيط = ${fmt(median)}",
            "6. المنوال = ${if (modes.isEmpty()) "لا يوجد منوال (كل القيم تكررت بالتساوي)" else modes.joinToString(", ") { fmt(it) }}",
            "7. المدى = القيمة العظمى - الصغرى = ${fmt(max)} - ${fmt(min)} = ${fmt(range)}",
            "8. مجموع مربعات الانحرافات مجـ(س - س̄)² = ${fmt(sumSquaredDiffs)}",
            "9. التباين (ع²) = مجـ(س - س̄)² ÷ ن = ${fmt(sumSquaredDiffs)} ÷ $n = ${fmt(variance)}",
            "10. الانحراف المعياري (ع) = √التباين = √${fmt(variance)} = ${fmt(stdDev)}"
        )

        return StatisticsReport(
            count = n,
            sum = sum,
            mean = mean,
            median = median,
            modes = modes,
            range = range,
            variance = variance,
            stdDev = stdDev,
            min = min,
            max = max,
            steps = steps
        )
    }

    /**
     * Factorial n!
     */
    fun factorial(n: Long): Long {
        require(n >= 0) { "المضروب n! معرف للأعداد غير السالبة فقط" }
        require(n <= 20) { "القيمة كبيرة جداً وتتجاوز سعة الحساب (الحد الأقصى n = 20)" }
        var res = 1L
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    /**
     * Permutations nPr = n! / (n - r)! (التباديل)
     */
    fun nPr(n: Long, r: Long): Long {
        require(n >= 0 && r >= 0) { "القيم يجب أن تكون غير سالبة" }
        require(r <= n) { "يجب أن تكون r ≤ n في التباديل nPr" }
        require(n <= 20) { "الحد الأقصى لـ n هو 20" }
        var res = 1L
        for (i in 0 until r) {
            res *= (n - i)
        }
        return res
    }

    /**
     * Combinations nCr = n! / (r! (n - r)!) (التوافيق)
     */
    fun nCr(n: Long, r: Long): Long {
        require(n >= 0 && r >= 0) { "القيم يجب أن تكون غير سالبة" }
        require(r <= n) { "يجب أن تكون r ≤ n في التوافيق nCr" }
        val k = if (r > n - r) n - r else r
        var res = 1L
        for (i in 1..k) {
            res = res * (n - i + 1) / i
        }
        return res
    }

    private fun fmt(d: Double): String {
        if (abs(d - d.roundToLong()) < 1e-6) {
            return d.roundToLong().toString()
        }
        return String.format(java.util.Locale.US, "%.3f", d).trimEnd('0').trimEnd('.')
    }
}
