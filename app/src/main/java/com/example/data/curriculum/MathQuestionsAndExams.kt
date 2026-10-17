package com.example.data.curriculum

import com.example.data.local.entities.ExamEntity
import com.example.data.local.entities.QuestionEntity

object MathQuestionsAndExams {

    fun getUnitExams(): List<ExamEntity> = listOf(
        ExamEntity(id = 101, subjectId = 1, unitId = 101, title = "اختبار شامل: الوحدة الأولى (الأعداد المركبة)", durationMinutes = 30, totalQuestions = 7, passingScore = 50),
        ExamEntity(id = 102, subjectId = 1, unitId = 102, title = "اختبار شامل: الوحدة الثانية (مبدأ العد ومفكوك حدين)", durationMinutes = 25, totalQuestions = 5, passingScore = 50),
        ExamEntity(id = 103, subjectId = 1, unitId = 103, title = "اختبار شامل: الوحدة الثالثة (الاحتمالات)", durationMinutes = 30, totalQuestions = 6, passingScore = 50),
        ExamEntity(id = 104, subjectId = 1, unitId = 104, title = "اختبار شامل: الوحدة الرابعة (القطوع المخروطية)", durationMinutes = 30, totalQuestions = 6, passingScore = 50),
        ExamEntity(id = 105, subjectId = 1, unitId = 105, title = "اختبار شامل: الوحدة الخامسة (التفاضل)", durationMinutes = 40, totalQuestions = 9, passingScore = 50),
        ExamEntity(id = 106, subjectId = 1, unitId = 106, title = "اختبار شامل: الوحدة السادسة (التكامل)", durationMinutes = 40, totalQuestions = 7, passingScore = 50),
        ExamEntity(id = 107, subjectId = 1, unitId = 107, title = "اختبار شامل: الوحدة السابعة (الهندسة الإقليدية)", durationMinutes = 25, totalQuestions = 5, passingScore = 50)
    )

