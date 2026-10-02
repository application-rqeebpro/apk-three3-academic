package com.example.data.solver

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProblemVerification(
    @Json(name = "is_valid")
    val isValid: Boolean = true,
    @Json(name = "verification_details")
    val verificationDetails: String = "",
    @Json(name = "checks_list")
    val checksList: List<String> = emptyList(),
    @Json(name = "alternative_check")
    val alternativeCheck: String? = null,
    @Json(name = "common_mistakes_avoided")
    val commonMistakesAvoided: String? = null
)

@JsonClass(generateAdapter = true)
data class QuestionSolvedItem(
    @Json(name = "question_number")
    val questionNumber: Int = 1,
    @Json(name = "question_title")
    val questionTitle: String = "السؤال 1",
    @Json(name = "question_text")
    val questionText: String = "",
    @Json(name = "question_type")
    val questionType: String = "CALCULATION", // "MULTIPLE_CHOICE", "TRUE_FALSE", "CALCULATION"
    @Json(name = "selected_option_label")
    val selectedOptionLabel: String? = null, // e.g. "الاختيار ③"
    @Json(name = "selected_option_text")
    val selectedOptionText: String? = null, // e.g. "30°"
    @Json(name = "is_true")
    val isTrue: Boolean? = null, // true = صح, false = خطأ
    @Json(name = "correction")
    val correction: String? = null, // if false, brief correction
    @Json(name = "final_answer")
    val finalAnswer: String = "",
    @Json(name = "givens")
    val givens: List<String> = emptyList(),
    @Json(name = "laws")
    val laws: List<String> = emptyList(),
    @Json(name = "substitution_steps")
    val substitutionSteps: List<String> = emptyList(),
    @Json(name = "calculation_steps")
    val calculationSteps: List<String> = emptyList(),
    @Json(name = "verification_note")
    val verificationNote: String? = null
)

@JsonClass(generateAdapter = true)
data class EducationalSolution(
    @Json(name = "is_image_clear")
    val isImageClear: Boolean = true,
    @Json(name = "unclear_reason")
    val unclearReason: String? = null,
    @Json(name = "subject")
    val subject: String = "عام",
    @Json(name = "items")
    val items: List<QuestionSolvedItem> = emptyList(),
    // Backward compatibility fields for single-question responses
    @Json(name = "question_understanding")
    val questionUnderstanding: String = "",
    @Json(name = "question_type")
    val questionType: String = "CALCULATION",
    @Json(name = "selected_option_label")
    val selectedOptionLabel: String? = null,
    @Json(name = "selected_option_text")
    val selectedOptionText: String? = null,
    @Json(name = "is_true")
    val isTrue: Boolean? = null,
    @Json(name = "correction")
    val correction: String? = null,
    @Json(name = "givens")
    val givens: List<String> = emptyList(),
    @Json(name = "required")
    val required: String = "",
    @Json(name = "laws")
    val laws: List<String> = emptyList(),
    @Json(name = "substitution_steps")
    val substitutionSteps: List<String> = emptyList(),
    @Json(name = "calculation_steps")
    val calculationSteps: List<String> = emptyList(),
    @Json(name = "unit_check")
    val unitCheck: String? = null,
    @Json(name = "verification")
    val verification: ProblemVerification = ProblemVerification(),
    @Json(name = "final_answer")
    val finalAnswer: String = "",
    @Json(name = "multiple_choice_answer")
    val multipleChoiceAnswer: String? = null,
    @Json(name = "confidence")
    val confidence: String = "high", // "high", "medium", "unclear"
    @Json(name = "easier_explanation")
    val easierExplanation: String = ""
) {
    /**
     * Resolves the list of questions, converting legacy single-question formats if needed.
     */
    fun resolvedItems(): List<QuestionSolvedItem> {
        if (items.isNotEmpty()) return items

        // Infer question type from available properties
        val type = when {
            questionType.isNotBlank() && questionType != "CALCULATION" -> questionType
            isTrue != null -> "TRUE_FALSE"
            !selectedOptionLabel.isNullOrBlank() || !multipleChoiceAnswer.isNullOrBlank() -> "MULTIPLE_CHOICE"
            else -> "CALCULATION"
        }

        val optLabel = when {
            !selectedOptionLabel.isNullOrBlank() -> MathFormatter.formatOptionLabel(selectedOptionLabel)
            !multipleChoiceAnswer.isNullOrBlank() -> MathFormatter.formatOptionLabel(multipleChoiceAnswer)
            else -> null
        }

        return listOf(
            QuestionSolvedItem(
                questionNumber = 1,
                questionTitle = "السؤال",
                questionText = questionUnderstanding,
                questionType = type,
                selectedOptionLabel = optLabel,
                selectedOptionText = selectedOptionText?.ifBlank { null } ?: finalAnswer.takeIf { type == "MULTIPLE_CHOICE" },
                isTrue = isTrue,
                correction = correction,
                finalAnswer = finalAnswer,
                givens = givens,
                laws = laws,
                substitutionSteps = substitutionSteps,
                calculationSteps = calculationSteps,
                verificationNote = verification.verificationDetails.takeIf { it.isNotBlank() }
            )
        )
    }
}

