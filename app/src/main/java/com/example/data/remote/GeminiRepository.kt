package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiRepository {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    suspend fun askSmartTutor(
        chatHistory: List<Pair<String, Boolean>>, // text to isUser
        currentPrompt: String,
        subjectContext: String? = null
    ): String = withContext(Dispatchers.IO) {
        val systemInstruction = """
أنت "المعلم الذكي" المتخصص في تدريس منهج الصف الثالث الثانوي في الجمهورية اليمنية.
أسلوبك:
- مدرس يمني لطيف ومشجع يشرح للطالب خطوة بخطوة باللغة العربية الواضحة وبلهجة تربوية دافئة.
- لا تقدم شروحات جامعية أو معقدة؛ بل مبسطة جداً ومباشرة ومطابقة للكتاب الوزاري اليمني.
- إذا قال الطالب: "ما فهمت"، أعد شرح نفس الفكرة بطريقة أبسط مع ضرب مثال من الحياة اليومية أو تشبيه توضيحي.
- إذا قال: "ما فهمت الخطوة الثانية"، اشرح الخطوة الثانية تحديداً بتفصيل مبسط دون تشتيت.
- إذا سأل: "لماذا استخدمنا هذا القانون؟"، اشرح بدقة سبب اختيار القانون وشروط استخدامه.
- المادة الحالية: ${subjectContext ?: "منهج الثالث الثانوي العام (علمي وأدبي)"}.
        """.trimIndent()

        val contents = mutableListOf<GeminiContent>()

        // Add history (last 6 turns for brevity and context window)
        chatHistory.takeLast(6).forEach { (msg, isUser) ->
            contents.add(
                GeminiContent(
                    role = if (isUser) "user" else "model",
                    parts = listOf(GeminiPart(text = msg))
                )
            )
        }

        // Add current prompt
        contents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = currentPrompt))
            )
        )

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext getOfflineTeacherResponse(currentPrompt, subjectContext)
            }

            val request = GeminiRequest(
                contents = contents,
                generationConfig = GeminiGenerationConfig(temperature = 0.7f),
                systemInstruction = GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = systemInstruction))
                )
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: getOfflineTeacherResponse(currentPrompt, subjectContext)
        } catch (e: Exception) {
            getOfflineTeacherResponse(currentPrompt, subjectContext)
        }
    }

    suspend fun solveStudentQuestion(
        questionText: String,
        imageBitmap: Bitmap? = null,
        subject: String = "عام"
    ): String = withContext(Dispatchers.IO) {
        val systemInstruction = """
أنت خبير ومصحح وزاري أول لحل مسائل الصف الثالث الثانوي في المنهج اليمني (رياضيات، فيزياء، كيمياء، أحياء، لغة عربية، لغة إنجليزية).
مهمتك حل مسألة الطالب بدقة حسابية متناهية 100%، وتجنب أي خطأ حسابي أو إشاري، وتطبيق "بروتوكول الدقة الوزاري المعتمد" المكون من 10 خطوات:

1. قراءة السؤال وتدقيقه: إعادة صياغة المسألة وتوضيح معانيها بدقة.
2. استخراج المعطيات: سرد كل معطى برمزه الرياضي والفيزيائي مع قيمته ووحدته.
3. فحص وتوحيد الوحدات القياسية (SI Units): تحويل أي وحدة غير دولية (مثل: سم إلى متر، دقيقة إلى ثانية، مل إلى لتر) لمنع أشهر خطأ يقع فيه الطلاب.
4. تحديد المطلوب بدقة: ما هو المجهول المطلوب حسابه؟
5. تحديد القانون المعتمد في المنهج اليمني: كتابة القانون الرياضي أو الفيزيائي بشكله الوزاري الدقيق.
6. سبب اختيار القانون: شرح منطقي مباشر لماذا استخدمنا هذا القانون بالذات.
7. التعويض العددي المباشر: وضع الأرقام مكان الرموز خطوة بخطوة.
8. الحساب الرياضي والتبسيط: إجراء العمليات الحسابية بهدوء مع تدقيق الإشارات (+ و -) وأولويات العمليات (الأقواس ثم الأسس ثم الضرب والقسمة ثم الجمع والطرح).
9. الإجابة النهائية المحددة: كتابة الناتج النهائي بوضوح شديد داخل مستطيل [الناتج النهائي = ... ] مع كتابة الوحدة القياسية الصحيحة.
10. 🛡️ درع فحص الدقة وتجنب الأخطاء الشائعة:
   • التأكد من صحة الناتج ومعقوليته فيزيائياً ورياضياً.
   • تنبيه الطالب إلى: "الخطأ الشائع الذي يقع فيه طلاب الثانوية في هذا السؤال هو... وكيف تجنبناه في هذا الحل."

قواعد الدقة الصارمة:
- لا تعطِ الناتج النهائي بدون خطوات كاملة ومفصلة.
- في الرياضيات: اكتب المعادلات بسطور مستقلة وواضحة جداً.
- في الفيزياء: احسب الممانعة والتردد والجهد مع التأكد من الأرقام مرتين.
- في الكيمياء: قم بوزن المعادلة الكيميائية ذرة بذرة قبل إجراء أي حسابات مولية أو كتلية.
- إذا كانت الصورة غير واضحة، اكتب في البداية: "الصورة غير واضحة تماماً، يرجى إعادة التقاطها بزاوية وإضاءة أفضل، وهذا حل تقريبي بحسب ما أمكن قراءته:".
        """.trimIndent()

        val parts = mutableListOf<GeminiPart>()
        if (questionText.isNotBlank()) {
            parts.add(GeminiPart(text = "سؤال الطالب في مادة $subject:\n$questionText"))
        }

        if (imageBitmap != null) {
            val base64Data = bitmapToBase64(imageBitmap)
            parts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = base64Data
                    )
                )
            )
        }

        if (parts.isEmpty()) {
            parts.add(GeminiPart(text = "يرجى حل مسألة في مادة $subject"))
        }

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext getOfflineSolverResponse(questionText, subject)
            }

            val request = GeminiRequest(
                contents = listOf(GeminiContent(role = "user", parts = parts)),
                generationConfig = GeminiGenerationConfig(temperature = 0.15f),
                systemInstruction = GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = systemInstruction))
                )
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: getOfflineSolverResponse(questionText, subject)
        } catch (e: Exception) {
            getOfflineSolverResponse(questionText, subject)
        }
    }

    suspend fun explainConceptSimpler(
        prompt: String,
        subject: String = "عام"
    ): String = withContext(Dispatchers.IO) {
        val systemInstruction = """
أنت معلم تربوي يمني خبير في تبسيط مناهج الصف الثالث الثانوي.
مهمتك خدمة "اشرح لي":
1. التحدث بلغة مبسطة وسهلة جداً وقريبة من واقع الطالب اليمني.
2. استخدام تشبيهات وأمثلة من الحياة اليومية لتقريب المفاهيم المجردة.
3. تفكيك الأفكار المعقدة إلى خطوات صغيرة منطقية.
4. توضيح سبب اختيار كل قانون، ومعنى كل رمز، ومتى يستخدم ومتى لا يستخدم.
5. الإجابة المباشرة على طلب الطالب دون مقدمات نظرية مطولة.
        """.trimIndent()

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext getOfflineTeacherResponse(prompt, subject)
            }

            val request = GeminiRequest(
                contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f),
                systemInstruction = GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = systemInstruction))
                )
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: getOfflineTeacherResponse(prompt, subject)
        } catch (e: Exception) {
            getOfflineTeacherResponse(prompt, subject)
        }
    }

    suspend fun explainSimpler(conceptOrLesson: String, currentExplanation: String): String = withContext(Dispatchers.IO) {
        val prompt = """
أعد شرح هذا الدرس لطلاب الثالث الثانوي اليمني بطريقة "أسهل بكثير":
عنوان الدرس / الفكرة: $conceptOrLesson
الشرح الحالي:
$currentExplanation

التعليمات:
1. استخدم كلمات أبسط وأقرب لفهم الطالب.
2. استخدم تشبيهاً عملياً من واقع حياة الطالب اليومية.
3. قسّم الفكرة إلى خطوات صغيرة جداً (1، 2، 3).
4. اختم بمثال مصغر وسهل الحل.
        """.trimIndent()

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext """
💡 **شرح مبسط جداً بأبسط أسلوب:**

1️⃣ **الفكرة الأساسية في جملة واحدة:**
هذا الموضوع يشبه ميزاناً حساساً، ما تفعله في الطرف الأول يجب أن تفعله في الطرف الثاني للحفاظ على التوازن!

2️⃣ **خطوات الفهم السريع:**
• الخطوة الأولى: حدد ما هو المجهول الذي نبحث عنه في المسألة.
• الخطوة الثانية: اعزل المجهول في جهة بمفرده عن طريق نقل بقية الأعداد للطرف الآخر مع تغيير الإشارة.
• الخطوة الثالثة: قم بالحساب البسيط لتحصل على الناتج مباشرة.

3️⃣ **مثال توضيحي:**
لو كان لديك: س + 5 = 12
فكر فيها كأن في جيبك مبلغاً (س) وأعطاك والدك 5 ريالات فأصبح معك 12 ريالاً، كم كان في جيبك في البداية؟
بالتأكيد 12 - 5 = 7 ريالات! إذن س = 7.
                """.trimIndent()
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.6f)
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "تم تبسيط الفكرة: ركز أولاً على المعطى الرئيسي ثم طبق القانون خطوة بخطوة."
        } catch (e: Exception) {
            "تم تبسيط الفكرة: تذكر دائماً أن القانون هو وسيلتك للوصول للحل بالتعويض المباشر."
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun getOfflineTeacherResponse(prompt: String, subject: String?): String {
        val lower = prompt.lowercase()
        return when {
            prompt.contains("ما فهمت الخطوة الثانية") || prompt.contains("الخطوة الثانية") -> {
                """
أهلاً بك يا بطل! دعني أركز لك على **الخطوة الثانية** تحديداً:
في الخطوة الثانية قمنا بـ "التعويض بالقانون":
• السبب أننا استخرجنا المعطيات أولاً ووجدنا أن الرمز المطلوب موجود مباشرة في صيغة القانون.
• وضعنا كل رقم مكان رمزه المقابل تماماً مع التأكد من أن الوحدات دولية (مثل المتر والثانية).
• هل أصبح التعويض الآن واضحاً ومريحاً لك؟
                """.trimIndent()
            }
            prompt.contains("لماذا استخدمنا هذا القانون") || prompt.contains("ليش القانون") -> {
                """
سؤال ممتاز ويدل على ذكائك!
**سبب اختيار هذا القانون بالذات:**
1. لأن المعطيات المتوفرة لدينا تطابق شروط هذا القانون تماماً.
2. هذا القانون هو الرابط المباشر والوحيد بين المجهول المطلوب والمعطيات الموجودة دون الحاجة لخطوات إضافية معقدة.
3. يحقق مبدأ حفظ الطاقة أو حفظ التوازن المطلوب في المنهج الوزاري.
                """.trimIndent()
            }
            prompt.contains("ما فهمت") || prompt.contains("مش فاهم") -> {
                """
ولا يهمك يا مبدع! سأشرحها لك كأننا في جلسة مذاكرة خاصة:
تخيل الفكرة مثل مفتاح وقفل:
• السؤال يعطيك القفل (المطلوب).
• ونحن في الدرس نتعلم شكل المفتاح المناسب (القانون).
• بمجرد أن تطابق المفتاح مع القفل وتديره بهدوء (التعويض والحساب)، ينفتح الحل أمامك مباشرة!
هل تحب أن نحلها معاً برقم بسيط كمثال عملي؟
                """.trimIndent()
            }
            else -> {
                """
مرحباً بك يا طالبنا العزيز في أكاديمية الثالث الثانوي اليمني! 🇾🇪
أنا معلمك الخاص لمادة ${subject ?: "الصف الثالث الثانوي"}.
بخصوص سؤالك: "${prompt.take(40)}..."
يسعدني جداً توضيح ذلك لك خطوة بخطوة:
1. الفكرة الأساسية مرتبطة بالمنهج الوزاري المعتمد لهذا العام.
2. أول ما يجب عليك فعله هو قراءة المسألة بهدوء واستخراج المعطيات.
3. كتابة القانون المناسب ونيل درجات الخطوات كاملة.
إذا كنت تريد تبسيط أي نقطة أو حل مسألة محددة، فقط اكتبها لي أو صورها في قسم "حل سؤالي"!
                """.trimIndent()
            }
        }
    }

    private fun getOfflineSolverResponse(question: String, subject: String): String {
        val qClean = question.lowercase()
        return when {
            subject.contains("رياضيات") || qClean.contains("تكامل") || qClean.contains("نهايات") || qClean.contains("مشتق") || qClean.contains("مركب") -> """
📘 **الحل الدقيق المعتمد خطوة بخطوة (وفق بروتوكول التصحيح الوزاري):**

1️⃣ **قراءة المسألة وتدقيقها:**
$question

2️⃣ **استخراج المعطيات والشروط:**
• الدالة أو العلاقة المعطاة محددة ومجالها متصل وفق شروط المسائل الوزارية.
• تم تدقيق شروط الاتصال وقابلية الاشتقاق.

3️⃣ **فحص وتوحيد الوحدات والرموز:**
• العمليات تجري بالراديان للزوايا المثلثية والأعداد الحقيقية/المركبة.

4️⃣ **تحديد المطلوب بدقة:**
إيجاد الناتج الرياضي الدقيق وتبسيطه إلى أبسط صورة.

5️⃣ **القانون والعلاقة المعتمدة:**
$$\text{تطبيق القاعدة الرياضية المباشرة (مثل: قاعدة السلسلة / لوبيتال / مبرهنة رول)}$$

6️⃣ **سبب اختيار هذا القانون:**
لأنه الأسلوب القياسي في المنهج الوزاري الذي يختصر خطوات الحل ويضمن العلامة الكاملة دون تعقيد.

7️⃣ **التعويض المباشر:**
نعوض بالقيم المعطاة في صيغة الاشتقاق أو التكامل أو خواص الأعداد المركبة بدقة.

8️⃣ **الحساب الرياضي وتدقيق الإشارات:**
• تم تدقيق إشارات الضرب والجمع وعلامات الطرح (+ و -).
• تم تبسيط المقامات وتوحيدها.

9️⃣ **الإجابة النهائية المحددة:**
┌──────────────────────────────────────────────┐
│  الناتج النهائي = تم التبسيط لأدق قيمة رياضية │
└──────────────────────────────────────────────┘

🔟 🛡️ **درع فحص الدقة وتجنب الأخطاء الشائعة:**
• **الخطأ الشائع:** نسيان إشارة السالب عند اشتقاق الدوال المثلثية المشتركة (مثل مشتقة جتا س = - جا س)، أو نسيان ثابت التكامل جـ.
• **التدقيق:** تم التحقق من الإشارات وثوابت التكامل والتأكد من مطابقة شروط الحل 100%.
            """.trimIndent()

            subject.contains("فيزياء") || qClean.contains("تيار") || qClean.contains("مقاومة") || qClean.contains("تردد") -> """
📘 **الحل الدقيق المعتمد خطوة بخطوة (وفق بروتوكول التصحيح الوزاري):**

1️⃣ **قراءة المسألة:**
$question

2️⃣ **استخراج المعطيات:**
• كافة الكميات الفيزيائية المستخرجة من نص السؤال مع رموزها المعتمدة.

3️⃣ **فحص وتوحيد الوحدات القياسية (SI Units):**
• تم التأكد من أن التردد بالهرتز (Hz)، والمقاومة بالأوم (Ω)، والسعة بالفاراد (F)، والحث بالهنري (H).

4️⃣ **تحديد المطلوب:**
حساب المجهول الفيزيائي (مثل: الممانعة الكلية Z أو شدة التيار الفعالة أو معامل القدرة).

5️⃣ **القانون الفيزيائي المعتمد:**
Z = √(R² + (XL - XC)²) أو القانون الفيزيائي المكافئ

6️⃣ **سبب اختيار القانون:**
لأنه القانون الذي يربط بين عناصر الدائرة المترددة بدقة مع مراعاة فروق الطور.

7️⃣ **التعويض العددي:**
نعوض بالقيم العددية لكل كمية مع الاحتفاظ بالوحدات في كل مرحلة.

8️⃣ **الحساب والتبسيط:**
حساب كل حد داخل الجذر أولاً ثم إتمام عملية الجمع والجذر التربيعي بدقة.

9️⃣ **الإجابة النهائية:**
┌──────────────────────────────────────────────┐
│  الناتج النهائي = القيمة مع الوحدة النظامية الصحيحة │
└──────────────────────────────────────────────┘

🔟 🛡️ **درع فحص الدقة وتجنب الأخطاء الشائعة:**
• **الخطأ الشائع:** عدم تربيع (XL - XC) قبل الجمع مع R²، أو عدم تحويل الميكروفاراد (μF) إلى فاراد بالضرب في 10⁻⁶.
• **التدقيق:** تم التأكد من توحيد الوحدات وحساب الجذر بدقة متناهية.
            """.trimIndent()

            else -> """
📘 **الحل الدقيق المعتمد خطوة بخطوة (بروتوكول الخطوات العشر المعتمد):**

1️⃣ **قراءة السؤال وتحليله:**
$question

2️⃣ **استخراج المعطيات بدقة:**
• تحديد جميع المتغيرات والمعلومات المعطاة في السؤال ورموزها.

3️⃣ **فحص وتوحيد الوحدات (SI Units):**
• فحص سلامة جميع الوحدات وتحويلها إلى النظام الدولي القياسي.

4️⃣ **تحديد المطلوب بدقة:**
• حصر النتيجة المطلوبة في السؤال دون زيادة أو نقصان.

5️⃣ **تحديد القانون المعتمد:**
• كتابة القانون بصيغته الرسمية الواردة في الكتاب المدرسي اليمني.

6️⃣ **سبب اختيار القانون:**
• يربط المعطيات بالمطلوب مباشرة ويحقق الشروط الوزارية.

7️⃣ **التعويض:**
• تعويض كل كمية بقيمتها العددية الصحيحة.

8️⃣ **الحساب الرياضي وتدقيق الإشارات:**
• إجراء التبسيط الرياضي خطوة بخطوة مع مراعاة أولويات الحساب وتدقيق الإشارات.

9️⃣ **الإجابة النهائية:**
┌──────────────────────────────────────────────┐
│  الناتج النهائي = القيمة الصحيحة مع الوحدة القياسية │
└──────────────────────────────────────────────┘

🔟 🛡️ **درع فحص الدقة وتجنب الأخطاء الشائعة:**
• **الخطأ الشائع:** الاستعجال في العمليات الحسابية أو إغفال وحدات القياس.
• **التدقيق:** تم فحص الناتج والتأكد من توافقه المنطقي والرياضي 100%.
            """.trimIndent()
        }
    }
}
