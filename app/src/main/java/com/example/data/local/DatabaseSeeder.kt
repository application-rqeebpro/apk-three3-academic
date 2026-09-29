package com.example.data.local

import com.example.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        // Seed users if no user exists
        val existingUsers = db.userDao().getUserByPhoneOrEmail("782916997")
        if (existingUsers == null) {
            // Seed Admin
            db.userDao().insertUser(
                UserEntity(
                    fullName = "المدير العام للأكاديمية",
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

            // Student account is created WITHOUT pre-activated subscription
            // The student must activate using an activation code or pay via wallets
        }

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
        val units = listOf(
            // الرياضيات
            UnitEntity(id = 1, subjectId = 1, title = "الوحدة الأولى: التفاضل وتطبيقاته", sortOrder = 1),
            UnitEntity(id = 2, subjectId = 1, title = "الوحدة الثانية: الأعداد المركبة", sortOrder = 2),
            UnitEntity(id = 3, subjectId = 1, title = "الوحدة الثالثة: التكامل وتطبيقاته", sortOrder = 3),

            // الفيزياء
            UnitEntity(id = 4, subjectId = 2, title = "الوحدة الأولى: التيار المتردد والدوائر المهتزة", sortOrder = 1),
            UnitEntity(id = 5, subjectId = 2, title = "الوحدة الثانية: الفيزياء الذرية والإشعاع", sortOrder = 2),

            // الكيمياء
            UnitEntity(id = 6, subjectId = 3, title = "الوحدة الأولى: الكيمياء الحرارية وقانون هس", sortOrder = 1),
            UnitEntity(id = 7, subjectId = 3, title = "الوحدة الثانية: الاتزان الكيميائي وسرعة التفاعل", sortOrder = 2),

            // اللغة العربية
            UnitEntity(id = 8, subjectId = 5, title = "الوحدة الأولى: قواعد النحو (أسلوب الاستثناء والتمييز)", sortOrder = 1),

            // الإنجليزي
            UnitEntity(id = 9, subjectId = 6, title = "Unit 1: Conditional Clauses & Reported Speech", sortOrder = 1)
        )
        currDao.insertUnits(units)

        // Seed Chapters
        val chapters = listOf(
            ChapterEntity(id = 1, unitId = 1, subjectId = 1, title = "الفصل الأول: نهايات الدوال الدائرية", sortOrder = 1),
            ChapterEntity(id = 2, unitId = 1, subjectId = 1, title = "الفصل الثاني: مشتقات الدوال المثلثية والضمنية", sortOrder = 2),
            ChapterEntity(id = 3, unitId = 2, subjectId = 1, title = "الفصل الأول: الصورة الجبرية والعمليات عليها", sortOrder = 1),
            ChapterEntity(id = 4, unitId = 4, subjectId = 2, title = "الفصل الأول: المولد الكهربائي والتيار الجيبي", sortOrder = 1),
            ChapterEntity(id = 5, unitId = 5, subjectId = 2, title = "الفصل الأول: التأثير الكهروضوئي", sortOrder = 1),
            ChapterEntity(id = 6, unitId = 6, subjectId = 3, title = "الفصل الأول: المحتوى الحراري وقانون هس", sortOrder = 1),
            ChapterEntity(id = 7, unitId = 8, subjectId = 5, title = "الفصل الأول: المستثنى بإلا وأخواتها", sortOrder = 1),
            ChapterEntity(id = 8, unitId = 9, subjectId = 6, title = "Chapter 1: If-Conditionals (Types 0, 1, 2, 3)", sortOrder = 1)
        )
        currDao.insertChapters(chapters)

        // Seed Lessons
        val lessons = listOf(
            LessonEntity(
                id = 1,
                chapterId = 1,
                unitId = 1,
                subjectId = 1,
                title = "نهاية الدوال الدائرية الأساسية (جا س / س)",
                coreIdea = "نهاية النسبة بين جيب الزاوية وقيمة الزاوية بالراديان عندما تقترب الزاوية من الصفر تساوي 1.",
                simplifiedExplanation = """
مرحباً يا بطل الصف الثالث الثانوي!
تخيل أن لديك زاوية صغيرة جداً جداً مقاسة بالراديان اسمها (س).
عندما تقترب هذه الزاوية من الصفر (س → 0):
تصبح قيمة (جا س) متقاربة جداً جداً مع قيمة الزاوية نفسها (س).
لذلك فإن:
نهــــا (جا س / س) عندما س → 0 = 1.
وكذلك:
نهــــا (ظا س / س) عندما س → 0 = 1.

تذكر دائماً الشروط الوزارية الثلاثة لتطبيق هذه القاعدة:
1) النهاية تسعى للصفر (س → 0).
2) المعامل داخل الجيب هو نفس المقام تماماً.
3) قياس الزاوية بالراديان.
                """.trimIndent(),
                easierExplanation = """
ببساطة شديدة وكأنك أمام السبورة مع أستاذك:
تخيل أنك تمشي نحو نقطة معينة، كلما اقتربت أكثر، المسافة بين خطوتك (س) وانحناءة خطوتك (جا س) تتطابق تماماً!
فإذا قسمت شيئين متطابقين على بعضهما في تلك اللحظة بالذات، تكون النتيجة دائماً (1).
لو جاءتك المسألة: نهـا (جا 5س / س)، فكر فوراً: ما الذي ينقص المقام ليكون مثل البسط؟
ينقصه 5! نضرب البسط والمقام في 5، فيصبح الناتج مباشرة 5 × 1 = 5!
                """.trimIndent(),
                keyPoints = "نهــــا (جا س / س) عندما س → 0 تساوي 1\nنهــــا (ظا س / س) عندما س → 0 تساوي 1\nنهــــا (1 - جتا س) / س عندما س → 0 تساوي 0\nإذا كان معامل الزاوية أ، فإن نهـا (جا أ س / ب س) = أ / ب",
                formulas = "القانون الرئيسي: نهـــا (جا س / س) = 1 (عندما س → 0)\nالتعميم: نهـــا (جا أ س / ب س) = أ / ب\nقانون الظل: نهـــا (ظا أ س / ب س) = أ / ب",
                solvedExample = """
المسألة:
أوجد نهــــا (جا 7س / 3س) عندما س → 0

خطوات الحل الوزاري النموذجي:
الخطوة 1: نلاحظ أن الزاوية في البسط هي (7س) بينما في المقام (3س).
الخطوة 2: نخرج الثابت (1/3) خارج النهاية:
= (1/3) × نهــــا (جا 7س / س)
الخطوة 3: نضرب المقام في 7 والبسط في 7 لتطابق الزاوية مع المقام:
= (7/3) × نهــــا (جا 7س / 7س)
الخطوة 4: بما أن نهــا (جا 7س / 7س) = 1، فإن:
الناتج = (7/3) × 1 = 7/3
الإجابة النهائية: 7/3
                """.trimIndent(),
                practiceExercise = """
تدريب وزاري:
أوجد قيمة: نهــــا (ظا 4س / جا 2س) عندما س → 0

طريقة الحل:
نقسم كلاً من البسط والمقام على (س):
البسط: نهـا (ظا 4س / س) = 4
المقام: نهـا (جا 2س / س) = 2
إذن النتيجة = 4 / 2 = 2.
سبب الإجابة: قسمة البسط والمقام على س تؤدي إلى ظهور صورتين قياسيتين للنهاية الدائرية.
                """.trimIndent(),
                isPremium = false,
                sortOrder = 1
            ),
            LessonEntity(
                id = 2,
                chapterId = 3,
                unitId = 2,
                subjectId = 1,
                title = "العدد المركب وصورته الجبرية (ت، ع = س + ص ت)",
                coreIdea = "العدد المركب هو عدد مكون من جزأين: جزء حقيقي (س) وجزء تخيلي (ص ت) حيث ت² = -1.",
                simplifiedExplanation = """
في الأعداد الحقيقية، كنا نعجز عن إيجاد جذر تربيعي لعدد سالب مثل √(-1).
في منهج الثالث الثانوي، اخترع الرياضيون الوحدة التخيلية (ت) بحيث:
ت = √(-1)، وبالتالي: ت² = -1.

الصورة الجبرية للعدد المركب:
ع = س + ص ت
- س: يسمى الجزء الحقيقي ويرمز له حـ(ع).
- ص: يسمى الجزء التخيلي ويرمز له تـ(ع).

قوى الوحدة التخيلية (ت):
ت¹ = ت
ت² = -1
ت³ = -ت
ت⁴ = 1
لأي أس ن، نقسم على 4 ونأخذ باقي القسمة!
                """.trimIndent(),
                easierExplanation = """
تخيل أن العمليات الحسابية كانت تعيش في شقة أرضية لا يوجد بها حل لجذر السالب!
فجاء المهندسون وبنوا طابقاً علوياً أسموه "الأعداد المركبة".
كل عدد فيه يتكون من هويتين: رجل حقيقي يمشي على الأرض (س)، وظله التخيلي (ص ت).
وهما لا يمتزجان مباشرة، مثل الزيت والماء! تجمع الحقيقي مع الحقيقي، والتخيلي مع التخيلي.
                """.trimIndent(),
                keyPoints = "ت = √(-1) و ت² = -1 و ت⁴ = 1\nمرافق العدد ع = س + ص ت هو ع̄ = س - ص ت\nحاصل ضرب العدد في مرافقه: ع × ع̄ = س² + ص² (دائماً عدد حقيقي موجب)\nمقياس العدد المركب: |ع| = ر = √(س² + ص²)",
                formulas = "ع = س + ص ت\nالمقياس: ر = √(س² + ص²)\nالمرافق: ع̄ = س - ص ت\nحاصل ضرب المرافقين: ع × ع̄ = س² + ص²",
                solvedExample = """
المسألة:
إذا كان ع = 3 + 4ت، أوجد:
1) مرافق العدد ع̄
2) مقياس العدد |ع|
3) ناتج ع × ع̄

الحل خطوة بخطوة:
1) المرافق نعكس فيه إشارة الجزء التخيلي فقط:
ع̄ = 3 - 4ت

2) المقياس ر = √(س² + ص²):
ر = √(3² + 4²) = √(9 + 16) = √25 = 5

3) ناتج الضرب:
ع × ع̄ = 3² + 4² = 9 + 16 = 25
الإجابة النهائية: ع̄ = 3 - 4ت، |ع| = 5، ع × ع̄ = 25
                """.trimIndent(),
                practiceExercise = """
تدريب:
أوجد قيمة ت⁵³ في أبسط صورة.

طريقة الحل:
نقسم الأس 53 على 4:
53 ÷ 4 = 13 والباقي 1.
إذن: ت⁵³ = ت¹ = ت.
السبب: مضاعفات العدد 4 من قوى (ت) تعطي دائماً 1، ويبقى فقط باقي القسمة.
                """.trimIndent(),
                isPremium = false,
                sortOrder = 2
            ),
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
                id = 4,
                chapterId = 6,
                unitId = 6,
                subjectId = 3,
                title = "قانون هس في الكيمياء الحرارية والتغير في المحتوى الحراري",
                coreIdea = "حرارة التفاعل الكلية لتفاعل كيميائي تتوقف على طبيعة المواد المتفاعلة والناتجة فقط، وليس على الخطوات التي يمر بها التفاعل.",
                simplifiedExplanation = """
نص قانون هس الوزاري:
"التغير في الإنثالبي (ΔH) لأي تفاعل كيميائي هو مقدار ثابت سواء تم التفاعل في خطوة واحدة أو في سلسلة من الخطوات".

لماذا نلجأ إلى قانون هس؟
1. صعوبة قياس حرارة بعض التفاعلات مباشرة عملياً بسبب بطء التفاعل الشديد (مثل تكوّن الصدأ أو الجرافيت إلى ماس).
2. اختلاط التفاعل بتفاعلات جانبية غير مرغوبة.
3. خطورة التفاعل أو شدة سرعته.

قواعد التعامل مع المعادلات في قانون هس:
1. إذا عكست المعادلة، اعكس إشارة ΔH (من موجب لسالب أو العكس).
2. إذا ضربت المعادلة في معامل، اضرب قيمة ΔH في نفس المعامل.
3. إذا جمعت المعادلات، اجمع قيم ΔH جبرياً.
                """.trimIndent(),
                easierExplanation = """
تخيل أنك تريد السفر من صنعاء إلى عدن.
سواء سافرت مباشرة في خط مستقيم، أو توقفت في ذمار ثم إب ثم تعز ثم وصلت عدن:
فإن فرق الارتفاع الكلي بين نقطة البداية ونقطة النهاية هو نفسه لا يتغير!
هذا هو قانون هس: العبرة بالبداية والنهاية، والمسار بينهما لا يغير حرارة التفاعل الإجمالية.
                """.trimIndent(),
                keyPoints = "قانون هس هو تطبيق لقانون حفظ الطاقة (القانون الأول للديناميكا الحرارية)\nالتفاعل الطارد للحرارة: ΔH سالبة\nالتفاعل الماص للحرارة: ΔH موجبة\nمعاملات المعادلة تعامل كمقادير رياضية تضرب وتقسم وتعكس",
                formulas = "ΔH (الكلية) = ΔH₁ + ΔH₂ + ΔH₃ + ...\nΔH° = مجموع ΔH° (نواتج) - مجموع ΔH° (متفاعلات)",
                solvedExample = """
احسب التغير في الإنثالبي لتفاعل تكوّن أول أكسيد الكربون:
C (جرافيت) + ½ O₂ (غ) → CO (غ)    ΔH = ؟

بمعلومية المعادلتين:
1) C (جرافيت) + O₂ (غ) → CO₂ (غ)    ΔH₁ = -393.5 ك.جول
2) CO (غ) + ½ O₂ (غ) → CO₂ (غ)    ΔH₂ = -283.0 ك.جول

خطوات الحل:
الخطوة 1: نلاحظ أن CO مطلوب في النواتج وبمعامل 1، بينما هو في المعادلة (2) في المتفاعلات.
إذن نعكس المعادلة (2) ونعكس إشارة ΔH₂:
CO₂ (غ) → CO (غ) + ½ O₂ (غ)    ΔH = +283.0 ك.جول

الخطوة 2: نترك المعادلة (1) كما هي لأن الكربون جرافيت في المتفاعلات:
C (جرافيت) + O₂ (غ) → CO₂ (غ)    ΔH₁ = -393.5 ك.جول

الخطوة 3: نجمع المعادلتين جبرياً ونحذف المتشابهات (CO₂ و ½ O₂):
ينتج التفاعل المطلوب:
C (جرافيت) + ½ O₂ (غ) → CO (غ)

الخطوة 4: نجمع قيم الإنثالبي:
ΔH = -393.5 + 283.0 = -110.5 ك.جول/مول.
الإجابة النهائية: ΔH = -110.5 ك.جول/مول (تفاعل طارد للحرارة).
                """.trimIndent(),
                practiceExercise = """
تدريب:
إذا كان التفاعل: A + B → C له ΔH = +50 ك.جول. فما قيمة ΔH للتفاعل: 2C → 2A + 2B؟

طريقة الحل:
1) المعادلة عكست اتجاهها، إذن تتغير الإشارة من موجب إلى سالب (-50).
2) ضربت المعادلة في 2، إذن نضرب في 2: 2 × (-50) = -100 ك.جول.
الإجابة: -100 ك.جول.
                """.trimIndent(),
                isPremium = true,
                sortOrder = 4
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
        currDao.insertLessons(lessons)

        // Seed Questions for Question Bank & Exams
        val questionDao = db.questionDao()
        val questions = listOf(
            QuestionEntity(
                id = 1,
                subjectId = 1,
                unitId = 1,
                lessonId = 1,
                questionType = "MCQ",
                questionText = "قيمة النهاية: نهــــا (جا 3س / ظا 5س) عندما س → 0 تساوي:",
                options = "3/5||5/3||1||0",
                correctAnswer = "3/5",
                explanation = "بقسمة البسط والمقام على س، نحصل على نهـا (جا 3س / س) ÷ نهـا (ظا 5س / س) = 3 ÷ 5 = 3/5.",
                difficulty = "EASY",
                isPremium = false
            ),
            QuestionEntity(
                id = 2,
                subjectId = 1,
                unitId = 2,
                lessonId = 2,
                questionType = "MCQ",
                questionText = "إذا كان ع = 1 + ت، فإن مقياس العدد المركب |ع| يساوي:",
                options = "√2||2||1||0",
                correctAnswer = "√2",
                explanation = "المقياس ر = √(س² + ص²) = √(1² + 1²) = √2.",
                difficulty = "EASY",
                isPremium = false
            ),
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
                id = 4,
                subjectId = 3,
                unitId = 6,
                lessonId = 4,
                questionType = "MCQ",
                questionText = "ينص قانون هس على أن حرارة التفاعل تعتمد على:",
                options = "طبيعة المتفاعلات والنواتج فقط||الخطوات التي يمر بها التفاعل||سرعة التفاعل||درجة حرارة اللهب",
                correctAnswer = "طبيعة المتفاعلات والنواتج فقط",
                explanation = "قانون هس ينص على أن التغير في الإنثالبي يعتمد على الحالتين الابتدائية والنهائية للمواد وليس على المسار أو الخطوات.",
                difficulty = "EASY",
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
                id = 6,
                subjectId = 1,
                unitId = 1,
                lessonId = 1,
                questionType = "MCQ",
                questionText = "نهــــا (1 - جتا س) / س عندما س → 0 تساوي:",
                options = "0||1||غير معرفة||½",
                correctAnswer = "0",
                explanation = "هذه نهاية أساسية مبرهنة في كتاب الرياضيات الثالث الثانوي، وقيمتها تساوي صفراً.",
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
        questionDao.insertQuestions(questions)

        // Seed Exams
        val examDao = db.examDao()
        val exams = listOf(
            ExamEntity(
                id = 1,
                subjectId = 1,
                title = "اختبار التفاضل والأعداد المركبة التجريبي الأول",
                durationMinutes = 20,
                totalQuestions = 5,
                passingScore = 60,
                isPremium = false
            ),
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
                id = 3,
                subjectId = 3,
                title = "اختبار الكيمياء الحرارية وقانون هس",
                durationMinutes = 15,
                totalQuestions = 5,
                passingScore = 50,
                isPremium = true
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
        examDao.insertExams(exams)

        // Seed Notification
        val notifDao = db.notificationDao()
        notifDao.insertNotification(
            NotificationEntity(
                title = "أهلاً بك في أكاديمية الثالث الثانوي اليمني! 🇾🇪",
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
