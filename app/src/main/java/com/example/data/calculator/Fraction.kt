package com.example.data.calculator

import kotlin.math.abs

data class Fraction(val num: Long, val den: Long) {
    init {
        require(den != 0L) { "المقام لا يمكن أن يكون صفراً" }
    }

    val isInteger: Boolean get() = num % den == 0L

    fun simplify(): Fraction {
        val g = gcd(abs(num), abs(den))
        val sign = if (den < 0) -1 else 1
        return Fraction((num / g) * sign, (abs(den) / g))
    }

    override fun toString(): String {
        val s = simplify()
        return if (s.den == 1L) "${s.num}" else "${s.num}/${s.den}"
    }

    companion object {
        fun fromDouble(value: Double, tolerance: Double = 1e-6): Fraction? {
            if (value.isNaN() || value.isInfinite()) return null
            var h1 = 1L; var h2 = 0L
            var k1 = 0L; var k2 = 1L
            var b = value
            do {
                val a = b.toLong()
                var aux = h1
                h1 = a * h1 + h2
                h2 = aux
                aux = k1
                k1 = a * k1 + k2
                k2 = aux
                b = 1.0 / (b - a)
            } while (abs(value - h1.toDouble() / k1.toDouble()) > value * tolerance && k1 < 100000L)
            return Fraction(h1, k1).simplify()
        }

        private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    }
}
