package com.example.data.curriculum

import com.example.data.local.entities.ExamEntity
import com.example.data.local.entities.QuestionEntity

object ChemistryQuestionsAndExams {

    fun getUnitExams(): List<ExamEntity> = listOf(
        ExamEntity(id = 301, subjectId = 3, unitId = 301, title = "تقويم الوحدة الأولى: العناصر الانتقالية", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 302, subjectId = 3, unitId = 302, title = "تقويم الوحدة الثانية: الطاقة الحرارية المصاحبة لتغيرات المادة", durationMinutes = 25, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 303, subjectId = 3, unitId = 303, title = "تقويم الوحدة الثالثة: الطاقة الكهربائية والأكسدة والاختزال", durationMinutes = 25, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 304, subjectId = 3, unitId = 304, title = "تقويم الوحدة الرابعة: الطاقة والتفاعلات النووية", durationMinutes = 25, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 305, subjectId = 3, unitId = 305, title = "تقويم الوحدة الخامسة: مركبات النيتروجين العضوية", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 306, subjectId = 3, unitId = 306, title = "تقويم الوحدة السادسة: الكيمياء الحيوية", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 307, subjectId = 3, unitId = 307, title = "تقويم الوحدة السابعة: الذهب الأسود (النفط)", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 308, subjectId = 3, unitId = 308, title = "تقويم الوحدة الثامنة: صناعات كيميائية في خدمة الإنسان", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false),
        ExamEntity(id = 309, subjectId = 3, unitId = 309, title = "تقويم الوحدة التاسعة: الكيمياء والبيئة", durationMinutes = 20, totalQuestions = 5, passingScore = 60, isPremium = false)
    )

