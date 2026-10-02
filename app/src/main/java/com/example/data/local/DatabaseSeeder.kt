package com.example.data.local

import com.example.data.curriculum.ChemistryQuestionsAndExams
import com.example.data.curriculum.ChemistryUnits1to3
import com.example.data.curriculum.ChemistryUnits4to6
import com.example.data.curriculum.ChemistryUnits7to9
import com.example.data.curriculum.MathQuestionsAndExams
import com.example.data.curriculum.MathUnits1to3
import com.example.data.curriculum.MathUnits4to7
import com.example.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        // Check if database is already seeded to avoid heavy work on every app startup
        val existingAdmin = db.userDao().getUserByPhoneOrEmail("782916997")
        if (existingAdmin != null) {
            return@withContext
        }

        // Seed Admin
        db.userDao().insertUser(
            UserEntity(
                fullName = "المدير العام للمنصة",
                phoneOrEmail = "782916997",
                passwordHash = "admin123",
                role = "ADMIN",
                grade = "الإدارة العامة",
                track = "علمي",
                isActive = true
            )
        )

        // Seed Demo Student
        val studentId = db.userDao().insertUser(
            UserEntity(
                fullName = "محمد أحمد صالح",
                phoneOrEmail = "770000000",
                passwordHash = "123456",
                role = "STUDENT",
                grade = "الصف الثالث الثانوي",
                track = "علمي",
                isActive = true
            )
        )

        // Student account is created WITHOUT subscription. Official activation code or payment required.

        // Seed Plans
        val planDao = db.subscriptionPlanDao()
        val defaultPlans = listOf(
            SubscriptionPlanEntity(
                name = "اشتراك 3 أشهر",
                durationMonths = 3,
                priceYmr = 12000,
                description = "وصول كامل لجميع المواد وشروحات الدروس وبنك الأسئلة والمعلم الذكي لمدة 3 أشهر.",
                isPopular = false
            ),
            SubscriptionPlanEntity(
                name = "اشتراك 6 أشهر",
                durationMonths = 6,
                priceYmr = 24000,
                description = "الخيار الأنسب للفصل الدراسي والتحضير المكثف للاختبارات الوزارية مع كافة الميزات.",
                isPopular = true
            ),
            SubscriptionPlanEntity(
                name = "اشتراك سنوي (12 شهر)",
                durationMonths = 12,
                priceYmr = 30000,
                description = "تغطية كاملة للمنهج الوزاري طوال العام الدراسي وحتى اختبارات الشهادة العامة مع خصم خاص.",
                isPopular = false
            )
        )
        planDao.insertPlans(defaultPlans)

        // Seed Subscription Codes (Including easy-to-use codes for testing)
        val codeDao = db.subscriptionCodeDao()
        val defaultCodes = listOf(
            SubscriptionCodeEntity(
                code = "YEMEN-2026",
                planName = "اشتراك سنوي (12 شهر)",
                durationMonths = 12,
                priceYmr = 30000,
                status = "UNUSED",
                tiedStudentPhone = null
            ),
            SubscriptionCodeEntity(
                code = "YEMEN-90DAYS",
                planName = "اشتراك 3 أشهر",
                durationMonths = 3,
                priceYmr = 12000,
                status = "UNUSED",
                tiedStudentPhone = null
            ),
            SubscriptionCodeEntity(
                code = "YEM-8K4P-29MX",
                planName = "اشتراك 3 أشهر",
                durationMonths = 3,
                priceYmr = 12000,
                status = "UNUSED",
                tiedStudentPhone = null
            ),
            SubscriptionCodeEntity(
                code = "YEM-9F3R-77KL",
                planName = "اشتراك 6 أشهر",
                durationMonths = 6,
                priceYmr = 24000,
                status = "UNUSED",
                tiedStudentPhone = null
            ),
            SubscriptionCodeEntity(
                code = "YEM-YEAR-2026",
                planName = "اشتراك سنوي (12 شهر)",
                durationMonths = 12,
                priceYmr = 30000,
                status = "UNUSED",
                tiedStudentPhone = null
            )
        )
        defaultCodes.forEach { codeDao.insertCode(it) }

        // Seed Subjects
        val currDao = db.curriculumDao()
        val subjects = listOf(
            SubjectEntity(
                id = 1,
                name = "الرياضيات",
                iconName = "math",
                track = "SCIENTIFIC",
                description = "التفاضل والتكامل، مبرهنات المشتقات، الأعداد المركبة، المصفوفات والهندسة التحليلية.",
                sortOrder = 1
            ),
            SubjectEntity(
                id = 2,
                name = "الفيزياء",
                iconName = "physics",
                track = "SCIENTIFIC",
                description = "التيار المتردد، الدوائر المهتزة، التأثير الكهروضوئي، طيف الذرة، والفيزياء النووية.",
                sortOrder = 2
            ),
            SubjectEntity(
                id = 3,
                name = "الكيمياء",
                iconName = "chemistry",
                track = "SCIENTIFIC",
                description = "الكيمياء الحرارية وقانون هس، سرعة التفاعل والاتزان، الكيمياء الكهربائية، والعضوية.",
                sortOrder = 3
            ),
            SubjectEntity(
                id = 4,
                name = "الأحياء",
                iconName = "biology",
                track = "SCIENTIFIC",
                description = "التكاثر في الإنسان والنبات، الوراثة المندلية والجزيئية (DNA/RNA)، والتنظيم العصبي.",
                sortOrder = 4
            ),
            SubjectEntity(
                id = 5,
                name = "اللغة العربية",
                iconName = "arabic",
                track = "BOTH",
                description = "النحو والصرف (إعراب الجمل، الاستثناء، التمييز، الحال)، البلاغة، والأدب والنصوص.",
                sortOrder = 5
            ),
            SubjectEntity(
                id = 6,
                name = "اللغة الإنجليزية",
                iconName = "english",
                track = "BOTH",
                description = "Grammar rules, Conditionals, Passive Voice, Reported Speech, and Comprehension.",
                sortOrder = 6
            ),
            SubjectEntity(
                id = 7,
                name = "التربية الإسلامية",
                iconName = "islamic",
                track = "BOTH",
                description = "القرآن الكريم، الحديث النبوي، الفقه وأحكام المعاملات والميراث، والسيرة النبوية.",
                sortOrder = 7
            ),
            SubjectEntity(
                id = 8,
                name = "التاريخ",
                iconName = "history",
                track = "LITERARY",
                description = "تاريخ اليمن الحديث والمعاصر، حركات التحرر، ثورة 26 سبتمبر و 14 أكتوبر، وتحقيق الوحدة.",
                sortOrder = 8
            ),
            SubjectEntity(
                id = 9,
                name = "الجغرافيا",
                iconName = "geography",
                track = "LITERARY",
                description = "جغرافية الجمهورية اليمنية: الموقع والمظاهر التضاريسية والمناخية والموارد الطبيعية.",
                sortOrder = 9
            )
        )
        currDao.insertSubjects(subjects)

        // Seed Units
        val otherUnits = listOf(
            // الفيزياء
            UnitEntity(id = 4, subjectId = 2, title = "الوحدة الأولى: التيار المتردد والدوائر المهتزة", sortOrder = 1),
            UnitEntity(id = 5, subjectId = 2, title = "الوحدة الثانية: الفيزياء الذرية والإشعاع", sortOrder = 2),

            // اللغة العربية
            UnitEntity(id = 8, subjectId = 5, title = "الوحدة الأولى: قواعد النحو (أسلوب الاستثناء والتمييز)", sortOrder = 1),

            // الإنجليزي
            UnitEntity(id = 9, subjectId = 6, title = "Unit 1: Conditional Clauses & Reported Speech", sortOrder = 1)
        )
        currDao.deleteLegacyUnits(1, 100)
        currDao.deleteLegacyUnits(3, 100)
        currDao.deleteLegacyLessons(1, 1000)
        currDao.deleteLegacyLessons(3, 1000)
        currDao.insertUnits(
            otherUnits + 
            MathUnits1to3.getUnits() + 
            MathUnits4to7.getUnits() +
            ChemistryUnits1to3.getUnits() +
            ChemistryUnits4to6.getUnits() +
            ChemistryUnits7to9.getUnits()
        )

        // Seed Chapters
        val chapters = listOf(
            ChapterEntity(id = 4, unitId = 4, subjectId = 2, title = "الفصل الأول: المولد الكهربائي والتيار الجيبي", sortOrder = 1),
            ChapterEntity(id = 5, unitId = 5, subjectId = 2, title = "الفصل الأول: التأثير الكهروضوئي", sortOrder = 1),
            ChapterEntity(id = 7, unitId = 8, subjectId = 5, title = "الفصل الأول: المستثنى بإلا وأخواتها", sortOrder = 1),
            ChapterEntity(id = 8, unitId = 9, subjectId = 6, title = "Chapter 1: If-Conditionals (Types 0, 1, 2, 3)", sortOrder = 1)
        )
        currDao.insertChapters(chapters)

        // Seed Lessons
        val lessons = listOf(
            LessonEntity(
                id = 3,
                chapterId = 5,
                unitId = 5,
                subjectId = 2,
                title = "ظاهرة التأثير الكهروضوئي ومعادلة أينشتاين",
                coreIdea = "انبعاث إلكترونات من سطح فلز عند سقوط ضوء ذي تردد مناسب أكبر من أو يساوي تردد العتبة.",
                simplifiedExplanation = """
ما هو التأثير الكهروضوئي؟
عندما يسقط شعاع ضوئي (فوتونات) على لوح معدني (مثل الخارصين أو السيزيوم)، تنطلق منه إلكترونات تسمى (إلكترونات ضوئية).

الشروط الوزارية الهامة:
1. تردد الضوء الساقط (د) يجب أن يكون أكبر من أو يساوي تردد العتبة للمعدن (د.).
2. طاقة الفوتون الساقط (ط) = هـ × د (حيث هـ ثابت بلانك).
3. دالة الشغل للمعدن (هـ د.) هي أقل طاقة تكفي لتحرير الإلكترون دون إكسابه طاقة حركة.

معادلة أينشتاين الكهروضوئية:
طاقة الفوتون الساقط = دالة الشغل + طاقة الحركة العظمى للإلكترون
ط = دالة الشغل + ط ح
هـ د = هـ د. + ½ ك ع²
                """.trimIndent(),
                easierExplanation = """
تشبيه مبسط جداً:
تخيل أن الإلكترون محبوس في غرفة ومغلق عليه الباب بقفل يكلف فتحه 5 ريالات (دالة الشغل).
أنت ألقيت عليه فوتوناً يحتوي 8 ريالات (طاقة الفوتون).
الإلكترون سيستخدم 5 ريالات لفتح الباب والتحرر، والمتبقي معه (3 ريالات) سيجري بها في الهواء كطاقة حركة!
لو ألقيت عليه 4 ريالات فقط، لن يفتح الباب نهائياً مهما كررت المحاولة ومهما زادت شدة الضوء!
                """.trimIndent(),
                keyPoints = "تردد العتبة (د.): أقل تردد يحرر الإلكترون من سطح الفلز دون إكسابه طاقة حركة\nدالة الشغل (هـ د.): خاصة بنوع مادة الفلز ولا تتغير بتغير الضوء الساقط\nزيادة شدة الضوء تزيد من عدد الإلكترونات المنبعثة ولا تزيد طاقتها الحركية\nزيادة تردد الضوء تزيد من طاقة حركة الإلكترونات المنبعثة",
                formulas = "ط = هـ × د\nدالة الشغل (هـ د.) = هـ × د.\nط ح (العظمى) = هـ (د - د.) = ½ ك ع²\nجهد الإيقاف: ط ح = ش. × جـ ق",
                solvedExample = """
المسألة:
سقط ضوء تردده 8 × 10¹⁴ هيرتز على سطح فلز دالة شغله 3.3 × 10⁻¹⁹ جول.
(علماً بأن ثابت بلانك هـ = 6.6 × 10⁻³⁴ جول.ثانية)
المطلوب:
1) طاقة الفوتون الساقط.
2) طاقة الحركة العظمى للإلكترونات المتحررة.

الحل بالخطوات:
المعطيات:
د = 8 × 10¹⁴ هيرتز
دالة الشغل = 3.3 × 10⁻¹⁹ جول
هـ = 6.6 × 10⁻³⁴ جول.ثانية

1) طاقة الفوتون = هـ × د:
ط = 6.6 × 10⁻³⁴ × 8 × 10¹⁴ = 5.28 × 10⁻¹⁹ جول.

2) طاقة الحركة = طاقة الفوتون - دالة الشغل:
ط ح = 5.28 × 10⁻¹⁹ - 3.3 × 10⁻¹⁹ = 1.98 × 10⁻¹⁹ جول.
الإجابة النهائية: طاقة الفوتون = 5.28 × 10⁻¹⁹ جول، طاقة الحركة = 1.98 × 10⁻¹⁹ جول.
                """.trimIndent(),
                practiceExercise = """
تدريب وزاري:
علل: لا تنبعث إلكترونات كهروضوئية إذا كان تردد الضوء الساقط أقل من تردد العتبة، مهما بلغت شدة الضوء؟

الإجابة والسبب:
لأن التأثير الكهروضوئي يعتمد على تصادم فوتون واحد مع إلكترون واحد. فإذا كانت طاقة الفوتون الواحد أقل من دالة الشغل اللازمة لتحرير الإلكترون، فلا يمكن للإلكترون أن يتحرر، وزيادة الشدة تزيد فقط من عدد الفوتونات الضعيفة دون زيادة طاقة أي فوتون منها.
                """.trimIndent(),
                isPremium = false,
                sortOrder = 3
            ),

            LessonEntity(
                id = 5,
                chapterId = 7,
                unitId = 8,
                subjectId = 5,
                title = "أسلوب الاستثناء وإعراب المستثنى بـ (إلا)",
                coreIdea = "إخراج ما بعد أداة الاستثناء من حكم ما قبلها، وله ثلاثة أنواع تحدد إعراب المستثنى.",
                simplifiedExplanation = """
أركان أسلوب الاستثناء:
1. المستثنى منه: يقع قبل الأداة ويعرب حسب موقعه في الجملة.
2. أداة الاستثناء: (إلا، غير، سوى، خلا، عدا، حاشا).
3. المستثنى: الاسم الواقع بعد الأداة.

أنواع كلام الاستثناء في المنهج الوزاري:
النوع 1: تام مثبت (تام يعني المستثنى منه موجود، مثبت يعني غير منفي).
الحكم: واجب النصب على الاستثناء.
مثال: "حضر الطلابُ إلا طالباً".
طالباً: مستثنى منصوب وعلامة نصبه الفتحة.

النوع 2: تام منفي (المستثنى منه موجود والجملة سبقت بنفي أو نهي).
الحكم: جواز النصب على الاستثناء أو الاتباع على البدلية من المستثنى منه.
مثال: "ما حضر الطلابُ إلا طالباً / طالبٌ".

النوع 3: ناقص منفي (المستثنى منه محذوف والجملة منفية).
الحكم: يعرب ما بعد إلا حسب موقعه في الجملة كأن (ما) و (إلا) غير موجودتين.
مثال: "ما محمدٌ إلا رسولٌ".
رسولٌ: خبر مرفوع وعلامة رفعه الضمة.
                """.trimIndent(),
                easierExplanation = """
سر سحري وسهل جداً لمعرفة نوع الاستثناء:
احذف النفي (ما / لا / لم) واحذف (إلا) واقرأ الجملة:
إذا استقام المعنى تماماً، فالنوع (ناقص منفي) وما بعد إلا يعرب حسب موقعه!
مثال: "ما فاز إلا المجتهدُ" ← احذف "ما" و "إلا" فتصبح: "فاز المجتهدُ" ← فاعل مرفوع!
أما إذا اختل المعنى مثل: "ما غاب الطلاب إلا طالباً" ← "غاب الطلاب طالباً" لا تستقيم، فالنوع (تام منفي)!
                """.trimIndent(),
                keyPoints = "التام المثبت: واجب النصب دائماً\nالتام المنفي: يجوز النصب على الاستثناء أو البدل\nالناقص المنفي: يعرب حسب موقعه في الجملة\nغير وسوى تأخذان حكم إعراب ما بعد إلا، والاسم بعدهما مضاف إليه مجرور دائماً",
                formulas = "تام مثبت = واجب النصب\nتام منفي = (جائز النصب) أو (بدل من المستثنى منه)\nناقص منفي = حسب موقعه في الجملة",
                solvedExample = """
أعرب ما تحته خط:
"لا ينفعُ الإنسانَ في شدته إلا العملُ الصالحُ"

خطوات الحل:
الخطوة 1: نحدد نوع الاستثناء.
الجملة منفية بـ (لا). هل المستثنى منه موجود؟ لا.
الخطوة 2: نجرب الحذف الذهني لـ (لا) و (إلا):
"ينفعُ الإنسانَ في شدته العملُ الصالحُ"
المعنى مستقيم تماماً، إذن النوع (ناقص منفي).
الخطوة 3: الإعراب:
ينفعُ: فعل مضارع مرفوع بالضمة.
الإنسانَ: مفعول به مقدم منصوب بالفتحة.
العملُ: فاعل مؤخر مرفوع وعلامة رفعه الضمة الظاهرة على آخره.
الصالحُ: نعت مرفوع بالضمة.
                """.trimIndent(),
                practiceExercise = """
تدريب:
حول الاستثناء التام المثبت التالي إلى ناقص منفي: "قرأتُ الكتبَ إلا كتاباً"

الحل:
نحذف المستثنى منه (الكتب) وننفي الجملة:
"ما قرأتُ إلا كتاباً".
إعراب كتاباً الآن: مفعول به منصوب للفعل قرأ.
                """.trimIndent(),
                isPremium = false,
                sortOrder = 5
            )
        )
        currDao.insertLessons(
            lessons + 
            MathUnits1to3.getLessons() + 
            MathUnits4to7.getLessons() +
            ChemistryUnits1to3.getLessons() +
            ChemistryUnits4to6.getLessons() +
            ChemistryUnits7to9.getLessons()
        )

        // Seed Questions for Question Bank & Exams
        val questionDao = db.questionDao()
        questionDao.deleteLegacyQuestions(1, 1000)
        questionDao.deleteLegacyQuestions(3, 1000)
        val questions = listOf(
            QuestionEntity(
                id = 3,
                subjectId = 2,
                unitId = 5,
                lessonId = 3,
                questionType = "TRUE_FALSE",
                questionText = "تزداد الطاقة الحركية العظمى للإلكترونات الكهروضوئية المنبعثة بزيادة شدة الضوء الساقط.",
                options = "صح||خطأ",
                correctAnswer = "خطأ",
                explanation = "خطأ، لأن الطاقة الحركية تعتمد على تردد الضوء الساقط وليس على شدته؛ زيادة الشدة تزيد عدد الإلكترونات المنبعثة فقط.",
                difficulty = "MEDIUM",
                isPremium = false
            ),
            QuestionEntity(
                id = 5,
                subjectId = 5,
                unitId = 8,
                lessonId = 5,
                questionType = "MCQ",
                questionText = "في جملة (ما جاء إلا عليٌ)، إعراب كلمة (عليٌ) هو:",
                options = "فاعل مرفوع||مستثنى منصوب||بدل مرفوع||مفعول به",
                correctAnswer = "فاعل مرفوع",
                explanation = "الاستثناء ناقص منفي، بحذف ما وإلا تصبح الجملة: جاء عليٌ، إذن عليٌ فاعل مرفوع بالضمة.",
                difficulty = "MEDIUM",
                isPremium = false
            ),
            QuestionEntity(
                id = 7,
                subjectId = 2,
                unitId = 5,
                lessonId = 3,
                questionType = "MCQ",
                questionText = "أقل تردد يكفي لتحرير الإلكترونات من سطح الفلز يسمى:",
                options = "تردد العتبة||تردد الرنين||تردد الإشعاع||التردد الطبيعي",
                correctAnswer = "تردد العتبة",
                explanation = "تردد العتبة (د.) هو التردد الحرج الذي تكون طاقته مساوية لدالة شغل سطح الفلز تماماً.",
                difficulty = "EASY",
                isPremium = false
            ),
            QuestionEntity(
                id = 8,
                subjectId = 5,
                unitId = 8,
                lessonId = 5,
                questionType = "TRUE_FALSE",
                questionText = "في الاستثناء التام المثبت، يجب نصب المستثنى الواقع بعد إلا.",
                options = "صح||خطأ",
                correctAnswer = "صح",
                explanation = "صح، حكم المستثنى في الكلام التام المثبت هو وجوب النصب على الاستثناء دائماً.",
                difficulty = "EASY",
                isPremium = false
            )
        )
        questionDao.insertQuestions(
            questions + 
            MathQuestionsAndExams.getUnitQuestions() +
            ChemistryQuestionsAndExams.getUnitQuestions()
        )

        // Seed Exams
        val examDao = db.examDao()
        examDao.deleteLegacyExams(1, 100)
        examDao.deleteLegacyExams(3, 100)
        val exams = listOf(
            ExamEntity(
                id = 2,
                subjectId = 2,
                title = "اختبار الفيزياء الحديثة والتأثير الكهروضوئي",
                durationMinutes = 20,
                totalQuestions = 5,
                passingScore = 60,
                isPremium = false
            ),
            ExamEntity(
                id = 4,
                subjectId = 5,
                title = "اختبار النحو: أساليب الاستثناء الشامل",
                durationMinutes = 15,
                totalQuestions = 5,
                passingScore = 50,
                isPremium = false
            )
        )
        examDao.insertExams(
            exams + 
            MathQuestionsAndExams.getUnitExams() +
            ChemistryQuestionsAndExams.getUnitExams()
        )

        // Seed Notification
        val notifDao = db.notificationDao()
        notifDao.insertNotification(
            NotificationEntity(
                title = "أهلاً بك في تطبيق رقيب للتعليم الثانوي! 🇾🇪",
                message = "نتمنى لجميع طلابنا الأعزاء التوفيق والدرجات العالية في اختبارات الشهادة الثانوية العامة. لا تتردد في استخدام المعلم الذكي والتدريب اليومي.",
                targetType = "ALL",
                isRead = false
            )
        )

        // Seed initial activity log
        val logDao = db.activityLogDao()
        logDao.insertLog(
            ActivityLogEntity(
                actionType = "INITIAL_SETUP",
                adminName = "النظام",
                studentName = null,
                details = "تمت تهيئة قاعدة بيانات المنهج اليمني للثالث الثانوي بنجاح وتفعيل الحسابات الأساسية."
            )
        )
    }
}
