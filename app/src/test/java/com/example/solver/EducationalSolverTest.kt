package com.example.solver

import com.example.data.remote.GeminiRepository
import com.example.data.solver.EducationalSolution
import com.example.data.solver.EducationalSolverEngine
import com.example.data.solver.ProblemVerification
import org.junit.Assert.*
import org.junit.Test

class EducationalSolverTest {

    // -------------------------------------------------------------
    // 1. ARITHMETIC (جمع، طرح، ضرب، قسمة)
    // -------------------------------------------------------------
    @Test
    fun testArithmeticOperationsAndPrecedence() {
        val res1 = EducationalSolverEngine.evaluateArithmetic("2 + 3 * 4")
        assertNotNull(res1)
        assertEquals(14.0, res1!!, 1e-6)

        val res2 = EducationalSolverEngine.evaluateArithmetic("(10 - 2) / 4")
        assertNotNull(res2)
        assertEquals(2.0, res2!!, 1e-6)

        val res3 = EducationalSolverEngine.evaluateArithmetic("15.5 - 5.5 + 2 * 3")
        assertNotNull(res3)
        assertEquals(16.0, res3!!, 1e-6)
    }

    // -------------------------------------------------------------
    // 2. EXPONENTS & ROOTS (الأسس والجذور)
    // -------------------------------------------------------------
    @Test
    fun testExponentsAndSquareRoots() {
        val powerRes = EducationalSolverEngine.evaluateArithmetic("2 ^ 3")
        assertNotNull(powerRes)
        assertEquals(8.0, powerRes!!, 1e-6)

        val rootRes = EducationalSolverEngine.evaluateArithmetic("sqrt(16)")
        assertNotNull(rootRes)
        assertEquals(4.0, rootRes!!, 1e-6)

        val pythagorasRes = EducationalSolverEngine.evaluateArithmetic("sqrt(3^2 + 4^2)")
        assertNotNull(pythagorasRes)
        assertEquals(5.0, pythagorasRes!!, 1e-6)
    }

    // -------------------------------------------------------------
    // 3. EQUATIONS & DETERMINANTS (المعادلات والمحددات)
    // -------------------------------------------------------------
    @Test
    fun testDeterminantsAndCramerEquations() {
        // 2x + y = 7
        // x - y = 2
        // Solution: x = 3, y = 1
        val solution = EducationalSolverEngine.solveCramer2x2(
            a1 = 2.0, b1 = 1.0, c1 = 7.0,
            a2 = 1.0, b2 = -1.0, c2 = 2.0
        )
        assertNotNull(solution)
        assertEquals(3.0, solution!!.first, 1e-6)
        assertEquals(1.0, solution.second, 1e-6)

        val det = EducationalSolverEngine.determinant2x2(2.0, 3.0, 1.0, 4.0)
        assertEquals(5.0, det, 1e-6) // 2*4 - 3*1 = 5
    }

    // -------------------------------------------------------------
    // 4. COMPLEX NUMBERS (الأعداد المركبة)
    // -------------------------------------------------------------
    @Test
    fun testComplexNumbersArithmeticAndPolarConversion() {
        val z1 = EducationalSolverEngine.ComplexNumber(3.0, 4.0)
        assertEquals(5.0, z1.modulus, 1e-6) // |3 + 4i| = 5
        assertEquals(53.13, z1.argumentDeg, 0.1)

        val z2 = EducationalSolverEngine.ComplexNumber(1.0, 2.0)
        val sum = z1 + z2
        assertEquals(4.0, sum.real, 1e-6)
        assertEquals(6.0, sum.imag, 1e-6)

        // (1 + 2i) * (3 - i) = 3 - i + 6i - 2i^2 = 5 + 5i
        val z3 = EducationalSolverEngine.ComplexNumber(1.0, 2.0)
        val z4 = EducationalSolverEngine.ComplexNumber(3.0, -1.0)
        val prod = z3 * z4
        assertEquals(5.0, prod.real, 1e-6)
        assertEquals(5.0, prod.imag, 1e-6)

        // De Moivre Power (1 + i)^2 = 2i
        val zUnit = EducationalSolverEngine.ComplexNumber(1.0, 1.0)
        val zSquared = zUnit.powerDeMoivre(2)
        assertEquals(0.0, zSquared.real, 1e-4)
        assertEquals(2.0, zSquared.imag, 1e-4)
    }

    // -------------------------------------------------------------
    // 5. VECTORS (المتجهات)
    // -------------------------------------------------------------
    @Test
    fun testVectorsDotProductAndMagnitude() {
        val v1 = EducationalSolverEngine.Vector3D(3.0, 4.0, 0.0)
        assertEquals(5.0, v1.magnitude, 1e-6)

        val v2 = EducationalSolverEngine.Vector3D(1.0, 2.0, 0.0)
        val v3 = EducationalSolverEngine.Vector3D(3.0, 4.0, 0.0)
        // Dot product: 1*3 + 2*4 = 11
        assertEquals(11.0, v2 dot v3, 1e-6)

        // Orthogonal vectors angle = 90 degrees
        val vx = EducationalSolverEngine.Vector3D(1.0, 0.0, 0.0)
        val vy = EducationalSolverEngine.Vector3D(0.0, 1.0, 0.0)
        assertEquals(90.0, vx.angleWith(vy), 1e-4)

        // Cross product of i and j is k (0, 0, 1)
        val cross = vx cross vy
        assertEquals(0.0, cross.x, 1e-6)
        assertEquals(0.0, cross.y, 1e-6)
        assertEquals(1.0, cross.z, 1e-6)
    }