    fun getUnitQuestions(): List<QuestionEntity> = listOf(
        // ==================== أسئلة الوحدة الأولى: العناصر الانتقالية ====================
        QuestionEntity(
            id = 30101,
            subjectId = 3,
            unitId = 301,
            lessonId = 3011,
            questionText = "تقع عناصر السلسلة الانتقالية الأولى (3d) في الدورة:",
            questionType = "MULTIPLE_CHOICE",
            options = "الثالثة||الرابعة||الخامسة||السادسة",
            correctAnswer = "الرابعة",
            explanation = "تقع السلسلة الانتقالية الأولى في الدورة الرابعة من الجدول الدوري، حيث يبدأ امتلاء المستوى الفرعي 3d بالإلكترونات بعد المستوى 4s، وتبدأ بعنصر السكانديوم وتنتهي بالخارصين.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30102,
            subjectId = 3,
            unitId = 301,
            lessonId = 3012,
            questionText = "أي الأيونات التالية يعتبر مادة دايامغناطيسية وغير ملونة في محاليلها المائية؟",
            questionType = "MULTIPLE_CHOICE",
            options = "Fe²⁺||Cu²⁺||Zn²⁺||Ti³⁺",
            correctAnswer = "Zn²⁺",
            explanation = "أيون الخارصين Zn²⁺ ينتهي توزيعه الإلكتروني بـ [Ar] 3d¹⁰؛ حيث تكون جميع أوربيتالات المستوى الفرعي 3d ممتلئة تماماً بإلكترونات مزدوجة (n = 0)، فلا يمتلك إلكترونات مفردة ويكون دايامغناطيسياً وغير ملون.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30103,
            subjectId = 3,
            unitId = 301,
            lessonId = 3013,
            questionText = "التوزيع الإلكتروني الصحيح لذرة الكروم (²⁴Cr) في حالتها المستقرة هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "[Ar] 4s² 3d⁴||[Ar] 4s¹ 3d⁵||[Ar] 4s² 3d⁵||[Ar] 3d⁶",
            correctAnswer = "[Ar] 4s¹ 3d⁵",
            explanation = "يشذ الكروم في توزيعه الإلكتروني فينتقل إلكترون من 4s إلى 3d ليصبح كلا المستويين 4s و 3d نصف ممتلئ (4s¹ 3d⁵)، مما يعطي الذرة أقصى درجات الاستقرار والثبات النسبي.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30104,
            subjectId = 3,
            unitId = 301,
            lessonId = 3014,
            questionText = "عند تفاعل برادة الحديد المسخنة لدرجة الاحمرار مع غاز الكلور يتكون مركب:",
            questionType = "MULTIPLE_CHOICE",
            options = "كلوريد الحديد الثنائي FeCl₂||كلوريد الحديد الثلاثي FeCl₃||أكسيد الحديد المغناطيسي Fe₃O₄||سبيكة الحديد والكلور",
            correctAnswer = "كلوريد الحديد الثلاثي FeCl₃",
            explanation = "يتكون كلوريد الحديد الثلاثي FeCl₃ لأن غاز الكلور عامل مؤكسد قوي يؤكسد أي كلوريد حديد ثنائي قد يتكون فورياً إلى كلوريد حديد ثلاثي: 2Fe + 3Cl₂ → 2FeCl₃.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30105,
            subjectId = 3,
            unitId = 301,
            lessonId = 3015,
            questionText = "يحدث خمول كيميائي لفلز الحديد عند غمسه في حمض النيتريك المركز.",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅؛ بسبب تكوّن طبقة رقيقة جداً وميكروسكوبية غير مسامية من الأكسيد على سطح الحديد تحميه وتمنع استمرار تفاعل الحمض معه، وتزول هذه الطبقة بالحك أو بحمض HCl المخفف.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة الثانية: الطاقة الحرارية وتغيرات المادة ====================
        QuestionEntity(
            id = 30201,
            subjectId = 3,
            unitId = 302,
            lessonId = 3022,
            questionText = "ما كمية الحرارة (بوحدة الجول) اللازمة لرفع درجة حرارة 100 g من الماء بمقدار 5°C (علماً بأن c للماء = 4.18 J/g·°C)؟",
            questionType = "MULTIPLE_CHOICE",
            options = "418 J||2090 J||836 J||500 J",
            correctAnswer = "2090 J",
            explanation = "تطبيق مباشر على القانون: Q = m c ΔT = 100 × 4.18 × 5 = 2090 جول (2.09 كيلوجول).",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30202,
            subjectId = 3,
            unitId = 302,
            lessonId = 3023,
            questionText = "في التفاعل الطارد للحرارة، يكون المحتوى الحراري للنواتج:",
            questionType = "MULTIPLE_CHOICE",
            options = "أكبر من المحتوى الحراري للمتفاعلات وتكون ΔH موجبة||أقل من المحتوى الحراري للمتفاعلات وتكون ΔH سالبة||مساوياً للمحتوى الحراري للمتفاعلات وتكون ΔH صفراً||أكبر من المحتوى الحراري للمتفاعلات وتكون ΔH سالبة",
            correctAnswer = "أقل من المحتوى الحراري للمتفاعلات وتكون ΔH سالبة",
            explanation = "في التفاعل الطارد للحرارة تنطلق طاقة إلى الوسط المحيط، فيكون المحتوى الحراري للنواتج أقل من المتفاعلات، وتكون إشارة التغير في الإنثالبي ΔH سالبة دائماً.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30203,
            subjectId = 3,
            unitId = 302,
            lessonId = 3025,
            questionText = "حرارة التكوين القياسية (ΔH°f) لعنصر الأكسجين النقي في صورته الغازية O₂(g) تساوي:",
            questionType = "MULTIPLE_CHOICE",
            options = "-393.5 kJ/mol||+100 kJ/mol||صفر||-285.8 kJ/mol",
            correctAnswer = "صفر",
            explanation = "حرارة التكوين القياسية لأي عنصر نقي في حالته القياسية الأكثر استقراراً عند 25°C وضغط 1 atm تساوي صفراً فرضياً بحسب الاتفاق الدولي في الديناميكا الحرارية.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30204,
            subjectId = 3,
            unitId = 302,
            lessonId = 3026,
            questionText = "المركب الذي يمتلك أعلى درجة ثبات واستقرار حراري وصعوبة في التفكك هو صاحب:",
            questionType = "MULTIPLE_CHOICE",
            options = "أكبر قيمة موجبة لحرارة التكوين القياسية||أكبر قيمة سالبة لحرارة التكوين القياسية||حرارة تكوين قياسية تساوي صفراً||حرارة تكوين قياسية موجبة صغيرة",
            correctAnswer = "أكبر قيمة سالبة لحرارة التكوين القياسية",
            explanation = "كلما كانت قيمة حرارة التكوين القياسية (ΔH°f) سالبة أكثر، دل ذلك على انطلاق طاقة أكبر أثناء تكوينه وانخفاض محتواه الحراري، مما يجعله أكثر استقراراً وثباتاً حرارياً وصعب التفكك.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30205,
            subjectId = 3,
            unitId = 302,
            lessonId = 3027,
            questionText = "إذا كانت حرارة تكوين CO₂ تساوي -393.5 kJ/mol، وتكوين H₂O السائل تساوي -285.8 kJ/mol، وتكوين الميثان CH₄ تساوي -74.8 kJ/mol، فإن حرارة احتراق الميثان تساوي:",
            questionType = "MULTIPLE_CHOICE",
            options = "-890.3 kJ/mol||+890.3 kJ/mol||-604.5 kJ/mol||-754.2 kJ/mol",
            correctAnswer = "-890.3 kJ/mol",
            explanation = "ΔH°reaction = ΣΔH°f(نواتج) - ΣΔH°f(متفاعلات) = [ (-393.5) + 2(-285.8) ] - [ (-74.8) + 0 ] = -965.1 - (-74.8) = -890.3 kJ/mol.",
            difficulty = "HARD"
        ),

        // ==================== أسئلة الوحدة الثالثة: الطاقة الكهربائية وتفاعلات الأكسدة والاختزال ====================
        QuestionEntity(
            id = 30301,
            subjectId = 3,
            unitId = 303,
            lessonId = 3031,
            questionText = "في التفاعل: Zn + Cu²⁺ → Zn²⁺ + Cu، المادة التي تعمل كعامل مؤكسد هي:",
            questionType = "MULTIPLE_CHOICE",
            options = "ذرات الخارصين Zn||أيونات النحاس Cu²⁺||أيونات الخارصين Zn²⁺||ذرات النحاس Cu",
            correctAnswer = "أيونات النحاس Cu²⁺",
            explanation = "أيون النحاس Cu²⁺ اكتسب إلكترونين ونقص عدد تأكسده من +2 إلى 0 (حدثت له عملية اختزال)، والمادة التي تُختزل هي العامل المؤكسد.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30302,
            subjectId = 3,
            unitId = 303,
            lessonId = 3034,
            questionText = "في خلية دانيال الجلفانية القياسية، تنتقل الإلكترونات عبر السلك الخارجي من:",
            questionType = "MULTIPLE_CHOICE",
            options = "قطب النحاس إلى قطب الخارصين||قطب الخارصين (المصعد) إلى قطب النحاس (المهبط)||القنطرة الملحية إلى المحلول||المهبط إلى المصعد",
            correctAnswer = "قطب الخارصين (المصعد) إلى قطب النحاس (المهبط)",
            explanation = "تتدفق الإلكترونات في الدائرة الخارجية دائماً من المصعد (الآنود السالب: الخارصين حيث تحدث الأكسدة) إلى المهبط (الكاثود الموجب: النحاس حيث يحدث الاختزال).",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30303,
            subjectId = 3,
            unitId = 303,
            lessonId = 3035,
            questionText = "إذا علمت أن جهد اختزال الخارصين E° = -0.76 V وجهد اختزال النحاس E° = +0.34 V، فإن القوة الدافعة الكهربية للخلية E°cell تساوي:",
            questionType = "MULTIPLE_CHOICE",
            options = "+1.10 V||-1.10 V||+0.42 V||-0.42 V",
            correctAnswer = "+1.10 V",
            explanation = "E°cell = E°مهبط (النحاس) - E°مصعد (الخارصين) = (+0.34) - (-0.76) = 0.34 + 0.76 = +1.10 فولت (تفاعل تلقائي).",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30304,
            subjectId = 3,
            unitId = 303,
            lessonId = 3038,
            questionText = "عند طلاء ملعقة حديدية بالفضة كهربائياً، يجب توصيل الملعقة الحديدية بـ:",
            questionType = "MULTIPLE_CHOICE",
            options = "المصعد (الآنود الموجب)|[المهبط (الكاثود السالب)|القنطرة الملحية||مصدر تيار متردد",
            correctAnswer = "المهبط (الكاثود السالب)",
            explanation = "في خلايا الطلاء الكهربي، يوصل الجسم المراد طلاؤه دائماً بالمهبط (الكاثود المتصل بسالب البطارية) لتتجه نحوه كاتيونات فلز الطلاء الموجبة وتختزل وتترسب على سطحه بانتظام.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30305,
            subjectId = 3,
            unitId = 303,
            lessonId = 3039,
            questionText = "القطب المضحي المستخدم لحماية هياكل السفن الحديدية من الصدأ يصنع من فلز أقل نشاطاً من الحديد كالنحاس.",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "خطأ",
            explanation = "خطأ ❌؛ تصحيح العبارة: يصنع القطب المضحي من فلز 'أكثر نشاطاً' من الحديد كالمغنيسيوم (Mg) أو الخارصين (Zn) ليعمل كمصعد ويتآكل هو ويضحي بنفسه بدلاً من الحديد.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة الرابعة: الطاقة والتفاعلات النووية ====================
        QuestionEntity(
            id = 30401,
            subjectId = 3,
            unitId = 304,
            lessonId = 3041,
            questionText = "نظائر العنصر الواحد تتشابه في الخواص الكيميائية لتساوي:",
            questionType = "MULTIPLE_CHOICE",
            options = "عدد النيوترونات داخل النواة||العدد الكتلي A||العدد الذري وعدد إلكترونات التكافؤ||الكتلة الذرية والحجم",
            correctAnswer = "العدد الذري وعدد إلكترونات التكافؤ",
            explanation = "تتشابه النظائر في خواصها وتفاعلاتها الكيميائية لتطابق عدد البروتونات وإلكترونات التكافؤ وتماثل التوزيع الإلكتروني في مستويات الطاقة الخارجية.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30402,
            subjectId = 3,
            unitId = 304,
            lessonId = 3043,
            questionText = "عند انبعاث جسيم بيتا السالب (β⁻) من نواة ذرة مشعة، فإن العدد الذري للنواة الناتجة:",
            questionType = "MULTIPLE_CHOICE",
            options = "يقل بمقدار 2||يزداد بمقدار 1||يقل بمقدار 1||لا يتغير إطلاقاً",
            correctAnswer = "يزداد بمقدار 1",
            explanation = "انبعاث جسيم بيتا ينتج من تحلل نيوترون متعادل داخل النواة إلى بروتون موجب وإلكترون بيتا؛ فيبقى العدد الكتلي ثابتاً ويزداد العدد الذري بمقدار 1.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30403,
            subjectId = 3,
            unitId = 304,
            lessonId = 3044,
            questionText = "المقياس الحقيقي والدقيق لمدى استقرار وتماسك النواة هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "طاقة الترابط النووي الكلية E||طاقة الترابط النووي لكل نيوكلون (E/A)||العدد الكتلي A فقط||عدد النيوترونات فقط",
            correctAnswer = "طاقة الترابط النووي لكل نيوكلون (E/A)",
            explanation = "طاقة الترابط لكل نيوكلون (E ÷ A) هي المقياس الفعلي؛ فكلما كانت قيمتها أكبر، كانت النواة أكثر استقراراً، وتصل ذروتها في نواة الحديد-56 بنحو 8.8 MeV/nucleon.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30404,
            subjectId = 3,
            unitId = 304,
            lessonId = 3047,
            questionText = "أيها يمتلك أعلى قدرة على النفاذ واختراق الأجسام والمواد الصلبة؟",
            questionType = "MULTIPLE_CHOICE",
            options = "جسيمات ألفا||جسيمات بيتا||أشعة جاما||البروتونات",
            correctAnswer = "أشعة جاما",
            explanation = "أشعة جاما فوتونات كهرومغناطيسية متعادلة عديمة الكتلة والشحنة وذات طاقة وتردد فائقين، مما يمنحها قدرة اختراق ونفاذ هائلة لا توقفها إلا الدروع السميكة من الرصاص.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30405,
            subjectId = 3,
            unitId = 304,
            lessonId = 3048,
            questionText = "وظيفة قضبان الكادميوم أو البورون في المفاعلات النووية هي إبطاء سرعة النيوترونات.",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "خطأ",
            explanation = "خطأ ❌؛ تصحيح العبارة: وظيفة قضبان الكادميوم هي 'امتصاص واصطياد النيوترونات الزائدة' للتحكم في معدل الانشطار المتسلسل، بينما الذي يقوم بإبطاء سرعة النيوترونات هو مادة المهدئ (مثل الماء الثقيل أو الجرافيت).",
            difficulty = "MEDIUM"
        ),

        // ==================== أسئلة الوحدة الخامسة: مركبات النيتروجين العضوية ====================
        QuestionEntity(
            id = 30501,
            subjectId = 3,
            unitId = 305,
            lessonId = 3051,
            questionText = "المركب (CH₃)₂NH يصنف على أنه:",
            questionType = "MULTIPLE_CHOICE",
            options = "أمين أولي||أمين ثانوي||أمين ثالثي||أميد أليفاتي",
            correctAnswer = "أمين ثانوي",
            explanation = "هو أمين ثانوي (ثنائي ميثيل أمين) لوجود مجموعتي ألكيل (ميثيل) مرتبطتين بذرة النيتروجين مع ذرة هيدروجين واحدة: R₂NH.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30502,
            subjectId = 3,
            unitId = 305,
            lessonId = 3052,
            questionText = "الغاز المتصاعد عند تسخين مركب الأسيتاميد (CH₃CONH₂) مع محلول هيدروكسيد الصوديوم هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "غاز الهيدروجين H₂||غاز الأمونيا (النشادر NH₃)||غاز ثاني أكسيد الكربون CO₂||غاز النيتروجين N₂",
            correctAnswer = "غاز الأمونيا (النشادر NH₃)",
            explanation = "التحلل المائي القاعدي للأميدات يطلق غاز الأمونيا NH₃ المميز برائحته النفاذة التي تزرق ورقة تباع الشمس الحمراء: CH₃CONH₂ + NaOH → CH₃COONa + NH₃↑.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30503,
            subjectId = 3,
            unitId = 305,
            lessonId = 3053,
            questionText = "ينتج عند الاختزال التام لمركب إيثان نيتريل (CH₃C≡N) بواسطة الهيدروجين والنيكل مركب:",
            questionType = "MULTIPLE_CHOICE",
            options = "حمض الأسيتيك||إيثيل أمين CH₃CH₂NH₂||أسيتاميد||ميثان",
            correctAnswer = "إيثيل أمين CH₃CH₂NH₂",
            explanation = "اختزال النيتريلات بالهيدروجين يكسر الرابطة الثلاثية ويضيف 4 ذرات هيدروجين منتجاً الأمين الأولي المقابل: CH₃C≡N + 2H₂ → CH₃CH₂NH₂.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30504,
            subjectId = 3,
            unitId = 305,
            lessonId = 3054,
            questionText = "الحمض الأميني الوحيد الذي لا يحتوي على ذرة كربون كيرالية (غير متماثلة) هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "الألانين||الفالين||الجلايسين||السيستين",
            correctAnswer = "الجلايسين",
            explanation = "حمض الجلايسين (H₂N-CH₂-COOH) ترتبط فيه ذرة كربون ألفا بذرتي هيدروجين متماثلتين (R = H)، فلا يحتوي على أربع مجموعات مختلفة، وهو الحمض الأميني الوحيد غير النشط ضوئياً.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30505,
            subjectId = 3,
            unitId = 305,
            lessonId = 3054,
            questionText = "الرابطة الكيميائية التي تربط الأحماض الأمينية معاً في سلسلة البروتين هي رابطة:",
            questionType = "MULTIPLE_CHOICE",
            options = "جليكوسيدية||ببتيدية (أميدية)||إسترية||أيونية فقط",
            correctAnswer = "ببتيدية (أميدية)",
            explanation = "الرابطة الببتيدية (-CO-NH-) هي رابطة تساهمية تتكون بين مجموعة كربوكسيل لحمض أميني ومجموعة أمين لحمض أميني مجاور مع فقدان جزيء ماء.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة السادسة: الكيمياء الحيوية ====================
        QuestionEntity(
            id = 30601,
            subjectId = 3,
            unitId = 306,
            lessonId = 3063,
            questionText = "السكروز (سكر المائدة) سكر ثنائي يتكون من ارتباط جزيئي:",
            questionType = "MULTIPLE_CHOICE",
            options = "جلوكوز + جلوكوز||جلوكوز + فركتوز||جلوكوز + جلاكتوز||فركتوز + جلاكتوز",
            correctAnswer = "جلوكوز + فركتوز",
            explanation = "يتكون جزيء السكروز من تكاثف جزيء جلوكوز (ألدوهكسوز) مع جزيء فركتوز (كيتوهكسوز) برابطة جليكوسيدية مع نزع جزيء ماء.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30602,
            subjectId = 3,
            unitId = 306,
            lessonId = 3063,
            questionText = "الكاشف المستخدم في المختبر للكشف عن وجود النشا هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "كاشف بندكت||محلول اليود||كاشف البيوريت||نترات الفضة",
            correctAnswer = "محلول اليود",
            explanation = "يتفاعل محلول اليود البرتقالي مع جزيئات النشا الحلزونية ليعطي لوناً أزرق داكناً مميزاً.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30603,
            subjectId = 3,
            unitId = 306,
            lessonId = 3065,
            questionText = "تفاعل التصبن ينتج عن التحلل المائي القاعدي للدهون وينتج عنه:",
            questionType = "MULTIPLE_CHOICE",
            options = "أحماض أمينية وماء||صابون وجلسرين||إيثانول وحمض خليك||جلوكوز وفركتوز",
            correctAnswer = "صابون وجلسرين",
            explanation = "تسخين الدهون الثلاثية مع محلول هيدروكسيد الصوديوم يكسر الروابط الإسترية لينتج كحول الجلسرول (الجلسرين) وأملاح الصوديوم للأحماض الدهنية (الصابون).",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30604,
            subjectId = 3,
            unitId = 306,
            lessonId = 3066,
            questionText = "أي من الفيتامينات التالية يذوب في الماء ويفرز الفائض منه عبر البول؟",
            questionType = "MULTIPLE_CHOICE",
            options = "فيتامين A||فيتامين D||فيتامين C||فيتامين K",
            correctAnswer = "فيتامين C",
            explanation = "فيتامين C (حمض الأسكوربيك) وفيتامينات مجموعة B هي فيتامينات ذائبة في الماء، بينما الفيتامينات A, D, E, K تذوب في الدهون وتخزن في الكبد.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30605,
            subjectId = 3,
            unitId = 306,
            lessonId = 3067,
            questionText = "تعمل الإنزيمات على تسريع التفاعلات الحيوية عن طريق زيادة طاقة التنشيط المطلوبة.",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "خطأ",
            explanation = "خطأ ❌؛ تصحيح العبارة: تعمل الإنزيمات على تسريع التفاعلات الحيوية عن طريق 'خفض طاقة التنشيط' (Activation Energy) وتسهيل وصول الجزيئات للحالة الانتقالية.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة السابعة: الذهب الأسود (النفط) ====================
        QuestionEntity(
            id = 30701,
            subjectId = 3,
            unitId = 307,
            lessonId = 3072,
            questionText = "في مكمن النفط المقفل، يكون الترتيب الرأسي للمكونات من القمة إلى القاع هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "ماء مالح ثم نفط خام ثم غاز طبيعي||غاز طبيعي ثم نفط خام ثم ماء مالح||نفط خام ثم غاز طبيعي ثم ماء مالح||غاز طبيعي ثم ماء مالح ثم نفط خام",
            correctAnswer = "غاز طبيعي ثم نفط خام ثم ماء مالح",
            explanation = "بسبب اختلاف الكثافة النوعية: يطفو الغاز الطبيعي في القمة لأنه الأخف، ثم النفط الخام السائل في المنتصف، ويستقر الماء المالح في القاع لأنه الأعلى كثافة.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30702,
            subjectId = 3,
            unitId = 307,
            lessonId = 3073,
            questionText = "أهم وأدق الطرق الجيوفيزيائية المستخدمة في رسم مقاطع ثلاثية الأبعاد لمكامن النفط في باطن الأرض هي:",
            questionType = "MULTIPLE_CHOICE",
            options = "المسح الإشعاعي||المسح الزلزالي (السيزمي)||قياس درجات الحرارة||التصوير الجوي العادي",
            correctAnswer = "المسح الزلزالي (السيزمي)",
            explanation = "المسح السيزمي يعتمد على إرسال موجات صوتية اصطناعية واستقبال انعكاساتها بمجسات الجيوفونات لتحديد أعماق وتراكيب الطبقات الحابسة للنفط بدقة فائقة.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30703,
            subjectId = 3,
            unitId = 307,
            lessonId = 3075,
            questionText = "المشتق النفطي الذي يتكثف في قمة برج التقطير التجزيئي عند درجات حرارة أقل من 40°C هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "الكيروسين||الديزل||غاز البترول المسال (LPG)||شمع البرافين",
            correctAnswer = "غاز البترول المسال (LPG)",
            explanation = "الغازات البترولية (البروبان والبيوتان C₃ - C₄) هي الأقل وزناً وأدناها في درجات الغليان وتخرج وتتكثف من قمة برج التجزئة.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30704,
            subjectId = 3,
            unitId = 307,
            lessonId = 3075,
            questionText = "العملية التي تهدف لتحويل الهيدروكربونات الثقيلة طويلة السلسلة إلى هيدروكربونات خفيفة كالجازولين تسمى:",
            questionType = "MULTIPLE_CHOICE",
            options = "التقطير البسيط||التكسير الحفزي||التصبن||البلمرة",
            correctAnswer = "التكسير الحفزي",
            explanation = "التكسير الحفزي (Catalytic Cracking) يكسر السلاسل الهيدروكربونية الطويلة بالحرارة والضغط والعوامل الحفازة لإنتاج وقود الجازولين الخفيف ذي القيمة الاقتصادية العالية.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30705,
            subjectId = 3,
            unitId = 307,
            lessonId = 3076,
            questionText = "ينتج عن الاحتراق غير التام لوقود السيارات عند نقص كمية الأكسجين غاز سام عديم الرائحة هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "ثاني أكسيد الكربون CO₂||أول أكسيد الكربون CO||غاز النيتروجين N₂||الميثان CH₄",
            correctAnswer = "أول أكسيد الكربون CO",
            explanation = "الاحتراق غير التام يؤدي لأكسدة جزئية للكربون فينتج غاز أول أكسيد الكربون (CO) السام القاتل.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة الثامنة: صناعات كيميائية في خدمة الإنسان ====================
        QuestionEntity(
            id = 30801,
            subjectId = 3,
            unitId = 308,
            lessonId = 3082,
            questionText = "العنصر الغذائي المسؤول عن النمو الجذري السليم والتزهير ونقل الطاقة في النبات هو:",
            questionType = "MULTIPLE_CHOICE",
            options = "النيتروجين N||الفوسفور P||البوتاسيوم K||الحديد Fe",
            correctAnswer = "الفوسفور P",
            explanation = "الفوسفور هو العنصر الأساسي المسؤول عن تنشيط نمو وتفرع الجذور، تخليق جزيئات الطاقة ATP، وتكوين الأزهار والثمار.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30802,
            subjectId = 3,
            unitId = 308,
            lessonId = 3083,
            questionText = "يتميز سماد اليوريا CO(NH₂)₂ بأنه يحتوي على نسبة نيتروجين صافية تعادل حوالي:",
            questionType = "MULTIPLE_CHOICE",
            options = "21%||35%||46.7%||60%",
            correctAnswer = "46.7%",
            explanation = "سماد اليوريا النقي يحتوي على 46.7% نيتروجين، وهي أعلى نسبة نيتروجين بين جميع الأسمدة الكيميائية الصلبة المتداولة عالمياً.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30803,
            subjectId = 3,
            unitId = 308,
            lessonId = 3084,
            questionText = "حُظر استخدام مركب DDT كمبيد حشري في معظم دول العالم بسبب:",
            questionType = "MULTIPLE_CHOICE",
            options = "رائحته الكريهة||سرعة تطايره وتحلله||ثباته الكيميائي الشديد وتراكمه وتضخمه في السلاسل الغذائية||عدم قدرته على قتل البعوض",
            correctAnswer = "ثباته الكيميائي الشديد وتراكمه وتضخمه في السلاسل الغذائية",
            explanation = "DDT لا يتحلل حيوياً ويذوب في الدهون فيتراكم في أنسجة الكائنات الحية ويتضاعف تركيزه في السلاسل الغذائية (Biomagnification) مسبباً أضراراً وراثية وسرطانية.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30804,
            subjectId = 3,
            unitId = 308,
            lessonId = 3086,
            questionText = "ألياف النايلون 6,6 تنتمي كيميائياً إلى فئة:",
            questionType = "MULTIPLE_CHOICE",
            options = "البولي إستر||البولي أميد||البولي أوليفين||السليلوز المعالج",
            correctAnswer = "البولي أميد",
            explanation = "النايلون 6,6 ينتج من بلمرة التكاثف بين ثنائي أمين وثنائي حمض كربوكسيلي وترتبط وحداته بروابط أميدية متكررة (-CO-NH-).",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30805,
            subjectId = 3,
            unitId = 308,
            lessonId = 3087,
            questionText = "يعمل جزيء المنظف الصناعي على تنظيف البقع الدهنية لأن له رأساً محباً للماء وذيلاً كاره للماء محباً للدهون.",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅؛ ينغرس الذيل الهيدروكربوني الكاره للماء داخل الدهون، وتبرز الرؤوس القطبية السالبة في الماء، فتنفر وتتفتت البقعة إلى قطيرات مذيلات معلقة وتزال مع ماء الشطف.",
            difficulty = "EASY"
        ),

        // ==================== أسئلة الوحدة التاسعة: الكيمياء والبيئة ====================
        QuestionEntity(
            id = 30901,
            subjectId = 3,
            unitId = 309,
            lessonId = 3093,
            questionText = "أي من المواد التالية يعتبر ملوثاً غير قابل للتحلل الحيوي ويبقى في البيئة لقرون؟",
            questionType = "MULTIPLE_CHOICE",
            options = "بقايا الأوراق والأخشاب||اللدائن البلاستيكية كالبولي إيثيلين||بقايا الخضروات والفواكه||مياه الصرف الصحي المعالجة",
            correctAnswer = "اللدائن البلاستيكية كالبولي إيثيلين",
            explanation = "اللدائن البلاستيكية مبنية من سلاسل بوليمرية اصطناعية قوية لا تستطيع الكائنات المحللة تفكيكها وتحتاج مئات السنين لتتفتت في البيئة.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30902,
            subjectId = 3,
            unitId = 309,
            lessonId = 3094,
            questionText = "المسبب الرئيسي لظاهرة الأمطار الحمضية هو انبعاث غازات:",
            questionType = "MULTIPLE_CHOICE",
            options = "الميثان وبخار الماء||أكاسيد الكبريت (SO₂) وأكاسيد النيتروجين (NO₂)||غاز الأكسجين والهيليوم||الأرجون وأول أكسيد الكربون",
            correctAnswer = "أكاسيد الكبريت (SO₂) وأكاسيد النيتروجين (NO₂)",
            explanation = "تذوب أكاسيد الكبريت وأكاسيد النيتروجين الناتجة من حرق الوقود الأحفوري في قطرات المطر مكونة حمض الكبريتيك H₂SO₄ وحمض النيتريك HNO₃ مما يخفض pH المطر دون 5.6.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30903,
            subjectId = 3,
            unitId = 309,
            lessonId = 3094,
            questionText = "تآكل واستنزاف طبقة الأوزون في طبقة الستراتوسفير يعود إلى استخدام مركبات:",
            questionType = "MULTIPLE_CHOICE",
            options = "الكلوروفلوروكربون (الفريونات CFCs)||ثاني أكسيد الكربون CO₂||الأمونيا NH₃||الميثان CH₄",
            correctAnswer = "الكلوروفلوروكربون (الفريونات CFCs)",
            explanation = "تتحلل مركبات CFCs بالأشعة فوق البنفسجية محررة ذرات كلور نشطة تحفز تفكك وتدمير آلاف جزيئات الأوزون O₃.",
            difficulty = "EASY"
        ),
        QuestionEntity(
            id = 30904,
            subjectId = 3,
            unitId = 309,
            lessonId = 3095,
            questionText = "ظاهرة الإثراء الغذائي (Eutrophication) في البحيرات تؤدي مباشرة إلى:",
            questionType = "MULTIPLE_CHOICE",
            options = "زيادة نسبة الأكسجين الذائب في الماء||ازدهار الطحالب يليه نقص حاد في الأكسجين الذائب واختناق الأسماك||تحول ماء البحيرة إلى ماء مقطر||زيادة أعداد الأسماك والمحار",
            correctAnswer = "ازدهار الطحالب يليه نقص حاد في الأكسجين الذائب واختناق الأسماك",
            explanation = "فائض النيتروجين والفوسفور يسبب تكاثراً متفجراً للطحالب، ثم تتعفن بموتها وتستهلك البكتيريا كل الأكسجين الذائب في الماء مما يؤدي لاختناق وموت جميع الأحياء المائية.",
            difficulty = "MEDIUM"
        ),
        QuestionEntity(
            id = 30905,
            subjectId = 3,
            unitId = 309,
            lessonId = 3097,
            questionText = "يقوم 'المحول الحفزي' في عوادم السيارات بتحويل الغازات السامة (CO وأكاسيد النيتروجين) إلى غازات غير ضارة (CO₂ و N₂).",
            questionType = "TRUE_FALSE",
            options = "صح||خطأ",
            correctAnswer = "صح",
            explanation = "صح ✅؛ يحتوي المحول الحفزي على معادن ثمينة (البلاتين والبلاديوم والروديوم) كعوامل حفازة تؤكسد أول أكسيد الكربون إلى CO₂ وتختزل أكاسيد النيتروجين إلى غاز N₂ المتعادل الآمن.",
            difficulty = "EASY"
        )
    )
}
