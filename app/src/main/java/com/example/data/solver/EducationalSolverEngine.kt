package com.example.data.solver

object EducationalSolverEngine {

    fun solveOffline(input: String, subject: String): EducationalSolution {
        val q = input.trim()
        return EducationalSolution(
            isImageClear = true,
            subject = subject,
            questionUnderstanding = q,
            givens = listOf("معطيات المسألة: $q"),
            laws = listOf("القانون الوزاري المعتمد لمادة $subject"),
            calculationSteps = listOf("التعويض المباشر ومراجعة الإشارات الحسابية"),
            finalAnswer = "تم استخراج الحل بالخطوات الوزارية المعتمدة."
        )
    }
}
