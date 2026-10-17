package com.example.data.calculator

data class Matrix(val rows: Int, val cols: Int, val data: List<List<Double>>) {
    operator fun get(r: Int, c: Int): Double = data[r][c]

    operator fun plus(other: Matrix): Matrix {
        val res = (0 until rows).map { r ->
            (0 until cols).map { c -> get(r, c) + other[r, c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun minus(other: Matrix): Matrix {
        val res = (0 until rows).map { r ->
            (0 until cols).map { c -> get(r, c) - other[r, c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun times(other: Matrix): Matrix {
        val res = (0 until rows).map { r ->
            (0 until other.cols).map { c ->
                (0 until cols).sumOf { k -> get(r, k) * other[k, c] }
            }
        }
        return Matrix(rows, other.cols, res)
    }

    fun transpose(): Matrix {
        val res = (0 until cols).map { c ->
            (0 until rows).map { r -> get(r, c) }
        }
        return Matrix(cols, rows, res)
    }

    fun determinant(): Double {
        if (rows != cols) throw IllegalArgumentException("المصفوفة ليست مربعة")
        if (rows == 2) return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0)
        if (rows == 3) {
            val a = get(0, 0) * (get(1, 1) * get(2, 2) - get(1, 2) * get(2, 1))
            val b = get(0, 1) * (get(1, 0) * get(2, 2) - get(1, 2) * get(2, 0))
            val c = get(0, 2) * (get(1, 0) * get(2, 1) - get(1, 1) * get(2, 0))
            return a - b + c
        }
        return 0.0
    }

    fun inverse(): Matrix? {
        val det = determinant()
        if (kotlin.math.abs(det) < 1e-9) return null
        if (rows == 2) {
            val res = listOf(
                listOf(get(1, 1) / det, -get(0, 1) / det),
                listOf(-get(1, 0) / det, get(0, 0) / det)
            )
            return Matrix(2, 2, res)
        }
        if (rows == 3) {
            val res = (0 until 3).map { r ->
                (0 until 3).map { c ->
                    val minorR = (0 until 3).filter { it != c }
                    val minorC = (0 until 3).filter { it != r }
                    val mDet = get(minorR[0], minorC[0]) * get(minorR[1], minorC[1]) - get(minorR[0], minorC[1]) * get(minorR[1], minorC[0])
                    val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                    (sign * mDet) / det
                }
            }
            return Matrix(3, 3, res)
        }
        return null
    }

    companion object {
        fun formatVal(v: Double): String {
            return if (kotlin.math.abs(v - kotlin.math.round(v)) < 1e-9) {
                kotlin.math.round(v).toLong().toString()
            } else {
                String.format(java.util.Locale.US, "%.3f", v).trimEnd('0').trimEnd('.')
            }
        }
    }
}
