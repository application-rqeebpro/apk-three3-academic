package com.example.calculator

import com.example.data.calculator.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.math.abs

class ScientificEvaluatorTest {

    @Test
    fun testBasicArithmetic() {
        val r1 = ScientificEvaluator.evaluate("2 + 3", AngleMode.DEG)
        assertEquals(5.0, r1.decimalValue, 1e-6)

        val r2 = ScientificEvaluator.evaluate("10 − 4", AngleMode.DEG)
        assertEquals(6.0, r2.decimalValue, 1e-6)

        val r3 = ScientificEvaluator.evaluate("3 × 4", AngleMode.DEG)
        assertEquals(12.0, r3.decimalValue, 1e-6)

        val r4 = ScientificEvaluator.evaluate("20 ÷ 5", AngleMode.DEG)
        assertEquals(4.0, r4.decimalValue, 1e-6)
    }

    @Test
    fun testPowersAndRoots() {
        val rPow = ScientificEvaluator.evaluate("2²", AngleMode.DEG)
        assertEquals(4.0, rPow.decimalValue, 1e-6)

        val rSqrt = ScientificEvaluator.evaluate("√16", AngleMode.DEG)
        assertEquals(4.0, rSqrt.decimalValue, 1e-6)
    }

    @Test
    fun testTrigonometryInDeg() {
        val rSin = ScientificEvaluator.evaluate("sin(30)", AngleMode.DEG)
        assertEquals(0.5, rSin.decimalValue, 1e-6)
    }

    @Test
    fun testFractions() {
        // 1/2 + 1/4 = 3/4
        val f1 = Fraction(1, 2)
        val f2 = Fraction(1, 4)
        val sum = f1 + f2
        assertEquals(3L, sum.num)
        assertEquals(4L, sum.den)

        // 2/3 * 3/4 = 1/2 (simplified)
        val f3 = Fraction(2, 3)
        val f4 = Fraction(3, 4)
        val prod = f3 * f4
        assertEquals(1L, prod.num)
        assertEquals(2L, prod.den)
    }

    @Test
    fun testEquationSolver() {
        // 2x + 4 = 10 -> x = 3
        val eqResult = EquationSolver.solveLinear(2.0, 4.0, 10.0)
        assertEquals("س = 3", eqResult.finalAnswer)
    }

    @Test
    fun testComplexNumbers() {
        // z = [4, 60°] -> sqrt(z) = [2, 30°]
        val z = ComplexNumber.fromPolar(4.0, 60.0)
        val sqrtZ = z.sqrt()
        assertEquals(2.0, sqrtZ.r, 1e-6)
        assertEquals(30.0, sqrtZ.thetaDeg, 1e-6)
    }

    @Test
    fun testMatrixDeterminant() {
        val m = Matrix.from2x2(1.0, 2.0, 3.0, 4.0)
        // det = 1*4 - 2*3 = -2
        assertEquals(-2.0, m.determinant(), 1e-6)
    }
}
