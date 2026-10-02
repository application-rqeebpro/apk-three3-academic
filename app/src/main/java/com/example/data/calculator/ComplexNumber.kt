package com.example.data.calculator

import kotlin.math.*

/**
 * Representation and operations for Complex Numbers (الأعداد المركبة)
 * Designed according to the 3rd Secondary Yemeni curriculum:
 * - Algebraic / Cartesian form: a + bi (الصورة الجبرية)
 * - Polar / Trigonometric form: [r , θ°] (الصورة القطبية [ر ، هـ])
 * - Conjugate, modulus, argument, powers, roots
 */
data class ComplexNumber(
    val real: Double,
    val imag: Double
) {
    val r: Double get() = hypot(real, imag)
    val thetaRad: Double get() = atan2(imag, real)
    val thetaDeg: Double
        get() {
            var deg = Math.toDegrees(thetaRad)
            if (deg < 0) deg += 360.0
            return deg
        }

    operator fun plus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(this.real + other.real, this.imag + other.imag)

    operator fun minus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(this.real - other.real, this.imag - other.imag)

    operator fun times(other: ComplexNumber): ComplexNumber =
        ComplexNumber(
            this.real * other.real - this.imag * other.imag,
            this.real * other.imag + this.imag * other.real
        )

    operator fun div(other: ComplexNumber): ComplexNumber {
        val denom = other.real * other.real + other.imag * other.imag
        require(denom != 0.0) { "لا يمكن القسمة على عدد مركب قيمته صفر" }
        val r = (this.real * other.real + this.imag * other.imag) / denom
        val i = (this.imag * other.real - this.real * other.imag) / denom
        return ComplexNumber(r, i)
    }

    /**
     * Conjugate of a + bi is a - bi (المرافق)
     */
    fun conjugate(): ComplexNumber = ComplexNumber(real, -imag)

    /**
     * Modulus |z| (المقياس ر)
     */
    fun abs(): Double = r

    /**
     * Power using De Moivre's Theorem: z^n = r^n [cos(nθ) + i sin(nθ)]
     */
    fun pow(n: Double): ComplexNumber {
        val newR = r.pow(n)
        val newTheta = thetaRad * n
        return ComplexNumber(newR * cos(newTheta), newR * sin(newTheta))
    }

    /**
     * Square root in polar format
     */
    fun sqrt(): ComplexNumber {
        val rootR = kotlin.math.sqrt(r)
        val halfTheta = thetaRad / 2.0
        return ComplexNumber(rootR * cos(halfTheta), rootR * sin(halfTheta))
    }

    /**
     * Algebraic string formatted cleanly: 3 + 4i or 3 - 4i
     */
    fun toAlgebraicString(): String {
        val rClean = formatDouble(real)
        val iClean = formatDouble(kotlin.math.abs(imag))
        return when {
            kotlin.math.abs(imag) < 1e-9 -> rClean
            kotlin.math.abs(real) < 1e-9 -> if (imag < 0) "-${iClean}i" else "${iClean}i"
            imag < 0 -> "$rClean - ${iClean}i"
            else -> "$rClean + ${iClean}i"
        }
    }

    /**
     * Polar string formatted as in the Yemeni Curriculum: [ر ، هـ°]
     */
    fun toPolarString(): String {
        val rFormatted = formatDouble(r)
        val degFormatted = formatDouble(thetaDeg)
        return "[$rFormatted ، $degFormatted°]"
    }

    override fun toString(): String = toAlgebraicString()

    companion object {
        val ZERO = ComplexNumber(0.0, 0.0)
        val ONE = ComplexNumber(1.0, 0.0)
        val I = ComplexNumber(0.0, 1.0)

        fun fromPolar(r: Double, thetaDegrees: Double): ComplexNumber {
            val rad = Math.toRadians(thetaDegrees)
            return ComplexNumber(r * cos(rad), r * sin(rad))
        }

        private fun formatDouble(d: Double): String {
            if (kotlin.math.abs(d - d.roundToLong()) < 1e-6) {
                return d.roundToLong().toString()
            }
            return String.format(java.util.Locale.US, "%.3f", d).trimEnd('0').trimEnd('.')
        }
    }
}
