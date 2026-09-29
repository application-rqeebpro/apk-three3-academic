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
data class EducationalSolution(
    @Json(name = "is_image_clear")
    val isImageClear: Boolean = true,
    @Json(name = "unclear_reason")
    val unclearReason: String? = null,
    @Json(name = "subject")
    val subject: String = "عام",
    @Json(name = "question_understanding")
    val questionUnderstanding: String = "",
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
)

fun EducationalSolution.toFormattedEducationalText(): String {
    if (!isImageClear) {
        return """
⚠️ **تنبيه بخصوص وضوح السؤال / الصورة:**
${unclearReason ?: "الجزء الخاص بالسؤال غير واضح في الصورة، أرسل صورة أوضح حتى أحل السؤال بدقة وبشكل صحيح."}
        """.trimIndent()
    }

    val sb = StringBuilder()
    if (questionUnderstanding.isNotBlank()) {
        sb.append("📘 **قراءة وفهم المسألة:**\n$questionUnderstanding\n\n")
    }

    if (givens.isNotEmpty()) {
        sb.append("📋 **المعطيات:**\n")
        givens.forEach { sb.append("• $it\n") }
        sb.append("\n")
    }

    if (required.isNotBlank()) {
        sb.append("🎯 **المطلوب:**\n$required\n\n")
    }

    if (laws.isNotEmpty()) {
        sb.append("📜 **القانون المعتمد في المنهج اليمني:**\n")
        laws.forEach { sb.append("• $it\n") }
        sb.append("\n")
    }

    if (substitutionSteps.isNotEmpty()) {
        sb.append("✍️ **التعويض العددي:**\n")
        substitutionSteps.forEach { sb.append("• $it\n") }
        sb.append("\n")
    }

    if (calculationSteps.isNotEmpty()) {
        sb.append("🔢 **الحساب والتبسيط خطوة بخطوة:**\n")
        calculationSteps.forEach { sb.append("• $it\n") }
        sb.append("\n")
    }

    if (!unitCheck.isNullOrBlank()) {
        sb.append("📏 **فحص وتوحيد الوحدات:**\n$unitCheck\n\n")
    }

    if (finalAnswer.isNotBlank()) {
        sb.append("🏆 **الإجابة النهائية:**\n")
        sb.append("┌──────────────────────────────────────────────┐\n")
        sb.append("│  $finalAnswer  │\n")
        sb.append("└──────────────────────────────────────────────┘\n\n")
    }

    if (!multipleChoiceAnswer.isNullOrBlank()) {
        sb.append("🔘 **الخيار الصحيح في السؤال:** $multipleChoiceAnswer\n\n")
    }

    sb.append("🛡️ **مرحلة التدقيق المستقل (VERIFY_SOLUTION):**\n")
    sb.append("${verification.verificationDetails}\n")
    verification.checksList.forEach { sb.append("  $it\n") }
    if (!verification.commonMistakesAvoided.isNullOrBlank()) {
        sb.append("\n⚠️ **أخطاء شائعة تم تجنبها:** ${verification.commonMistakesAvoided}\n")
    }

    return sb.toString().trim()
}