    // -------------------------------------------------------------
    // 6. TRIGONOMETRY (المثلثات)
    // -------------------------------------------------------------
    @Test
    fun testTrigonometricValues() {
        val sin30 = EducationalSolverEngine.evaluateArithmetic("sin(30)")
        assertNotNull(sin30)
        assertEquals(0.5, sin30!!, 1e-4)

        val cos60 = EducationalSolverEngine.evaluateArithmetic("cos(60)")
        assertNotNull(cos60)
        assertEquals(0.5, cos60!!, 1e-4)

        val tan45 = EducationalSolverEngine.evaluateArithmetic("tan(45)")
        assertNotNull(tan45)
        assertEquals(1.0, tan45!!, 1e-4)
    }

    // -------------------------------------------------------------
    // 7. PHYSICS & UNIT CONVERSIONS (الفيزياء وتحويل الوحدات)
    // -------------------------------------------------------------
    @Test
    fun testPhysicsUnitConversionsAndAcCircuit() {
        // Speed conversion: 72 km/h = 20 m/s
        assertEquals(20.0, EducationalSolverEngine.Physics.kmhToMs(72.0), 1e-6)
        assertEquals(72.0, EducationalSolverEngine.Physics.msToKmh(20.0), 1e-6)

        // Time conversion: 2 hours = 7200 seconds
        assertEquals(7200.0, EducationalSolverEngine.Physics.hoursToSeconds(2.0), 1e-6)

        // Capacitance: 50 microFarads = 50 * 10^-6 Farads
        assertEquals(5.0e-5, EducationalSolverEngine.Physics.microFaradToFarad(50.0), 1e-9)

        // AC circuit test: R = 30, XL = 80, XC = 40 => Z = 50, I = 100/50 = 2 A
        val acResult = EducationalSolverEngine.Physics.calculateAcCircuit(
            voltage = 100.0,
            frequency = 50.0,
            resistance = 30.0,
            inductanceHenry = 80.0 / (2 * Math.PI * 50.0),
            capacitanceFarad = 1.0 / (2 * Math.PI * 50.0 * 40.0)
        )
        assertEquals(50.0, acResult.z, 0.5)
        assertEquals(2.0, acResult.current, 0.05)
    }

    // -------------------------------------------------------------
    // 8. CHEMISTRY (الكيمياء وحساب المولات و pH)
    // -------------------------------------------------------------
    @Test
    fun testChemistryMolesAndPhCalculation() {
        // Moles: 36.03 g of H2O (M = 18.015) = 2.0 moles
        val moles = EducationalSolverEngine.Chemistry.calculateMoles(36.03, 18.015)
        assertEquals(2.0, moles, 1e-3)

        // pH: [H+] = 10^-3 M => pH = 3.0, pOH = 11.0
        val ph = EducationalSolverEngine.Chemistry.calculatePh(1.0e-3)
        assertEquals(3.0, ph, 1e-4)
        assertEquals(11.0, EducationalSolverEngine.Chemistry.calculatePoh(ph), 1e-4)
    }

    // -------------------------------------------------------------
    // 9. UNCLEAR IMAGE HANDLING (ممنوع اختراع البيانات)
    // -------------------------------------------------------------
    @Test
    fun testUnclearImageRejection() {
        val repo = GeminiRepository()
        val solution = repo.getOfflineStructuredSolution("صورة غير واضحة للسؤال", "الرياضيات")

        assertFalse("يجب أن يرفض النظام حل الصورة غير الواضحة منعاً للتخمين الخاطئ", solution.isImageClear)
        assertEquals("unclear", solution.confidence)
        assertNotNull(solution.unclearReason)
        assertTrue(solution.unclearReason!!.contains("غير واضح في الصورة"))
        assertFalse(solution.verification.isValid)
    }

    // -------------------------------------------------------------
    // 10. VERIFY_SOLUTION PROTOCOL (مرحلة التدقيق المستقلة)
    // -------------------------------------------------------------
    @Test
    fun testVerifySolutionProtocol() {
        val solution = EducationalSolution(
            isImageClear = true,
            subject = "الفيزياء",
            questionUnderstanding = "حساب شدة التيار في دائرة تيار متردد",
            givens = listOf("R = 30 Ω", "XL = 80 Ω", "XC = 40 Ω", "V = 100 V"),
            required = "حساب الممانعة Z وشدة التيار I",
            laws = listOf("Z = √(R² + (XL - XC)²)", "I = V / Z"),
            substitutionSteps = listOf("Z = √(30² + 40²) = 50 Ω", "I = 100 / 50 = 2 A"),
            calculationSteps = listOf("1. (80 - 40) = 40", "2. 900 + 1600 = 2500", "3. √2500 = 50"),
            unitCheck = "الممانعة بالأوم والجهد بالفولت والتيار بالأمبير (SI)",
            verification = ProblemVerification(),
            finalAnswer = "Z = 50 Ω , I = 2 A",
            multipleChoiceAnswer = "(ب)",
            confidence = "high",
            easierExplanation = "شرح مبسط جداً بمثال قائم الزاوية"
        )

        val report = EducationalSolverEngine.verifySolution(solution)
        assertTrue("يجب أن تجتاز المسألة المستوفية لجميع الخطوات مرحلة التحقق بنجاح", report.isValid)
        assertTrue(report.checksList.isNotEmpty())
        assertTrue(report.verificationDetails.contains("تم فحص وتدقيق"))
    }
}
