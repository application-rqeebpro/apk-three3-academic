package com.example.data.calculator

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * High-precision Matrix operations for 2x2 and 3x3 matrices
 * Supports: Addition, Subtraction, Multiplication, Determinant, Transpose, Inverse
 */
data class Matrix(
    val rows: Int,
    val cols: Int,
    val data: List<List<Double>>
) {
    init {
        require(rows > 0 && cols > 0)
        require(data.size == rows)
        require(data.all { it.size == cols })
    }

    operator fun get(r: Int, c: Int): Double = data[r][c]

    operator fun plus(other: Matrix): Matrix {
        require(this.rows == other.rows && this.cols == other.cols) { "يجب أن تكون المصفوفتان من نفس الرتبة لإجراء الجمع" }
        val res = (0 until rows).map { r ->
            (0 until cols).map { c -> this[r, c] + other[r, c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun minus(other: Matrix): Matrix {
        require(this.rows == other.rows && this.cols == other.cols) { "يجب أن تكون المصفوفتان من نفس الرتبة لإجراء الطرح" }
        val res = (0 until rows).map { r ->
            (0 until cols).map { c -> this[r, c] - other[r, c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun times(other: Matrix): Matrix {
        require(this.cols == other.rows) { "عدد أعمدة الأولى (${this.cols}) يجب أن يساوي عدد صفوف الثانية (${other.rows})" }
        val res = (0 until this.rows).map { r ->
            (0 until other.cols).map { c ->
                var sum = 0.0
                for (k in 0 until this.cols) {
                    sum += this[r, k] * other[k, c]
                }
                sum
            }
        }
        return Matrix(this.rows, other.cols, res)
    }

    operator fun times(scalar: Double): Matrix {
        val res = (0 until rows).map { r ->
            (0 until cols).map { c -> this[r, c] * scalar }
        }
        return Matrix(rows, cols, res)
    }

    fun transpose(): Matrix {
        val res = (0 until cols).map { c ->
            (0 until rows).map { r -> this[r, c] }
        }
        return Matrix(cols, rows, res)
    }

    fun determinant(): Double {
        require(rows == cols) { "المحدد يحسب للمصفوفة المربعة فقط" }
        return when (rows) {
            1 -> this[0, 0]
            2 -> this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
            3 -> {
                val a = this[0, 0] * (this[1, 1] * this[2, 2] - this[1, 2] * this[2, 1])
                val b = this[0, 1] * (this[1, 0] * this[2, 2] - this[1, 2] * this[2, 0])
                val c = this[0, 2] * (this[1, 0] * this[2, 1] - this[1, 1] * this[2, 0])
                a - b + c
            }
            else -> throw IllegalArgumentException("رتبة المصفوفة غير مدعومة")
        }
    }

    fun inverse(): Matrix? {
        val det = determinant()
        if (abs(det) < 1e-11) return null // Singular matrix (ليس لها معكوس)
        val invDet = 1.0 / det

        return when (rows) {
            2 -> {
                val invData = listOf(
                    listOf(this[1, 1] * invDet, -this[0, 1] * invDet),
                    listOf(-this[1, 0] * invDet, this[0, 0] * invDet)
                )
                Matrix(2, 2, invData)
            }
            3 -> {
                val adjData = mutableListOf<MutableList<Double>>()
                for (r in 0 until 3) {
                    val rowList = mutableListOf<Double>()
                    for (c in 0 until 3) {
                        val subMatrix = getMinor(r, c)
                        val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                        val cofactor = sign * subMatrix.determinant()
                        rowList.add(cofactor * invDet)
                    }
                    adjData.add(rowList)
                }
                // Transpose of cofactor matrix gives the inverse!
                Matrix(3, 3, adjData).transpose()
            }
            else -> null
        }
    }

    private fun getMinor(rowToRemove: Int, colToRemove: Int): Matrix {
        val minorData = (0 until rows).filter { it != rowToRemove }.map { r ->
            (0 until cols).filter { it != colToRemove }.map { c ->
                this[r, c]
            }
        }
        return Matrix(rows - 1, cols - 1, minorData)
    }

    fun format(): String {
        return data.joinToString("\n") { row ->
            row.joinToString("   ") { formatVal(it) }
        }
    }

    companion object {
        fun identity(size: Int): Matrix {
            val d = (0 until size).map { r ->
                (0 until size).map { c -> if (r == c) 1.0 else 0.0 }
            }
            return Matrix(size, size, d)
        }

        fun from2x2(a: Double, b: Double, c: Double, d: Double): Matrix {
            return Matrix(2, 2, listOf(listOf(a, b), listOf(c, d)))
        }

        fun from3x3(
            a1: Double, a2: Double, a3: Double,
            b1: Double, b2: Double, b3: Double,
            c1: Double, c2: Double, c3: Double
        ): Matrix {
            return Matrix(3, 3, listOf(
                listOf(a1, a2, a3),
                listOf(b1, b2, b3),
                listOf(c1, c2, c3)
            ))
        }

        fun formatVal(v: Double): String {
            if (abs(v - v.roundToLong()) < 1e-6) {
                return v.roundToLong().toString()
            }
            return String.format(java.util.Locale.US, "%.2f", v).trimEnd('0').trimEnd('.')
        }
    }
}
