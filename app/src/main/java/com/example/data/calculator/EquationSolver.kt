package com.example.data.calculator

import kotlin.math.*

data class EquationResult(
    val roots: List<String>,
    val explanation: String
)

object EquationSolver {

    // Quadratic: a x^2 + b x + c = 0
    fun solveQuadratic(a: Double, b: Double, c: Double): EquationResult {
        if (abs(a) < 1e-9) {
            if (abs(b) < 1e-9) return EquationResult(emptyList(), "ليس لها حل")
            val root = -c / b
            return EquationResult(listOf("س = $root"), "معادلة من الدرجة الأولى")
        }

        val delta = b * b - 4 * a * c
        val deltaStr = String.format("%.2f", delta)

        return if (delta > 0) {
            val r1 = (-b + sqrt(delta)) / (2 * a)
            val r2 = (-b - sqrt(delta)) / (2 * a)
            EquationResult(
                listOf("س₁ = $r1", "س₂ = $r2"),
                "المميز Δ = $deltaStr > 0، يوجد جذران حقيقيان مختلفان."
            )
        } else if (abs(delta) < 1e-9) {
            val r = -b / (2 * a)
            EquationResult(
                listOf("س = $r"),
                "المميز Δ = 0، جذر حقيقي مضاعف."
            )
        } else {
            val real = -b / (2 * a)
            val imag = sqrt(-delta) / (2 * a)
            val r1 = "$real + ${abs(imag)}ت"
            val r2 = "$real - ${abs(imag)}ت"
            EquationResult(
                listOf("س₁ = $r1", "س₂ = $r2"),
                "المميز Δ = $deltaStr < 0، يوجد جذران مركبان مترافقان."
            )
        }
    }
}
