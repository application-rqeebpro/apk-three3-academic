package com.example.data.solver

data class QuestionSolvedItem(
    val questionNumber: Int = 1,
    val questionTitle: String = "السؤال 1",
    val questionText: String = "",
    val questionType: String = "MULTIPLE_CHOICE", // "MULTIPLE_CHOICE", "TRUE_FALSE", "CALCULATION", "ESSAY"
    val selectedOptionLabel: String = "", // e.g. "الاختيار ①" or "الاختيار ③"
    val selectedOptionText: String? = null,
    val isTrue: Boolean? = null, // true = صح, false = خطأ
    val correction: String? = null,
    val finalAnswer: String = "",
    val givens: List<String> = emptyList(),
    val laws: List<String> = emptyList(),
    val substitutionSteps: List<String> = emptyList(),
    val calculationSteps: List<String> = emptyList(),
    val unitCheck: String? = null,
    val easierExplanation: String? = null
) {
    val correctChoice: String
        get() = when {
            selectedOptionLabel.isNotBlank() -> "$selectedOptionLabel ${selectedOptionText ?: ""}".trim()
            isTrue != null -> if (isTrue == true) "صواب ✓" else "خطأ ✗ ${correction?.let { "($it)" } ?: ""}"
            finalAnswer.isNotBlank() -> finalAnswer
            else -> "إجابة محددة"
        }

    val stepsOrReason: String
        get() = when {
            calculationSteps.isNotEmpty() -> calculationSteps.joinToString("\n• ")
            substitutionSteps.isNotEmpty() -> substitutionSteps.joinToString("\n• ")
            correction != null -> correction
            else -> ""
        }
}

data class ProblemVerification(
    val isValid: Boolean = true,
    val verificationDetails: String = "✓ تم التحقق المستقل ومراجعة الخطوات الرياضية والعلمية بدقة",
    val checksList: List<String> = listOf(
        "✓ تدقيق قراءة وفهم المسألة ومعطياتها",
        "✓ فحص صحة القوانين المطبقة وفق المنهج اليمني",
        "✓ مراجعة الحسابات والتعويضات بدقة",
        "✓ التحقق من منطقية الناتج النهائي والوحدات"
    ),
    val alternativeCheck: String? = null,
    val commonMistakesAvoided: String? = null
)

data class EducationalSolution(
    val isImageClear: Boolean = true,
    val unclearReason: String? = null,
    val subject: String = "عام",
    val questionUnderstanding: String = "",
    val givens: List<String> = emptyList(),
    val required: String = "",
    val laws: List<String> = emptyList(),
    val substitutionSteps: List<String> = emptyList(),
    val calculationSteps: List<String> = emptyList(),
    val unitCheck: String? = null,
    val verification: ProblemVerification = ProblemVerification(),
    val finalAnswer: String = "",
    val multipleChoiceAnswer: String? = null,
    val confidence: String = "high",
    val easierExplanation: String = "",
    val items: List<QuestionSolvedItem> = emptyList(),
    val isExamSheet: Boolean = false,
    val totalQuestionsCount: Int = 0
) {
    fun resolvedItems(): List<QuestionSolvedItem> {
        if (items.isNotEmpty()) return items

        // If items is empty but single solution has content, return as item 1
        if (finalAnswer.isNotBlank() || calculationSteps.isNotEmpty() || givens.isNotEmpty()) {
            return listOf(
                QuestionSolvedItem(
                    questionNumber = 1,
                    questionTitle = "المسألة",
                    questionText = questionUnderstanding,
                    questionType = when {
                        multipleChoiceAnswer != null -> "MULTIPLE_CHOICE"
                        finalAnswer.contains("صح") || finalAnswer.contains("خطأ") -> "TRUE_FALSE"
                        else -> "CALCULATION"
                    },
                    selectedOptionLabel = multipleChoiceAnswer ?: "",
                    finalAnswer = finalAnswer,
                    givens = givens,
                    laws = laws,
                    substitutionSteps = substitutionSteps,
                    calculationSteps = calculationSteps,
                    unitCheck = unitCheck,
                    easierExplanation = easierExplanation
                )
            )
        }
        return emptyList()
    }
}

fun EducationalSolution.toFormattedEducationalText(): String {
    val sb = StringBuilder()
    val allItems = resolvedItems()

    if (allItems.size > 1) {
        sb.appendLine("════════════════════════════════════════")
        sb.appendLine("🇾🇪 نموذج الإجابة النموذجي الكامل للاختبار الوزاري (${allItems.size} سؤالاً)")
        sb.appendLine("مادة: $subject • منصة رقيب للتعليم الثانوي")
        sb.appendLine("════════════════════════════════════════\n")

        for (item in allItems) {
            sb.appendLine("【السؤال ${item.questionNumber}】: ${item.questionText}")
            when (item.questionType) {
                "MULTIPLE_CHOICE" -> {
                    sb.appendLine("• الإجابة الصحيحة: ${item.selectedOptionLabel} ${item.selectedOptionText ?: ""}")
                }
                "TRUE_FALSE" -> {
                    val status = if (item.isTrue == true) "✓ صــواب" else "✗ خــطـأ"
                    sb.appendLine("• الحكم: $status")
                    if (item.correction?.isNotBlank() == true) {
                        sb.appendLine("• التصحيح: ${item.correction}")
                    }
                }
                else -> {
                    sb.appendLine("• الناتج النهائي: ${item.finalAnswer}")
                }
            }
            if (item.calculationSteps.isNotEmpty()) {
                sb.appendLine("• خطوات التوضيح: " + item.calculationSteps.joinToString(" ⟵ "))
            }
            sb.appendLine("────────────────────────────────────────\n")
        }
        return sb.toString()
    }

    val item = allItems.firstOrNull()
    sb.appendLine("🇾🇪 الحل النموذجي بالخطوات الوزارية (مادة $subject)")
    sb.appendLine("════════════════════════════════════════")
    if (questionUnderstanding.isNotBlank()) {
        sb.appendLine("📌 فهم السؤال: $questionUnderstanding\n")
    }
    if (givens.isNotEmpty()) {
        sb.appendLine("📋 المعطيات:")
        givens.forEach { sb.appendLine("  • $it") }
        sb.appendLine()
    }
    if (required.isNotBlank()) {
        sb.appendLine("🎯 المطلوب: $required\n")
    }
    if (laws.isNotEmpty()) {
        sb.appendLine("📐 القوانين المطبقة:")
        laws.forEach { sb.appendLine("  • $it") }
        sb.appendLine()
    }
    if (substitutionSteps.isNotEmpty()) {
        sb.appendLine("🔢 خطوات التعويض:")
        substitutionSteps.forEach { sb.appendLine("  • $it") }
        sb.appendLine()
    }
    if (calculationSteps.isNotEmpty()) {
        sb.appendLine("✍️ العمليات الحسابية:")
        calculationSteps.forEach { sb.appendLine("  • $it") }
        sb.appendLine()
    }
    if (finalAnswer.isNotBlank()) {
        sb.appendLine("🏁 الناتج النهائي: $finalAnswer\n")
    }
    if (item?.selectedOptionLabel?.isNotBlank() == true) {
        sb.appendLine("🔘 الاختيار الصحيح: ${item.selectedOptionLabel}\n")
    }
    if (easierExplanation.isNotBlank()) {
        sb.appendLine("💡 تبسيط الفكرة: $easierExplanation\n")
    }
    return sb.toString()
}
