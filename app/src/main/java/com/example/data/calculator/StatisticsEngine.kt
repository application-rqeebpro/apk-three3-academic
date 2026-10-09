package com.example.data.calculator

import kotlin.math.sqrt

data class StatisticsReport(
    val count: Int,
    val sum: Double,
    val mean: Double,
    val median: Double,
    val variance: Double,
    val standardDeviation: Double
)

object StatisticsEngine {
    fun calculate(values: List<Double>): StatisticsReport? {
        if (values.isEmpty()) return null
        val n = values.size
        val sum = values.sum()
        val mean = sum / n
        val sorted = values.sorted()
        val median = if (n % 2 == 1) sorted[n / 2] else (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        val variance = values.map { (it - mean) * (it - mean) }.sum() / n
        val stdDev = sqrt(variance)

        return StatisticsReport(n, sum, mean, median, variance, stdDev)
    }
}