    fun getUnitQuestions(): List<QuestionEntity> = listOf(
        // ==================== الوحدة 1: الأعداد المركبة ====================
        QuestionEntity(
            id = 10101,
            subjectId = 1,
            unitId = 101,
            lessonId = 1011,
            questionType = "MCQ",
            questionText = "قيمة ت³⁵ تساوي:",
            options = "1||-1||ت||-ت",
            correctAnswer = "-ت",
            explanation = "نقسم 35 على 4: 35 = (4 × 8) + 3، الباقي 3 إذن ت³⁵ = ت³ = -ت.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10102,
            subjectId = 1,
            unitId = 101,
            lessonId = 1012,
            questionType = "MCQ",
            questionText = "إذا كان ع₁ = 2 + 3ت وكان ع₂ = 4 - 5ت، فإن ع₁ + ع₂ يساوي:",
            options = "6 - 2ت||6 + 2ت||-2 + 8ت||6 - 8ت",
            correctAnswer = "6 - 2ت",
            explanation = "نجمع الحقيقي مع الحقيقي: 2 + 4 = 6، والتخيلي مع التخيلي: 3 + (-5) = -2ت. الناتج 6 - 2ت.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10103,
            subjectId = 1,
            unitId = 101,
            lessonId = 1013,
            questionType = "MCQ",
            questionText = "حاصل ضرب العدد المركب ع = 3 + 4ت في مرافقه ع̄ يساوي:",
            options = "7||25||-7||5",
            correctAnswer = "25",
            explanation = "ع × ع̄ = س² + ص² = 3² + 4² = 9 + 16 = 25.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10104,
            subjectId = 1,
            unitId = 101,
            lessonId = 1014,
            questionType = "TRUE_FALSE",
            questionText = "مقياس العدد المركب ع = -4ت يساوي -4.",
            options = "صح||خطأ",
            correctAnswer = "خطأ",
            explanation = "خطأ ❌، المقياس دائماً موجب أو صفر: ر = √(0² + (-4)²) = √16 = 4.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10105,
            subjectId = 1,
            unitId = 101,
            lessonId = 1015,
            questionType = "MCQ",
            questionText = "باستخدام مبرهنة ديموافر، ناتج [2 ، 30°]³ هو:",
            options = "[8 ، 90°]||[6 ، 90°]||[8 ، 30°]||[6 ، 30°]",
            correctAnswer = "[8 ، 90°]",
            explanation = "نرفع المقياس للقوة 3 ونضرب السعة في 3: [2³ ، 3 × 30°] = [8 ، 90°] = 8ت.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 10106,
            subjectId = 1,
            unitId = 101,
            lessonId = 1016,
            questionType = "MCQ",
            questionText = "جذرا المعادلة س² + 16 = 0 في مجموعة الأعداد المركبة هما:",
            options = "± 4ت||± 4||± 16ت||لا يوجد حل",
            correctAnswer = "± 4ت",
            explanation = "س² = -16 ⟹ س = ± √(-16) = ± 4ت.",
            difficulty = "EASY"
        ),

        // ==================== الوحدة 2: مبدأ العد ومفكوك حدين ====================
        QuestionEntity(
            id = 10201,
            subjectId = 1,
            unitId = 102,
            lessonId = 1021,
            questionType = "MCQ",
            questionText = "كم عدداً من منزلتين مختلفتين يمكن تكوينه من الأرقام {3 ، 5 ، 7 ، 9}؟",
            options = "12||16||8||24",
            correctAnswer = "12",
            explanation = "اختيار الآحاد 4 خيارات، واختيار العشرات 3 خيارات: 4 × 3 = 12 عدداً.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10202,
            subjectId = 1,
            unitId = 102,
            lessonId = 1022,
            questionType = "MCQ",
            questionText = "قيمة 5 ل 2 تساوي:",
            options = "20||10||25||120",
            correctAnswer = "20",
            explanation = "5 ل 2 = 5 × 4 = 20.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10203,
            subjectId = 1,
            unitId = 102,
            lessonId = 1023,
            questionType = "MCQ",
            questionText = "قيمة 7 ق 5 تساوي:",
            options = "21||42||35||14",
            correctAnswer = "21",
            explanation = "بقانون التبسيط: 7 ق 5 = 7 ق 2 = (7 × 6) / (2 × 1) = 21.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10204,
            subjectId = 1,
            unitId = 102,
            lessonId = 1024,
            questionType = "MCQ",
            questionText = "عدد حدود مفكوك (س + ص)⁹ يساوي:",
            options = "10||9||8||18",
            correctAnswer = "10",
            explanation = "عدد حدود مفكوك ذات الحدين يساوي ن + 1 = 9 + 1 = 10 حدود.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10205,
            subjectId = 1,
            unitId = 102,
            lessonId = 1025,
            questionType = "TRUE_FALSE",
            questionText = "إذا كان ن ق 2 = 15 فإن ن = 6.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، ن(ن-1)/2 = 15 ⟹ ن(ن-1) = 30 ⟹ ن = 6 لأن 6 × 5 = 30.",
            difficulty = "MEDIUM"
        ),

        // ==================== الوحدة 3: الاحتمالات ====================
        QuestionEntity(
            id = 10301,
            subjectId = 1,
            unitId = 103,
            lessonId = 1031,
            questionType = "MCQ",
            questionText = "إذا كان احتمال وقوع حادثة ل(ح) = 0.7 فإن احتمال متممتها ل(ح̄) يساوي:",
            options = "0.3||0.7||1||0",
            correctAnswer = "0.3",
            explanation = "ل(ح̄) = 1 - ل(ح) = 1 - 0.7 = 0.3.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10302,
            subjectId = 1,
            unitId = 103,
            lessonId = 1032,
            questionType = "TRUE_FALSE",
            questionText = "إذا كان أ و ب حادثتين متنافيتين، فإن ل(أ ∩ ب) = 0.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، الحادثتان المتنافيتان لا يمكن حدوثهما معاً أبداً وتقاطعهما فاي ∅.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10303,
            subjectId = 1,
            unitId = 103,
            lessonId = 1033,
            questionType = "MCQ",
            questionText = "إذا كانت الحادثتان أ و ب مستقلتين، وكان ل(أ) = 0.4 و ل(ب) = 0.5 فإن ل(أ ∩ ب) يساوي:",
            options = "0.20||0.90||0.10||0.50",
            correctAnswer = "0.20",
            explanation = "في الحوادث المستقلة: ل(أ ∩ ب) = ل(أ) × ل(ب) = 0.4 × 0.5 = 0.20.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10304,
            subjectId = 1,
            unitId = 103,
            lessonId = 1034,
            questionType = "MCQ",
            questionText = "قانون الاحتمال الكلي لحادثة أ مجزأة على ب₁ و ب₂ هو:",
            options = "ل(ب₁)ل(أ|ب₁) + ل(ب₂)ل(أ|ب₂)||ل(ب₁) + ل(ب₂)||ل(أ|ب₁) × ل(أ|ب₂)||ل(ب₁ ∩ ب₂)",
            correctAnswer = "ل(ب₁)ل(أ|ب₁) + ل(ب₂)ل(أ|ب₂)",
            explanation = "قانون الاحتمال الكلي يجمع مسارات الاحتمالات المشروطة لكل تجزئة.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 10305,
            subjectId = 1,
            unitId = 103,
            lessonId = 1035,
            questionType = "TRUE_FALSE",
            questionText = "في السحب مع الإعادة، تكون الحوادث مستقلة وفضاء العينة يبقى ثابتاً.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، لأن إرجاع العنصر المسحوب يعيد فضاء العينة لعدده الأصلي دون نقص.",
            difficulty = "EASY"
        ),

        // ==================== الوحدة 4: القطوع المخروطية ====================
        QuestionEntity(
            id = 10401,
            subjectId = 1,
            unitId = 104,
            lessonId = 1041,
            questionType = "MCQ",
            questionText = "القطع المخروطي الذي اختلافه المركزي ي = 1 هو:",
            options = "قطع مكافئ||قطع ناقص||قطع زائد||دائرة",
            correctAnswer = "قطع مكافئ",
            explanation = "القطع المكافئ يتميز بأن ي = 1، بينما الناقص ي < 1، والزائد ي > 1.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10402,
            subjectId = 1,
            unitId = 104,
            lessonId = 1042,
            questionType = "MCQ",
            questionText = "بؤرة القطع المكافئ ص² = 8س هي النقطة:",
            options = "(2 ، 0)||(0 ، 2)||(-2 ، 0)||(4 ، 0)",
            correctAnswer = "(2 ، 0)",
            explanation = "ص² = 4أ س ⟹ 4أ = 8 ⟹ أ = 2. البؤرة تقع على محور السينات: (2 ، 0).",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10403,
            subjectId = 1,
            unitId = 104,
            lessonId = 1043,
            questionType = "TRUE_FALSE",
            questionText = "في القطع الناقص دائماً أ² = ب² + جـ².",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، في القطع الناقص أ هو الأكبر دائماً و أ² = ب² + جـ².",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10404,
            subjectId = 1,
            unitId = 104,
            lessonId = 1044,
            questionType = "MCQ",
            questionText = "معادلتا خطي التقارب للقطع الزائد (س² / 16) - (ص² / 9) = 1 هما:",
            options = "ص = ± (3/4) س||ص = ± (4/3) س||ص = ± (9/16) س||ص = ± س",
            correctAnswer = "ص = ± (3/4) س",
            explanation = "أ² = 16 ⟹ أ = 4 ، ب² = 9 ⟹ ب = 3. خطوط التقارب للقطع الأفقي: ص = ± (ب/أ) س = ± (3/4) س.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 10405,
            subjectId = 1,
            unitId = 104,
            lessonId = 1045,
            questionType = "MCQ",
            questionText = "في المعادلة العامة أ س² + ب س ص + جـ ص² + ... = 0، إذا كان ب² - 4 أ جـ = 0 فإن القطع يكون:",
            options = "قطعاً مكافئاً||قطعاً ناقصاً||قطعاً زائداً||دائرة",
            correctAnswer = "قطعاً مكافئاً",
            explanation = "المميز = 0 يمثل قطعاً مكافئاً.",
            difficulty = "EASY"
        ),

        // ==================== الوحدة 5: التفاضل ====================
        QuestionEntity(
            id = 10501,
            subjectId = 1,
            unitId = 105,
            lessonId = 1051,
            questionType = "TRUE_FALSE",
            questionText = "إذا حققت دالة شروط مبرهنة رول على [أ ، ب] فإن د'(جـ) = 0 لنقطة جـ واحدة على الأقل.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، نتيجة مبرهنة رول هي وجود جـ ∈ (أ ، ب) بحيث يكون المماس أفقياً د'(جـ) = 0.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10502,
            subjectId = 1,
            unitId = 105,
            lessonId = 1052,
            questionType = "MCQ",
            questionText = "التفسير الهندسي لمبرهنة القيمة المتوسطة هو وجود مماس لمنحنى الدالة:",
            options = "يوازي القاطع الواصل بين طرفي المنحنى||يعامد القاطع||أفقي تماماً||يمر بنقطة الأصل",
            correctAnswer = "يوازي القاطع الواصل بين طرفي المنحنى",
            explanation = "ميل المماس د'(جـ) = ميل القاطع [د(ب) - د(أ)] / (ب - أ)، مما يعني توازيهما.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10503,
            subjectId = 1,
            unitId = 105,
            lessonId = 1053,
            questionType = "MCQ",
            questionText = "باستخدام قاعدة لوبيتال، قيمة نهــــا (جا 4س / س) عندما س → 0 تساوي:",
            options = "4||1||0||غير موجودة",
            correctAnswer = "4",
            explanation = "باشتقاق البسط (4 جتا 4س) والمقام (1): 4 جتا 0 = 4 × 1 = 4.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10504,
            subjectId = 1,
            unitId = 105,
            lessonId = 1054,
            questionType = "MCQ",
            questionText = "تكون الدالة د متزايدة تماماً على فترة إذا كانت المشتقة الأولى د'(س):",
            options = "موجبة (> 0)||سالبة (< 0)||تساوي صفراً||غير معرفة",
            correctAnswer = "موجبة (> 0)",
            explanation = "د'(س) > 0 تعني أن ميل المماس موجب والدالة في حالة صعود وتزايد مستمر.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10505,
            subjectId = 1,
            unitId = 105,
            lessonId = 1055,
            questionType = "TRUE_FALSE",
            questionText = "إذا كانت د'(جـ) = 0 وكانت المشتقة الثانية د''(جـ) < 0 فإن للدالة قيمة عظمى محلية عند جـ.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، اختبار المشتقة الثانية: د'' < 0 تدل على تقعر لأسفل وبالتالي قمة عظمى محلية.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10506,
            subjectId = 1,
            unitId = 105,
            lessonId = 1056,
            questionType = "MCQ",
            questionText = "الشرط الأساسي لتكون النقطة (س₀ ، د(س₀)) نقطة انعطاف هو:",
            options = "تغير إشارة المشتقة الثانية د''(س) حولها||أن تكون د'(س) = 0 فقط||أن تكون د''(س) > 0 دائماً||أن تكون نقطة انقطاع",
            correctAnswer = "تغير إشارة المشتقة الثانية د''(س) حولها",
            explanation = "نقطة الانعطاف تفصل بين تقعر لأعلى وتقعر لأسفل، أي يتغير عندها إشارة المشتقة الثانية.",
            difficulty = "MEDIUM"
        ),

        // ==================== الوحدة 6: التكامل ====================
        QuestionEntity(
            id = 10601,
            subjectId = 1,
            unitId = 106,
            lessonId = 1061,
            questionType = "MCQ",
            questionText = "طول كل فترة جزئية Δس في تجزئة منتظمة للفترة [2 ، 8] مقسمة إلى 3 فترات يساوي:",
            options = "2||3||6||1",
            correctAnswer = "2",
            explanation = "Δس = (ب - أ) / ن = (8 - 2) / 3 = 6 / 3 = 2.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10602,
            subjectId = 1,
            unitId = 106,
            lessonId = 1062,
            questionType = "MCQ",
            questionText = "ناتج التكامل غير المحدد: ∫ (4س³ + جتا س) د س يساوي:",
            options = "س⁴ + جا س + جـ||12س² - جا س + جـ||س⁴ - جا س + جـ||4س⁴ + جا س + جـ",
            correctAnswer = "س⁴ + جا س + جـ",
            explanation = "تكامل 4س³ هو س⁴، وتكامل جتا س هو جا س، مع إضافة ثابت التكامل جـ.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10603,
            subjectId = 1,
            unitId = 106,
            lessonId = 1063,
            questionType = "MCQ",
            questionText = "قانون التكامل بالتجزيء لحساب ∫ ص د ع هو:",
            options = "ص ع - ∫ ع د ص||ص ع + ∫ ع د ص||ص' ع - ص ع'||∫ ص د ص - ∫ ع د ع",
            correctAnswer = "ص ع - ∫ ع د ص",
            explanation = "صيغة التجزيء الشهيرة المشتقة من قاعدة مشتقة حاصل ضرب دالتين: ∫ ص د ع = ص ع - ∫ ع د ص.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10604,
            subjectId = 1,
            unitId = 106,
            lessonId = 1064,
            questionType = "TRUE_FALSE",
            questionText = "إذا كانت د(س) دالة فردية، فإن تكاملها المحدد من -أ إلى أ يساوي صفراً.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، المساحة فوق محور السينات تلغي المساحة تحت محور السينات بسبب التناظر الفردي.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10605,
            subjectId = 1,
            unitId = 106,
            lessonId = 1065,
            questionType = "MCQ",
            questionText = "مشتقة دالة التكامل د/دس [ ∫ من 1 إلى س (جتا ت³) دت ] تساوي:",
            options = "جتا (س³)||3س² جتا (س³)||-جا (س³)||0",
            correctAnswer = "جتا (س³)",
            explanation = "حسب المبرهنة الأساسية للتفاضل والتكامل، نعوض بالحد العلوي س مباشرة مكان المتغير ت.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10606,
            subjectId = 1,
            unitId = 106,
            lessonId = 1066,
            questionType = "MCQ",
            questionText = "قانون حجم الجسم الناشئ عن دوران ص = د(س) حول محور السينات من أ إلى ب هو:",
            options = "π ∫ (ص)² د س||∫ ص د س||2π ∫ ص د س||π ∫ ص د س",
            correctAnswer = "π ∫ (ص)² د س",
            explanation = "الحجم هو مجموع مساحات الأقراص الدائرية π نق² ع = π ∫ ص² د س.",
            difficulty = "EASY"
        ),

        // ==================== الوحدة 7: الهندسة الإقليدية ====================
        QuestionEntity(
            id = 10701,
            subjectId = 1,
            unitId = 107,
            lessonId = 1071,
            questionType = "TRUE_FALSE",
            questionText = "المستقيمان الموازيان لمستقيم ثالث في الفراغ يكونان متوازيين.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، خاصية التعدي في التوازي صحيحة دائماً في الفراغ ثلاثي الأبعاد.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10702,
            subjectId = 1,
            unitId = 107,
            lessonId = 1072,
            questionType = "MCQ",
            questionText = "لكي يكون مستقيم عمودياً على مستوى، يكفي أن يكون عمودياً على:",
            options = "مستقيمين متقاطعين في ذلك المستوى||مستقيم واحد فقط||مستقيمين متوازيين||أي نقطة في المستوى",
            correctAnswer = "مستقيمين متقاطعين في ذلك المستوى",
            explanation = "مبرهنة التعامد الفراغية: التعامد مع مستقيمين متقاطعين في مستوى يضمن التعامد مع المستوى بأكمله.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10703,
            subjectId = 1,
            unitId = 107,
            lessonId = 1073,
            questionType = "MCQ",
            questionText = "قياس الزاوية الزوجية يساوي قياس:",
            options = "الزاوية المستوية العائدة لها||حافة الزاوية||مجموع زاويتي المستويين||90° دائماً",
            correctAnswer = "الزاوية المستوية العائدة لها",
            explanation = "يقاس انفراج الزاوية الزوجية بالزاوية المستوية الناتجة من إقامة عمودين على الحافة المشتركة من نقطة واحدة.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10704,
            subjectId = 1,
            unitId = 107,
            lessonId = 1074,
            questionType = "MCQ",
            questionText = "حجم الكرة التي نصف قطرها نق يعطى بالقانون:",
            options = "(4/3) π نق³||4 π نق²||π نق² ع||(1/3) π نق² ع",
            correctAnswer = "(4/3) π نق³",
            explanation = "حجم الكرة = (4/3) π نق³، ومساحة سطحها = 4π نق².",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 10705,
            subjectId = 1,
            unitId = 107,
            lessonId = 1075,
            questionType = "TRUE_FALSE",
            questionText = "حجم المخروط القائم يساوي ثلث (⅓) حجم الأسطوانة المشتركة معه في القاعدة والارتفاع.",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅، حجم المخروط = ⅓ π نق² ع، وحجم الأسطوانة = π نق² ع.",
            difficulty = "EASY"
        )
    )
}
