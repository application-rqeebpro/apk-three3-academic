package com.example.solver

import com.example.data.solver.EducationalSolution
import com.example.data.solver.EducationalSolverEngine
import org.junit.Assert.*
import org.junit.Test

class EducationalSolverTest {

    @Test
    fun testEducationalSolverOffline() {
        val solution = EducationalSolverEngine.solveOffline(
            input = "سقط جسم من ارتفاع 20 متر، احسب سرعته لحظة وصوله للأرض",
            subject = "الفيزياء"
        )
        assertNotNull(solution)
        assertTrue(solution.isImageClear)
        assertEquals("الفيزياء", solution.subject)
        assertTrue(solution.laws.isNotEmpty())
        assertTrue(solution.calculationSteps.isNotEmpty())
    }

    @Test
    fun testEducationalSolverVerification() {
        val solution = EducationalSolution(
            isImageClear = true,
            subject = "الكيمياء",
            questionUnderstanding = "احسب عدد المولات في 10 غرام من NaOH",
            givens = listOf("الكتلة = 10g", "الكتلة المولية = 40g/mol"),
            laws = listOf("عدد المولات = الكتلة / الكتلة المولية"),
            calculationSteps = listOf("n = 10 / 40 = 0.25 mol"),
            finalAnswer = "0.25 مول"
        )
        val verification = EducationalSolverEngine.verifySolution(solution)
        assertTrue(verification.isValid)
        assertTrue(verification.checksList.isNotEmpty())
    }
}
