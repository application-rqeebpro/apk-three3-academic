package com.example.data.local

import com.example.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        // 1. Ensure Admin & Demo student exist
        val existingAdmin = db.userDao().getUserByPhoneOrEmail("782916997")
        if (existingAdmin == null) {
            // Seed Admin (المدير العام)
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
            db.userDao().insertUser(
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
        }

        // 2. Ensure Subjects exist
        val existingSubjects = db.curriculumDao().getAllSubjects()
        // We insert default subjects if empty
        val subjectsList = listOf(
            SubjectEntity(
                name = "الرياضيات",
                description = "الجبر والهندسة، الأعداد المركبة، النهايات والتفاضل، والتكامل",
                iconRes = "📐",
                colorHex = "#0044B3",
                track = "علمي"
            ),
            SubjectEntity(
                name = "الكيمياء",
                description = "الكيمياء العضوية، الكيمياء الحرارية، والاتزان الكيميائي",
                iconRes = "🧪",
                colorHex = "#0D9488",
                track = "علمي"
            ),
            SubjectEntity(
                name = "الفيزياء",
                description = "الفيزياء الذرية، الإلكترونيات، والمغناطيسية والكهرباء",
                iconRes = "⚡",
                colorHex = "#D97706",
                track = "علمي"
            ),
            SubjectEntity(
                name = "اللغة العربية",
                description = "النحو والصرف، البلاغة والنقد، والأدب والنصوص",
                iconRes = "📖",
                colorHex = "#7C3AED",
                track = "مشترك"
            ),
            SubjectEntity(
                name = "اللغة الإنجليزية",
                description = "English for Yemen Book 6, Grammar, Vocabulary, Reading Comprehension",
                iconRes = "🌐",
                colorHex = "#2563EB",
                track = "مشترك"
            ),
            SubjectEntity(
                name = "القرآن الكريم والتربية الإسلامية",
                description = "علوم القرآن، الحديث النبوي، الفقه الإسلامي، والسيرة",
                iconRes = "🕌",
                colorHex = "#059669",
                track = "مشترك"
            )
        )

        // Seed default units and lessons
        val mathSubjectId = 1L
        val chemistrySubjectId = 2L
        val physicsSubjectId = 3L

        db.curriculumDao().insertSubjects(subjectsList)

        // Math Units
        val mathUnits = listOf(
            UnitEntity(
                id = 1L,
                subjectId = mathSubjectId,
                unitNumber = 1,
                title = "الوحدة الأولى: الأعداد المركبة",
                description = "التعريف، العمليات الجبرية، الصورة القطبية، ومبرهنة ديموافر والجذور النونية"
            ),
            UnitEntity(
                id = 2L,
                subjectId = mathSubjectId,
                unitNumber = 2,
                title = "الوحدة الثانية: مبدأ العد ونظرية ذات الحدين",
                description = "التباديل والتوافيق، خواص التوافيق، ومفكوك ذات الحدين"
            ),
            UnitEntity(
                id = 3L,
                subjectId = mathSubjectId,
                unitNumber = 3,
                title = "الوحدة الثالثة: النهايات والاتصال",
                description = "نهايات الدوال المثلثية، الاتصال، وقاعدة لوبيتال"
            )
        )
        db.curriculumDao().insertUnits(mathUnits)

        // Math Lessons
        val mathLessons = listOf(
            LessonEntity(
                unitId = 1L,
                subjectId = mathSubjectId,
                lessonNumber = 1,
                title = "الصورة القطبية ومبرهنة ديموافر",
                coreIdea = "تحويل العدد المركب ع = س + ت ص إلى صورته المثلثية ع = ر (جتا هـ + ت جا هـ) وتطبيق مبرهنة ديموافر لحساب الأسس.",
                simplifiedExplanation = "المقياس ر = √(س² + ص²)، والسعة هـ = ظا⁻¹(ص ÷ س). تنص مبرهنة ديموافر على أن: [ر (جتا هـ + ت جا هـ)]ⁿ = رⁿ (جتا ن هـ + ت جا ن هـ).",
                formulas = "ر = √(س² + ص²)\nهـ = ظا⁻¹(ص ÷ س)\nعⁿ = رⁿ [جتا(ن هـ) + ت جا(ن هـ)]",
                symbolsExplanation = "ر: مقياس العدد المركب (المعيار)\nهـ: السعة الأساسية (الزاوية بالدرجات أو الراديان)\nت: الوحدة التخيلية ت² = -1",
                keyPoints = "✓ انتبه لإشارات س و ص لتحديد الربع الصحيح للسعة هـ\n✓ تذكر أن جتا(-هـ) = جتا(هـ) بينما جا(-هـ) = -جا(هـ)\n✓ عند رفع الزاوية لأس، نضرب الزاوية في ن بينما نرفع المقياس للأس ن",
                solvedExample = "المسألة: اكتب العدد ع = 1 + ت بالصورة القطبية، ثم احسب ع⁴.\nالحل:\n1) س = 1 ، ص = 1\n2) ر = √(1² + 1²) = √2\n3) ظا هـ = 1 ÷ 1 = 1 وبما أن س > 0 و ص > 0 فإن هـ = 45° (π/4)\n4) الصورة القطبية: ع = √2 (جتا 45° + ت جا 45°)\n5) بتطبيق ديموافر: ع⁴ = (√2)⁴ [جتا(4 × 45°) + ت جا(4 × 45°)] = 4 [جتا 180° + ت جا 180°] = 4 (-1 + 0) = -4.",
                practiceExercise = "تدريب: اكتب العدد المركب ع = √3 + ت بالصورة المثلثية واحسب ع⁶.\nالجواب النهائي: ع⁶ = -64.",
                easierExplanation = "تخيّل العدد المركب كنقطة في المستوى، المقياس هو بعدها عن المركز، والسعة هي الزاوية مع المحور الأفقي. مبرهنة ديموافر تختصر الضرب المتكرر إلى ضرب في الزاوية فقط!"
            ),
            LessonEntity(
                unitId = 1L,
                subjectId = mathSubjectId,
                lessonNumber = 2,
                title = "الجذور النونية للعدد المركب",
                coreIdea = "إيجاد الجذور المختلفة للعدد المركب باستخدام الصيغة الوزارية ع^(1/ن) = ر^(1/ن) [جتا((هـ + 360° ك)/ن) + ت جا((هـ + 360° ك)/ن)].",
                simplifiedExplanation = "لكل عدد مركب ن جذر مختلف، يتم الحصول عليها بتعويض ك = 0 ، 1 ، 2 ، ... ، ن - 1.",
                formulas = "الجذور النونية = ر^(1/ن) [جتا((هـ + 2ك π)/ن) + ت جا((هـ + 2ك π)/ن)] حيث ك = 0، 1، ...، ن-1",
                keyPoints = "✓ مجموع الجذور النونية لأي عدد مركب يساوي صفراً\n✓ الجذور النونية تمثل رؤوس مضلع منتظم عدد أضلاعه ن مرسوم في دائرة نصف قطرها ر^(1/ن)",
                solvedExample = "أوجد الجذور التربيعية للعدد ع = 4 (جتا 60° + ت جا 60°).\nالحل:\nر = 4 ، هـ = 60° ، ن = 2\nالجذر الأول (ك=0): √4 [جتا(60÷2) + ت جا(60÷2)] = 2 (جتا 30° + ت جا 30°) = √3 + ت.\nالجذر الثاني (ك=1): 2 [جتا((60+360)÷2) + ت جا(420÷2)] = 2 (جتا 210° + ت جا 210°) = -√3 - ت.",
                practiceExercise = "أوجد الجذور التكعيبية للعدد ع = 8 ت.",
                easierExplanation = "الجذور توزع الزاوية بالتساوي على دائرة كاملة مقسمة بعدد ن من الأقسام!"
            )
        )
        db.curriculumDao().insertLessons(mathLessons)

        // Seed Sample Documents (ملازم PDF و Word)
        val sampleBooklets = listOf(
            DocumentBookletEntity(
                title = "ملزمة الأعداد المركبة الوزارية الشاملة",
                subjectId = mathSubjectId,
                subjectName = "الرياضيات",
                grade = "الصف الثالث الثانوي",
                description = "ملزمة كاملة تضم شرح الصورة القطبية ومبرهنة ديموافر والجذور النونية مع أكثر من 40 مسألة وزارية محلولة.",
                fileType = "PDF",
                fileName = "ملزمة_الأعداد_المركبة_الوزارية.pdf",
                fileSize = "2.4 MB"
            ),
            DocumentBookletEntity(
                title = "كراسة قوانين ومعادلات الكيمياء الحرارية والعضوية",
                subjectId = chemistrySubjectId,
                subjectName = "الكيمياء",
                grade = "الصف الثالث الثانوي",
                description = "ملخص وزاري مركز وشامل لجميع قوانين الكيمياء الحرارية ومخططات التفاعلات العضوية.",
                fileType = "PDF",
                fileName = "كراسة_قوانين_الكيمياء.pdf",
                fileSize = "1.8 MB"
            ),
            DocumentBookletEntity(
                title = "بنك الأسئلة الوزارية الشامل - مادة الفيزياء (ملف Word قابل للتعديل)",
                subjectId = physicsSubjectId,
                subjectName = "الفيزياء",
                grade = "الصف الثالث الثانوي",
                description = "نماذج اختبارات وزارية من عام 2018 حتى 2024 بصيغة Word للمراجعة والطباعة.",
                fileType = "WORD",
                fileName = "بنك_أسئلة_الفيزياء_الوزاري.docx",
                fileSize = "3.1 MB"
            )
        )
        for (b in sampleBooklets) {
            db.documentBookletDao().insertBooklet(b)
        }

        // Seed Sample Subscription Codes
        val existingCode = db.subscriptionDao().getCode("RAQEEB-1M")
        if (existingCode == null) {
            val defaultCodes = listOf(
                SubscriptionCodeEntity(
                    code = "RAQEEB-1M",
                    planName = "باقة شهر واحد (5000 ريال)",
                    durationDays = 30,
                    isUsed = false
                ),
                SubscriptionCodeEntity(
                    code = "RAQEEB-3M",
                    planName = "باقة ثلاثة أشهر (12000 ريال)",
                    durationDays = 90,
                    isUsed = false
                ),
                SubscriptionCodeEntity(
                    code = "RAQEEB-6M",
                    planName = "باقة ستة أشهر (24000 ريال)",
                    durationDays = 180,
                    isUsed = false
                ),
                SubscriptionCodeEntity(
                    code = "RAQEEB-8M",
                    planName = "باقة ثمانية أشهر (30000 ريال)",
                    durationDays = 240,
                    isUsed = false
                ),
                SubscriptionCodeEntity(
                    code = "YEMEN2025",
                    planName = "باقة ثلاثة أشهر (12000 ريال)",
                    durationDays = 90,
                    isUsed = false
                ),
                SubscriptionCodeEntity(
                    code = "VIP2025",
                    planName = "باقة ثمانية أشهر (30000 ريال)",
                    durationDays = 240,
                    isUsed = false
                )
            )
            db.subscriptionDao().insertCodes(defaultCodes)
        }
    }
}
