package com.example.data.remote

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import com.example.data.solver.EducationalSolution
import com.example.data.solver.EducationalSolutionParser
import com.example.data.solver.MathFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiRepository {

    private val apiKey = "" // Uses AI Studio server proxy or direct endpoints
    private val models = listOf(
        "gemini-2.5-flash",
        "gemini-1.5-flash",
        "gemini-1.5-pro"
    )

    suspend fun solveStudentQuestionStructured(
        questionText: String,
        imageBitmap: Bitmap? = null,
        attachedFile: AttachedFile? = null,
        subject: String = "عام",
        image: Bitmap? = null
    ): EducationalSolution = withContext(Dispatchers.IO) {
        val actualImage = imageBitmap ?: image
        val systemInstruction = """
أنت المعلم والمصحح الوزاري الأول لطلاب الصف الثالث الثانوي في المنهج اليمني (منصة رقيب للتعليم الثانوي).
مهمتك: قراءة وحل المسائل وأوراق ونماذج الاختبارات المرفقة في الصورة أو النص بأعلى درجات الدقة.

قواعد التعامل مع أوراق ونماذج الاختبارات (حتى 50 سؤالاً):
1. إذا كانت الصورة تحتوي على نموذج اختبار كامل (صح أو خطأ أو اختيار من متعدد مكون من 10 أو 20 أو 50 سؤالاً):
   - اقرأ كل سؤال بالترتيب الدقيق من رقم 1 إلى آخر سؤال في الورقة (مثال: حتى السؤال 50).
   - لكل سؤال، أنشئ عنصراً مستقلاً داخل مصفوفة items.
   - إذا كان السؤال اختيار من متعدد (MULTIPLE_CHOICE): حدد رقم الاختيار الصحيح بدقة في selected_option_label بصيغة "الاختيار ①" أو "الاختيار ②" أو "الاختيار ③" أو "الاختيار ④" مع نص الاختيار.
   - إذا كان السؤال صواب أو خطأ (TRUE_FALSE): ضع is_true = true (إذا كانت صواب) أو is_true = false (إذا كانت خطأ)، وإذا كانت خطأ اكتب التصحيح الوزاري الدقيق والمختصر في correction.
   - اكتب خطوات توضيحية أو تعليلاً مختصراً في calculation_steps.

2. إذا كانت الصورة أو النص تحتوي على مسألة واحدة تفصيلية:
   - حل المسألة بطريقة السبورة الوزارية خطوة بخطوة: المعطيات (givens)، القوانين (laws)، التعويض (substitution_steps)، والناتج النهائي (final_answer) مع وحدات القياس.

3. معايير هامة:
   - اقرأ المسألة حتى لو كانت ملتقطة من كتاب مدرسي أو دفتر أو شاشة أو ورقة اختبار مطبوعة.
   - ضع دائماً is_image_clear = true. لا تضع is_image_clear = false إلا إذا كانت الصورة فارغة تماماً ولا تحتوي على أي نص أو أرقام.
   - لا تستخدم أكواد LaTeX الخام (مثل \frac أو \sqrt)، بل اكتب الرموز مباشرة: √ للجدور، ° للزوايا، × للضرب، ÷ للقسمة، والأسس المرفوعة.

التزم بالصيغة الهيكلية JSON التالية:
{
  "is_image_clear": true,
  "unclear_reason": null,
  "subject": "$subject",
  "final_answer": "ملخص عام لنتيجة الحل",
  "items": [
    {
      "question_number": 1,
      "question_title": "السؤال 1",
      "question_text": "نص السؤال الأول",
      "question_type": "MULTIPLE_CHOICE",
      "selected_option_label": "الاختيار ②",
      "selected_option_text": "نص الاختيار",
      "is_true": null,
      "correction": null,
      "final_answer": "نص الإجابة",
      "givens": [],
      "laws": [],
      "substitution_steps": [],
      "calculation_steps": ["توضيح خطوة الحل"]
    },
    {
      "question_number": 2,
      "question_title": "السؤال 2",
      "question_text": "نص عبارة الصواب والخطأ",
      "question_type": "TRUE_FALSE",
      "selected_option_label": "",
      "selected_option_text": null,
      "is_true": false,
      "correction": "التصحيح العلمي الصحيح للعبارة",
      "final_answer": "خطأ",
      "givens": [],
      "laws": [],
      "substitution_steps": [],
      "calculation_steps": ["التعليل"]
    }
  ]
}
        """.trimIndent()

        val parts = mutableListOf<GeminiPart>()
        if (questionText.isNotBlank()) {
            parts.add(GeminiPart(text = "سؤال الطالب أو نموذج الاختبار في مادة $subject:\n$questionText\nيرجى حله بالكامل (حتى 50 سؤالاً إن وجد) وفق المنهج اليمني بصيغة JSON."))
        }

        if (actualImage != null) {
            val scaledBmp = scaleBitmap(actualImage, 1600)
            val base64Data = bitmapToBase64(scaledBmp)
            parts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = base64Data
                    )
                )
            )
            if (questionText.isBlank()) {
                parts.add(GeminiPart(text = "يرجى قراءة وحل جميع الأسئلة الموجودة في ورقة أو نموذج هذا الاختبار في مادة $subject بالكامل (حل جميع الأسئلة حتى 50 سؤال اختيار أو صح/خطأ) بالخطوات الوزارية المعتمدة وإرجاع النتيجة بصيغة JSON."))
            }
        }

        if (attachedFile != null) {
            parts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = attachedFile.mimeType,
                        data = attachedFile.base64Data
                    )
                )
            )
            if (questionText.isBlank() && actualImage == null) {
                parts.add(GeminiPart(text = "يرجى قراءة وحل جميع الأسئلة ونموذج الاختبار المرفق في هذا الملف في مادة $subject بصيغة JSON."))
            }
        }

        if (parts.isEmpty()) {
            return@withContext EducationalSolution(
                isImageClear = false,
                unclearReason = "يرجى كتابة نص السؤال أو التقاط صورة لورقة الاختبار أو رفع الملف أولاً.",
                subject = subject,
                confidence = "unclear"
            )
        }

        try {
            val rawJson = executeGeminiCall(parts, systemInstruction)
            EducationalSolutionParser.parse(rawJson, subject)
        } catch (e: Exception) {
            EducationalSolution(
                isImageClear = false,
                unclearReason = "تعذر الاتصال بخدمة الذكاء الاصطناعي حالياً (${e.message}). يرجى التأكد من اتصال الإنترنت وإعادة المحاولة.",
                subject = subject,
                questionUnderstanding = if (questionText.isNotBlank()) questionText else "ورقة الاختبار المرفقة",
                confidence = "error",
                finalAnswer = "يرجى إعادة المحاولة للتواصل مع المصحح الآلي."
            )
        }
    }

    suspend fun explainConcept(prompt: String, subject: String, level: String = "مفصل"): String = withContext(Dispatchers.IO) {
        val sysPrompt = "أنت معلم ثانوي متميز لطلاب المنهج اليمني في مادة $subject. اشرح بأسلوب $level مع أمثلة واضحة وشرح السبورة التوضيحي."
        try {
            executeGeminiCall(listOf(GeminiPart(text = prompt)), sysPrompt)
        } catch (e: Exception) {
            "شرح توضيحي لموضوع: $prompt في مادة $subject. يرجى التركيز على المفاهيم والقوانين الوزارية وتطبيقها في حل المسائل."
        }
    }

    suspend fun generateSimplerExplanation(lessonTitle: String, coreIdea: String, subject: String): String = withContext(Dispatchers.IO) {
        val prompt = "أنت معلم ثانوي متميز لطلاب اليمن. اشرح درس '$lessonTitle' في مادة $subject بطريقة ممتعة جداً، مع تشبيه بسيط من الحياة اليومية، في 4 إلى 6 أسطر فقط."
        try {
            executeGeminiCall(listOf(GeminiPart(text = prompt)), "اشرح بتبسيط رائع وباللغة العربية الفصحى السلسة.")
        } catch (e: Exception) {
            "فكرة الدرس الأساسية: $coreIdea. تذكر دائماً فهم القوانين والتدريب العملي على حل التمارين الوزارية!"
        }
    }

    suspend fun askSmartTutor(question: String, subject: String): String = withContext(Dispatchers.IO) {
        val prompt = "سؤال طالب الصف الثالث الثانوي في مادة $subject: $question. أجب كمعلم ناصح وخبير بالمنهج اليمني."
        try {
            executeGeminiCall(listOf(GeminiPart(text = prompt)), "أنت المعلم الذكي لمنصة رقيب للتعليم الثانوي بالجمهورية اليمنية.")
        } catch (e: Exception) {
            "أهلاً بك يا بطل! يسعدني دائماً مساعدتك في مادة $subject. يرجى التأكد من اتصال الإنترنت وإعادة إرسال سؤالك."
        }
    }

    suspend fun askSmartTeacherFull(
        questionText: String,
        imageBitmap: Bitmap? = null,
        subject: String = "عام",
        modalities: Set<String> = setOf("TEXT", "VIDEO", "IMAGE", "PDF")
    ): SmartTeacherResponse = withContext(Dispatchers.IO) {
        val systemInstruction = """
أنت "الأستاذ الذكي" (منصة رقيب للتعليم الثانوي - الجمهورية اليمنية).
مهمتك: مساعدة وشرح وحل أي سؤال أو مسألة أو استفسار يطرحه طالب الصف الثالث الثانوي (علمي وأدبي) بأعلى جودة تعليمية وأسلوب أستاذ متميز وناصح.

التعليمات الوزارية والتربوية:
1. اشرح المفهوم أو حل المسألة بخطوات السبورة المعتمدة خطوة بخطوة باللغة العربية الفصحى السلسة والممتعة.
2. اقترح للطالب درساً وفيديو تعليمياً مخصصاً للموضوع مع عنوان دقيق وعبارة بحث للوصول لأفضل شروحات الفيديو على يوتيوب.
3. ارسم أو صف لوحة توضيحية ومخططاً بصرياً (Diagram / Board Concept) يساعد الطالب على استيعاب الفكرة وتذكرها.
4. لخّص أهم القوانين والملاحظات الامتحانية في شكل ملزمة ومذكرة PDF جاهزة.

أرجع النتيجة بصيغة JSON حصراً بهذا الهيكل:
{
  "answer_text": "الشرح الكامل والوافي والمفصل للموضوع أو المسألة مع الخطوات...",
  "key_points": ["نقطة تركيز 1", "نقطة تركيز 2"],
  "formulas": ["القانون 1", "القانون 2"],
  "video_title": "عنوان درس الفيديو التعليمي المقترح",
  "video_query": "عبارة البحث عن الفيديو في يوتيوب",
  "video_description": "ما سيتعلمه الطالب من هذا المقطع المرئي",
  "diagram_title": "عنوان المخطط أو اللوحة التعليمية",
  "diagram_summary": "الوصف البصري أو مخطط المفاهيم للوحة السبورة",
  "pdf_title": "عنوان الملزمة المرفقة"
}
        """.trimIndent()

        val parts = mutableListOf<GeminiPart>()
        if (questionText.isNotBlank()) {
            parts.add(GeminiPart(text = "سؤال واستفسار الطالب في مادة $subject:\n$questionText\nيرجى تقديم الشرح والحل بصيغة JSON متضمناً النص المقترح والفيديو والرسم التوضيحي وملخص PDF."))
        }

        if (imageBitmap != null) {
            val scaledBmp = scaleBitmap(imageBitmap, 1600)
            val base64Data = bitmapToBase64(scaledBmp)
            parts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = base64Data
                    )
                )
            )
            if (questionText.isBlank()) {
                parts.add(GeminiPart(text = "يرجى قراءة وحل المسألة أو السؤال المرفق في هذه الصورة في مادة $subject بالكامل بصيغة JSON متضمناً الشرح، الفيديو، المخطط، وملف PDF."))
            }
        }

        if (parts.isEmpty()) {
            return@withContext SmartTeacherResponse(
                answerText = "أهلاً بك يا بطل! يرجى كتابة سؤالك أو إرفاق صورة المسألة وسأقوم بشرحها لك بالكامل.",
                subject = subject
            )
        }

        try {
            val raw = executeGeminiCall(parts, systemInstruction)
            parseSmartTeacherResponse(raw, subject, questionText)
        } catch (e: Exception) {
            val fallbackAnswer = if (questionText.isNotBlank()) {
                "أهلاً بك يا بطل في مادة $subject! موضوع استفسارك: $questionText.\n\nتذكر دائماً أن فهم القوانين والتدريب العملي هو سر التفوق الوزاري. تعذر الاتصال اللحظي بمحرك الذكاء الاصطناعي (${e.message})، يرجى التأكد من اتصال الإنترنت وإعادة إرسال سؤالك وسأجيبك فوراً!"
            } else {
                "أهلاً بك يا بطل! تعذر تحليل الصورة المرفقة حالياً بسبب ضعف الاتصال، يرجى إعادة المحاولة."
            }
            SmartTeacherResponse(
                answerText = fallbackAnswer,
                subject = subject,
                videoTitle = "شرح درس مادة $subject - ثالث ثانوي المنهج اليمني",
                videoQuery = "شرح مادة $subject ثالث ثانوي اليمن $questionText",
                videoDescription = "مراجعة شاملة للدرس والأسئلة الوزارية",
                diagramTitle = "لوحة ومخطط المفاهيم",
                diagramSummary = "مخطط السبورة الوزارية وملخص القوانين الأساسية",
                pdfTitle = "ملخص وشرح $subject"
            )
        }
    }

    private fun parseSmartTeacherResponse(rawJson: String, subject: String, originalPrompt: String): SmartTeacherResponse {
        try {
            val cleaned = rawJson.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()
            val obj = JSONObject(cleaned)
            val answerText = obj.optString("answer_text", "")
            val keyPoints = mutableListOf<String>()
            val keyPointsArr = obj.optJSONArray("key_points")
            if (keyPointsArr != null) {
                for (i in 0 until keyPointsArr.length()) {
                    keyPoints.add(keyPointsArr.optString(i))
                }
            }
            val formulas = mutableListOf<String>()
            val formulasArr = obj.optJSONArray("formulas")
            if (formulasArr != null) {
                for (i in 0 until formulasArr.length()) {
                    formulas.add(formulasArr.optString(i))
                }
            }
            val videoTitle = obj.optString("video_title", "درس فيديو: $subject").ifEmpty { "درس فيديو: $subject" }
            val videoQuery = obj.optString("video_query", "شرح منهج اليمن $subject $originalPrompt").ifEmpty { "شرح منهج اليمن $subject $originalPrompt" }
            val videoDesc = obj.optString("video_description", "فيديو تعليمي تفصيلي لشرح وحل أسئلة الموضوع").ifEmpty { "فيديو تعليمي تفصيلي لشرح وحل أسئلة الموضوع" }
            val diagramTitle = obj.optString("diagram_title", "لوحة ومخطط المفاهيم").ifEmpty { "لوحة ومخطط المفاهيم" }
            val diagramSummary = obj.optString("diagram_summary", "مخطط سبورة يوضح خطوات وتفرعات الدرس").ifEmpty { "مخطط سبورة يوضح خطوات وتفرعات الدرس" }
            val pdfTitle = obj.optString("pdf_title", "ملزمة شرح الأستاذ الذكي - $subject").ifEmpty { "ملزمة شرح الأستاذ الذكي - $subject" }

            return SmartTeacherResponse(
                answerText = if (answerText.isNotBlank()) answerText else cleaned,
                subject = subject,
                keyPoints = keyPoints,
                formulas = formulas,
                videoTitle = videoTitle,
                videoQuery = videoQuery,
                videoDescription = videoDesc,
                diagramTitle = diagramTitle,
                diagramSummary = diagramSummary,
                pdfTitle = pdfTitle
            )
        } catch (e: Exception) {
            return SmartTeacherResponse(
                answerText = rawJson,
                subject = subject,
                videoTitle = "فيديو تعليمي في $subject",
                videoQuery = "شرح منهج اليمن $subject $originalPrompt",
                videoDescription = "شرح مرئي شامل للمنهج اليمني",
                diagramTitle = "لوحة المفاهيم",
                diagramSummary = "مخطط توضيحي للدرس",
                pdfTitle = "ملخص ومذكرة $subject"
            )
        }
    }

    private fun executeGeminiCall(parts: List<GeminiPart>, systemInstruction: String): String {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 60000

        val requestObj = JSONObject()
        val contentsArr = JSONArray()
        val contentObj = JSONObject()
        contentObj.put("role", "user")

        val partsArr = JSONArray()
        for (p in parts) {
            val partObj = JSONObject()
            if (p.text != null) {
                partObj.put("text", p.text)
            } else if (p.inlineData != null) {
                val inlineObj = JSONObject()
                inlineObj.put("mime_type", p.inlineData.mimeType)
                inlineObj.put("data", p.inlineData.data)
                partObj.put("inline_data", inlineObj)
            }
            partsArr.put(partObj)
        }
        contentObj.put("parts", partsArr)
        contentsArr.put(contentObj)
        requestObj.put("contents", contentsArr)

        val sysObj = JSONObject()
        val sysPartsArr = JSONArray()
        sysPartsArr.put(JSONObject().put("text", systemInstruction))
        sysObj.put("parts", sysPartsArr)
        requestObj.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.2)
        genConfig.put("maxOutputTokens", 8192)
        genConfig.put("responseMimeType", "application/json")
        requestObj.put("generationConfig", genConfig)

        OutputStreamWriter(conn.outputStream).use { it.write(requestObj.toString()) }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val respJson = JSONObject(responseText)
            val candidates = respJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val respParts = content?.optJSONArray("parts")
            return respParts?.optJSONObject(0)?.optString("text") ?: "{}"
        } else {
            val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error $responseCode"
            throw Exception("HTTP $responseCode: $errorText")
        }
    }

    private fun ensureSoftwareBitmap(bitmap: Bitmap): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false) ?: bitmap
        } else {
            bitmap
        }
    }

    private fun scaleBitmap(sourceBitmap: Bitmap, maxDimension: Int = 1600): Bitmap {
        val bitmap = ensureSoftwareBitmap(sourceBitmap)
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        val (newWidth, newHeight) = if (width > height) {
            maxDimension to (maxDimension / ratio).toInt()
        } else {
            (maxDimension * ratio).toInt() to maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth.coerceAtLeast(1), newHeight.coerceAtLeast(1), true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val scaled = scaleBitmap(bitmap, 1600)
        val stream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