fun EducationalSolution.toFormattedEducationalText(): String {
    if (!isImageClear) {
        val reason = unclearReason ?: "الجزء الخاص بالسؤال غير واضح في الصورة، أرسل صورة أوضح حتى أحل السؤال بدقة وبشكل صحيح."
        return "تنبيه بخصوص وضوح السؤال / الصورة:\n${MathFormatter.cleanMathText(reason)}"
    }

    val qItems = resolvedItems()
    val isMultiple = qItems.size > 1
    val sb = StringBuilder()

    qItems.forEachIndexed { index, item ->
        if (isMultiple) {
            sb.append("السؤال ${index + 1}\n")
        }

        when (item.questionType) {
            "MULTIPLE_CHOICE" -> {
                val label = MathFormatter.formatOptionLabel(item.selectedOptionLabel)
                val text = MathFormatter.cleanItem(item.selectedOptionText ?: item.finalAnswer)
                if (label.isNotBlank()) {
                    sb.append("الإجابة الصحيحة: $label\n")
                }
                if (text.isNotBlank()) {
                    sb.append("الإجابة: $text\n")
                }
            }
            "TRUE_FALSE" -> {
                if (item.isTrue == true) {
                    sb.append("الإجابة: صح ✅\n")
                } else if (item.isTrue == false) {
                    sb.append("الإجابة: خطأ ❌\n")
                    if (!item.correction.isNullOrBlank()) {
                        sb.append("التصحيح: ${MathFormatter.cleanMathText(item.correction)}\n")
                    }
                } else {
                    sb.append("الإجابة: ${MathFormatter.cleanItem(item.finalAnswer)}\n")
                }
            }
            else -> { // CALCULATION or default
                if (item.finalAnswer.isNotBlank()) {
                    val cleanAns = MathFormatter.cleanItem(item.finalAnswer)
                    sb.append("الإجابة النهائية: $cleanAns\n")
                }
            }
        }

        // Detailed steps if available
        val cleanedGivens = MathFormatter.cleanItemList(item.givens)
        val cleanedLaws = MathFormatter.cleanItemList(item.laws)
        val cleanedSubst = MathFormatter.cleanItemList(item.substitutionSteps)
        val cleanedCalcs = MathFormatter.cleanItemList(item.calculationSteps)

        val hasExplanation = cleanedGivens.isNotEmpty() || cleanedLaws.isNotEmpty() ||
                cleanedSubst.isNotEmpty() || cleanedCalcs.isNotEmpty()

        if (hasExplanation) {
            sb.append("\nشرح الحل:\n")
            if (cleanedGivens.isNotEmpty()) {
                sb.append("المعطيات:\n")
                cleanedGivens.forEach { sb.append("$it\n") }
                sb.append("\n")
            }
            if (cleanedLaws.isNotEmpty()) {
                sb.append("القانون:\n")
                cleanedLaws.forEach { sb.append("$it\n") }
                sb.append("\n")
            }
            if (cleanedSubst.isNotEmpty()) {
                sb.append("التعويض:\n")
                cleanedSubst.forEach { sb.append("$it\n") }
                sb.append("\n")
            }
            if (cleanedCalcs.isNotEmpty()) {
                sb.append("الحساب:\n")
                cleanedCalcs.forEach { sb.append("$it\n") }
                sb.append("\n")
            }
            if (item.finalAnswer.isNotBlank()) {
                sb.append("الإجابة:\n${MathFormatter.cleanItem(item.finalAnswer)}\n")
            }
        }

        if (index < qItems.size - 1) {
            sb.append("\n---\n\n")
        }
    }

    return MathFormatter.cleanMathText(sb.toString()).trim()
}
