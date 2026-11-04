package com.example.data.remote

data class AttachedFile(
    val fileName: String,
    val mimeType: String,
    val base64Data: String,
    val sizeBytes: Long = 0L
)

data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiGenerationConfig(
    val temperature: Float = 0.2f,
    val topP: Float = 0.9f,
    val maxOutputTokens: Int = 8192,
    val responseMimeType: String? = null
)

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
) {
    fun extractText(): String? {
        return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
    }
}

data class SmartTeacherResponse(
    val answerText: String,
    val subject: String = "عام",
    val keyPoints: List<String> = emptyList(),
    val formulas: List<String> = emptyList(),
    val videoTitle: String? = null,
    val videoQuery: String? = null,
    val videoDescription: String? = null,
    val diagramTitle: String? = null,
    val diagramSummary: String? = null,
    val pdfTitle: String? = null
)

/**
 * خيارات خدمة «لم أفهم؟» المعتمدة
 */
enum class ExplainOption(
    val title: String,
    val emoji: String,
    val subtitle: String
) {
    EXPLAIN_SIMPLY("اشرح ببساطة", "💡", "شرح ممتع بالتشبيهات من واقع البيئة والشارع اليمني لتعيش المفهوم وتفهمه فوراً"),
    STEP_BY_STEP("خطوة بخطوة", "👣", "تفكيك المفهوم أو المسألة إلى مراحل متسلسلة مرقمة مع توضيح التعليل والخطوات"),
    WHY_THIS_LAW("لماذا هذا الدرس أو القانون؟", "🎯", "الهدف الواقعي والحياتي والتطبيقي ولماذا وضعه العلماء وكيف نعيشه باليمن"),
    EXPECTED_QUESTIONS("أعطني أسئلة متوقعة", "📝", "أهم الأسئلة الوزارية المتوقعة لشهادة الثانوية العامة اليمنية (أتمتة)"),
    SOLVED_EXERCISES("أعطني تدريبات محلولة", "✏️", "تمارين تطبيقية نموذجية مع الحل التفصيلي خطوة بخطوة بالخطوات الوزارية")
}
