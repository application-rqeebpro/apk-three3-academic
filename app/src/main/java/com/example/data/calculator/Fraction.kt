package com.example.data.calculator

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Representation of an exact rational number (Fraction).
 * Handles addition, subtraction, multiplication, division,
 * automatic simplification, and decimal conversion.
 */
data class Fraction(val numerator: Long, val denominator: Long = 1L) : Comparable<Fraction> {

    val num: Long
    val den: Long

    init {
        require(denominator != 0L) { "لا يمكن أن يكون المقام صفراً" }
        val gcdVal = gcd(abs(numerator), abs(denominator))
        val sign = if (denominator < 0) -1L else 1L
        num = (numerator / gcdVal) * sign
        den = abs(denominator) / gcdVal
    }

    val isInteger: Boolean get() = den == 1L
    val isZero: Boolean get() = num == 0L

    operator fun plus(other: Fraction): Fraction {
        val commonDen = lcm(this.den, other.den)
        val n1 = this.num * (commonDen / this.den)
        val n2 = other.num * (commonDen / other.den)
        return Fraction(n1 + n2, commonDen)
    }

    operator fun minus(other: Fraction): Fraction {
        val commonDen = lcm(this.den, other.den)
        val n1 = this.num * (commonDen / this.den)
        val n2 = other.num * (commonDen / other.den)
        return Fraction(n1 - n2, commonDen)
    }

    operator fun times(other: Fraction): Fraction {
        return Fraction(this.num * other.num, this.den * other.den)
    }

    operator fun div(other: Fraction): Fraction {
        require(other.num != 0L) { "لا يمكن القسمة على كسر قيمته صفر" }
        return Fraction(this.num * other.den, this.den * other.num)
    }

    operator fun unaryMinus(): Fraction = Fraction(-num, den)

    fun pow(exponent: Int): Fraction {
        return when {
            exponent == 0 -> Fraction(1, 1)
            exponent > 0 -> Fraction(power(num, exponent), power(den, exponent))
            else -> {
                val absExp = abs(exponent)
                Fraction(power(den, absExp), power(num, absExp))
            }
        }
    }

    fun toDouble(): Double = num.toDouble() / den.toDouble()

    /**
     * Mixed fraction representation: e.g. 5/4 -> (whole = 1, remainderNum = 1, den = 4)
     */
    fun toMixedString(): String {
        if (den == 1L) return "$num"
        val whole = num / den
        val remainder = abs(num % den)
        return if (whole != 0L) {
            "$whole [$remainder/$den]"
        } else {
            "$num/$den"
        }
    }

    override fun toString(): String {
        return if (den == 1L) "$num" else "$num/$den"
    }

    override fun compareTo(other: Fraction): Int {
        val diff = this.num * other.den - other.num * this.den
        return diff.compareTo(0L)
    }

    companion object {
        val ZERO = Fraction(0, 1)
        val ONE = Fraction(1, 1)

        fun gcd(a: Long, b: Long): Long {
            var x = a
            var y = b
            while (y != 0L) {
                val t = y
                y = x % y
                x = t
            }
            return if (x == 0L) 1L else abs(x)
        }

        fun lcm(a: Long, b: Long): Long {
            if (a == 0L || b == 0L) return 0L
            return abs(a / gcd(a, b) * b)
        }

        /**
         * Convert a double into a simplified Fraction with good precision (continued fraction algorithm).
         */
        fun fromDouble(value: Double, maxDenominator: Long = 10000L): Fraction {
            if (value.isNaN() || value.isInfinite()) return ZERO
            val isNegative = value < 0
            val x = abs(value)

            var h1 = 1L
            var h0 = 0L
            var k1 = 0L
            var k0 = 1L

            var b = x
            do {
                val a = b.toLong()
                var aux = h1
                h1 = a * h1 + h0
                h0 = aux
                aux = k1
                k1 = a * k1 + k0
                k0 = aux
                if (k1 > maxDenominator) break
                b = 1.0 / (b - a.toDouble())
            } while (abs(x - h1.toDouble() / k1.toDouble()) > x * 1.0e-7 && b.isFinite() && b != 0.0)

            val finalNum = if (isNegative) -h1 else h1
            return Fraction(finalNum, if (k1 == 0L) 1L else k1)
        }

        fun fromString(str: String): Fraction? {
            val clean = str.trim()
            if (clean.contains("/")) {
                val parts = clean.split("/")
                if (parts.size == 2) {
                    val n = parts[0].trim().toLongOrNull()
                    val d = parts[1].trim().toLongOrNull()
                    if (n != null && d != null && d != 0L) {
                        return Fraction(n, d)
                    }
                }
            } else {
                val l = clean.toLongOrNull()
                if (l != null) return Fraction(l, 1)
                val d = clean.toDoubleOrNull()
                if (d != null) return fromDouble(d)
            }
            return null
        }

        private fun power(base: Long, exp: Int): Long {
            var res = 1L
            var b = base
            var e = exp
            while (e > 0) {
                if (e % 2 == 1) res *= b
                b *= b
                e /= 2
            }
            return res
        }
    }
}
