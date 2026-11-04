package com.example.calculator

import com.example.data.calculator.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScientificEvaluatorTest {

    @Test
    fun testBasicArithmetic() {
        val r1 = ScientificEvaluator.evaluate("2 + 3", AngleMode.DEGREE)
        assertEquals("5", r1.primaryDisplay)

        val r2 = ScientificEvaluator.evaluate("10 − 4", AngleMode.DEGREE)
        assertEquals("6", r2.primaryDisplay)

        val r3 = ScientificEvaluator.evaluate("3 × 4", AngleMode.DEGREE)
        assertEquals("12", r3.primaryDisplay)

        val r4 = ScientificEvaluator.evaluate("20 ÷ 5", AngleMode.DEGREE)
        assertEquals("4", r4.primaryDisplay)
    }

    @Test
    fun testPowersAndRoots() {
        val rSqrt = ScientificEvaluator.evaluate("sqrt(16)", AngleMode.DEGREE)
        assertEquals("4", rSqrt.primaryDisplay)
    }

    @Test
    fun testTrigonometryInDeg() {
        val rSin = ScientificEvaluator.evaluate("sin(30)", AngleMode.DEGREE)
        assertEquals("0.5", rSin.primaryDisplay)
    }

    @Test
    fun testFractions() {
        val f1 = Fraction(2, 4)
        val simplified = f1.simplify()
        assertEquals(1L, simplified.num)
        assertEquals(2L, simplified.den)
    }

    @Test
    fun testComplexNumbers() {
        val c1 = ComplexNumber(3.0, 4.0)
        assertEquals(5.0, c1.modulus, 1e-6)

        val c2 = ComplexNumber(1.0, 2.0)
        val sum = c1 + c2
        assertEquals(4.0, sum.real, 1e-6)
        assertEquals(6.0, sum.imag, 1e-6)
    }

    @Test
    fun testMatrixDeterminant() {
        val m = Matrix(2, 2, listOf(listOf(2.0, 1.0), listOf(3.0, 4.0)))
        val det = m.determinant()
        assertEquals(5.0, det, 1e-6) // 2*4 - 1*3 = 8 - 3 = 5
    }
}
