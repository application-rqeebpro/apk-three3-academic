package com.example.data.calculator

import java.util.Locale
import kotlin.math.*

data class ComplexNumber(val real: Double, val imag: Double) {
    val modulus: Double get() = sqrt(real * real + imag * imag)
    val argumentDeg: Double get() {
        val rad = atan2(imag, real)
        val deg = Math.toDegrees(rad)
        return if (deg < 0) deg + 360 else deg
    }
    val argumentRad: Double get() {
        val rad = atan2(imag, real)
        return if (rad < 0) rad + 2 * PI else rad
    }

    operator fun plus(other: ComplexNumber) = ComplexNumber(real + other.real, imag + other.imag)
    operator fun minus(other: ComplexNumber) = ComplexNumber(real - other.real, imag - other.imag)
    operator fun times(other: ComplexNumber) = ComplexNumber(
        real * other.real - imag * other.imag,
        real * other.imag + imag * other.real
    )
    operator fun div(other: ComplexNumber): ComplexNumber {
        val denom = other.real * other.real + other.imag * other.imag
        if (denom == 0.0) throw ArithmeticException("القسمة على صفر")
        return ComplexNumber(
            (real * other.real + imag * other.imag) / denom,
            (imag * other.real - real * other.imag) / denom
        )
    }

    fun conjugate() = ComplexNumber(real, -imag)
    fun power(n: Int): ComplexNumber {
        val rN = modulus.pow(n)
        val thetaN = argumentRad * n
        return ComplexNumber(rN * cos(thetaN), rN * sin(thetaN))
    }

    fun toCartesianString(): String {
        val rStr = if (abs(real) < 1e-9) "" else String.format(Locale.US, "%.4f", real).trimEnd('0').trimEnd('.')
        val iVal = abs(imag)
        val iStr = if (abs(iVal) < 1e-9) "" else if (abs(iVal - 1.0) < 1e-9) "ت" else String.format(Locale.US, "%.4f", iVal).trimEnd('0').trimEnd('.') + "ت"

        return when {
            rStr.isEmpty() && iStr.isEmpty() -> "0"
            rStr.isEmpty() -> if (imag < 0) "-$iStr" else iStr
            iStr.isEmpty() -> rStr
            imag < 0 -> "$rStr - $iStr"
            else -> "$rStr + $iStr"
        }
    }

    fun toPolarString(): String {
        val r = String.format(Locale.US, "%.4f", modulus).trimEnd('0').trimEnd('.')
        val deg = String.format(Locale.US, "%.2f", argumentDeg).trimEnd('0').trimEnd('.')
        return "[$r ، $deg°]"
    }

    companion object {
        val ZERO = ComplexNumber(0.0, 0.0)
        val ONE = ComplexNumber(1.0, 0.0)
        val I = ComplexNumber(0.0, 1.0)
    }
}
