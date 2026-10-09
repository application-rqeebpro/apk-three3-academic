package com.example.data.calculator

data class Matrix(val rows: Int, val cols: Int, val data: List<Double>) {
    operator fun get(r: Int, c: Int) = data[r * cols + c]

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
}
