package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class AppRepository(private val db: AppDatabase) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // ---------------------------------------------------------
    // AUTHENTICATION
    // ---------------------------------------------------------

    suspend fun login(phoneOrEmail: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserByPhoneOrEmail(phoneOrEmail.trim())
            ?: return@withContext Result.failure(Exception("لم يتم العثور على حساب بهذا الرقم أو البريد الإلكتروني"))

        if (user.isBlocked) {
            val reason = user.blockReason ?: "تم حظر هذا الرقم نظراً لتكرار محاولات التخمين الخاطئة لأكواد التفعيل."
            return@withContext Result.failure(Exception("⛔ هذا الحساب محظور:\n$reason\nيرجى مراجعة إدارة المنصة عبر الرقم 785502919."))
        }

        if (!user.isActive) {
            return@withContext Result.failure(Exception("تم تعطيل هذا الحساب من قبل الإدارة. يرجى التواصل مع الدعم."))
        }

        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("كلمة المرور غير صحيحة"))
        }

        _currentUser.value = user
        Result.success(user)
    }

    /**
     * التحقق الصارم من بيانات دخول مالك التطبيق والمدير العام الأربعة:
     * 1. البريد الإلكتروني: rqeebpro@gmail.com
     * 2. رقم الهاتف: 785502919
     * 3. كلمة المرور الأساسية: @#Abasabfarag1
     * 4. كلمة المرور السرية الإضافية
     */
    suspend fun loginOwner(
        email: String,
        phone: String,
        primaryPass: String,
        secretSecondaryPass: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanPrimary = primaryPass.trim()
        val cleanSecret = secretSecondaryPass.trim()

        val isEmailValid = cleanEmail == "rqeebpro@gmail.com"
        val isPhoneValid = cleanPhone == "785502919"
        val isPrimaryValid = cleanPrimary == "@#Abasabfarag1"
        val isSecretValid = cleanSecret == "RqeebSecret2025#" ||
            cleanSecret == "RaqeebOwner#Sec2026" ||
            cleanSecret == "RaqeebAdmin#2026@Security" ||
            cleanSecret == "RqSecret#2026Master" ||
            cleanSecret == "@#Abasabfarag1_SEC" ||
            cleanSecret == "785502919#Owner"

        if (!isEmailValid || !isPhoneValid || !isPrimaryValid || !isSecretValid) {
            // Log security failure
            val failedFields = mutableListOf<String>()
            if (!isEmailValid) failedFields.add("البريد الإلكتروني")
            if (!isPhoneValid) failedFields.add("رقم الهاتف")
            if (!isPrimaryValid) failedFields.add("كلمة المرور الأساسية")
            if (!isSecretValid) failedFields.add("كلمة المرور الإضافية السرية")

            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    adminName = "محاولة دخول غير مصرح بها للمالك",
                    actionType = "OWNER_AUTH_FAIL",
                    details = "فشل في مطابقة البيانات الأربعة: ${failedFields.joinToString(", ")} - الهدف: ${cleanPhone.ifEmpty { cleanEmail }}"
                )
            )
            return@withContext Result.failure(Exception("فشل التحقق: البيانات الأربعة المدخلة غير متطابقة مع حساب المالك المعتمد"))
        }

        // Retrieve or ensure owner entity exists with role ADMIN
        var adminUser = db.userDao().getUserByPhoneOrEmail("785502919")
            ?: db.userDao().getUserByPhoneOrEmail("rqeebpro@gmail.com")

        if (adminUser == null) {
            val newAdmin = UserEntity(
                fullName = "مالك التطبيق والمدير العام",
                phoneOrEmail = "785502919",
                passwordHash = "@#Abasabfarag1",
                role = "ADMIN",
                grade = "الإدارة العامة",
                track = "علمي",
                isActive = true
            )
            val id = db.userDao().insertUser(newAdmin)
            adminUser = newAdmin.copy(id = id)
        } else if (adminUser.role != "ADMIN") {
            adminUser = adminUser.copy(role = "ADMIN")
            db.userDao().updateUser(adminUser)
        }

        db.activityLogDao().insertLog(
            ActivityLogEntity(
                adminName = adminUser.fullName,
                actionType = "OWNER_AUTH_SUCCESS",
                details = "تسجيل دخول ناجح للمالك بالتحقق الرباعي الآمن للوحة المالك والإدارة"
            )
        )

        _currentUser.value = adminUser
        Result.success(adminUser)
    }

    suspend fun register(
        fullName: String,
        phoneOrEmail: String,
        password: String,
        grade: String = "الصف الثالث الثانوي",
        track: String = "علمي"
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = db.userDao().getUserByPhoneOrEmail(phoneOrEmail.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("رقم الهاتف أو البريد مسجل مسبقاً"))
        }

        val newUser = UserEntity(
            fullName = fullName.trim(),
            phoneOrEmail = phoneOrEmail.trim(),
            passwordHash = password,
            role = "STUDENT",
            grade = grade,
            track = track,
            isActive = true
        )

        val id = db.userDao().insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created

        // Send welcome notification
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "مرحباً بك يا ${created.fullName} في تطبيق رقيب للتعليم الثانوي! 🎉",
                message = "تم إنشاء حسابك بنجاح. يمكنك استكشاف المواد المجانية أو تفعيل الاشتراك للوصول الكامل لجميع الميزات.",
                targetType = "ALL"
            )
        )

        Result.success(created)
    }

    suspend fun autoLoginDemoStudent() = withContext(Dispatchers.IO) {
        val student = db.userDao().getUserByPhoneOrEmail("770000000")
        if (student != null) {
            _currentUser.value = student
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun resetPassword(phoneOrEmail: String, newPassword: String): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserByPhoneOrEmail(phoneOrEmail.trim())
            ?: return@withContext Result.failure(Exception("الحساب غير موجود"))

        db.userDao().updateUser(user.copy(passwordHash = newPassword))
        Result.success("تم تغيير كلمة المرور بنجاح. يمكنك تسجيل الدخول الآن.")
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        db.userDao().updateUser(user)
        if (_currentUser.value?.id == user.id) {
            _currentUser.value = user
        }
    }

    suspend fun deleteAccount(userId: Long) = withContext(Dispatchers.IO) {
        db.userDao().deleteUserById(userId)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null
        }
    }

    // ---------------------------------------------------------
    // SUBSCRIPTIONS & CODES
    // ---------------------------------------------------------

    fun getActiveSubscriptionForUser(userId: Long): Flow<SubscriptionEntity?> {
        return db.subscriptionDao().getActiveSubscription(userId)
    }

    fun getAllActivePlans(): Flow<List<SubscriptionPlanEntity>> {
        return db.subscriptionPlanDao().getActivePlans()
    }

    suspend fun activateSubscriptionDirectly(
        userId: Long,
        studentName: String,
        planMonths: Int,
        planName: String,
        priceYmr: Int,
        adminName: String
    ): Result<SubscriptionEntity> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val currentSub = db.subscriptionDao().getActiveSubscriptionSync(userId, now)

        val durationMs = planMonths.toLong() * 30L * 24 * 60 * 60 * 1000
        val startDate = if (currentSub != null && currentSub.endDate > now) currentSub.startDate else now
        val endDate = if (currentSub != null && currentSub.endDate > now) currentSub.endDate + durationMs else now + durationMs

        val sub = SubscriptionEntity(
            userId = userId,
            planName = planName,
            durationMonths = planMonths,
            priceYmr = priceYmr,
            startDate = startDate,
            endDate = endDate,
            status = "ACTIVE",
            activationMethod = "DIRECT_ADMIN"
        )
        val id = db.subscriptionDao().insertSubscription(sub)

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "تم تفعيل اشتراكك بنجاح 🎉",
                message = "الخطة: $planName\nتاريخ الانتهاء: ${dateFormat.format(Date(endDate))}",
                targetType = "ALL"
            )
        )

        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "ACTIVATE_SUBSCRIPTION",
                adminName = adminName,
                studentName = studentName,
                planName = planName,
                details = "تفعيل يدوي مباشر للاشتراك لمدة $planMonths أشهر"
            )
        )

        Result.success(sub.copy(id = id))
    }

    suspend fun extendSubscription(
        userId: Long,
        studentName: String,
        extraDays: Int,
        adminName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val currentSub = db.subscriptionDao().getActiveSubscriptionSync(userId, now)
        val extraMs = extraDays.toLong() * 24 * 60 * 60 * 1000

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        if (currentSub != null && currentSub.endDate > now) {
            val newEnd = currentSub.endDate + extraMs
            db.subscriptionDao().updateSubscription(currentSub.copy(endDate = newEnd))
            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    actionType = "EXTEND_SUBSCRIPTION",
                    adminName = adminName,
                    studentName = studentName,
                    details = "تمديد الاشتراك الحالي بمقدار $extraDays يوم (ينتهي في ${dateFormat.format(Date(newEnd))})"
                )
            )
            Result.success("تم تمديد الاشتراك بنجاح حتى ${dateFormat.format(Date(newEnd))}")
        } else {
            // New subscription from today
            val newSub = SubscriptionEntity(
                userId = userId,
                planName = "تمديد $extraDays يوم",
                durationMonths = (extraDays / 30).coerceAtLeast(1),
                priceYmr = 0,
                startDate = now,
                endDate = now + extraMs,
                status = "ACTIVE",
                activationMethod = "DIRECT_ADMIN"
            )
            db.subscriptionDao().insertSubscription(newSub)
            Result.success("تم تفعيل اشتراك جديد يبدأ اليوم حتى ${dateFormat.format(Date(now + extraMs))}")
        }
    }

    suspend fun generateSubscriptionCode(
        planMonths: Int,
        planName: String,
        priceYmr: Int,
        tiedPhone: String? = null,
        adminName: String
    ): SubscriptionCodeEntity = withContext(Dispatchers.IO) {
        val randomChars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val random = SecureRandom()
        val part1 = (1..4).map { randomChars[random.nextInt(randomChars.length)] }.joinToString("")
        val part2 = (1..4).map { randomChars[random.nextInt(randomChars.length)] }.joinToString("")
        val generatedCode = "YEM-$part1-$part2"

        val codeEntity = SubscriptionCodeEntity(
            code = generatedCode,
            planName = planName,
            durationMonths = planMonths,
            priceYmr = priceYmr,
            status = "UNUSED",
            tiedStudentPhone = tiedPhone?.takeIf { it.isNotBlank() },
            createdByAdmin = adminName
        )
        val id = db.subscriptionCodeDao().insertCode(codeEntity)

        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "GENERATE_CODE",
                adminName = adminName,
                details = "إنشاء كود اشتراك جديد: $generatedCode للخطة $planName ${tiedPhone?.let { "(مرتبط بالطالب: $it)" } ?: "(عام)"}"
            )
        )

        codeEntity.copy(id = id)
    }

    suspend fun cancelSubscriptionCode(codeId: Long, codeValue: String, adminName: String) = withContext(Dispatchers.IO) {
        val code = db.subscriptionCodeDao().getCodeByValue(codeValue)
        if (code != null && code.status == "UNUSED") {
            db.subscriptionCodeDao().updateCode(code.copy(status = "CANCELLED"))
            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    actionType = "CANCEL_CODE",
                    adminName = adminName,
                    details = "إلغاء كود الاشتراك $codeValue قبل استخدامه"
                )
            )
        }
    }

    suspend fun activateCodeByStudent(
        userId: Long,
        studentName: String,
        studentPhone: String,
        enteredCode: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(userId)
        if (user != null && user.isBlocked) {
            return@withContext Result.failure(Exception("⛔ حسابك محظور: ${user.blockReason ?: "تجاوزت الحد المسموح من المحاولات الخاطئة."}"))
        }

        val cleanCode = enteredCode.trim().uppercase()
        val codeEntity = db.subscriptionCodeDao().getCodeByValue(cleanCode)

        if (codeEntity == null) {
            // Increment failed attempts
            if (user != null) {
                val newAttempts = user.failedCodeAttempts + 1
                if (newAttempts >= 3) {
                    db.userDao().updateBlockStatus(
                        id = userId,
                        isBlocked = true,
                        reason = "تم حظر الحساب تلقائياً بسبب تخمين أكواد التفعيل بشكل متكرر (3 محاولات خاطئة)."
                    )
                    db.activityLogDao().insertLog(
                        ActivityLogEntity(
                            actionType = "SECURITY_BLOCK",
                            adminName = "نظام الأمان التلقائي 🛡️",
                            studentName = studentName,
                            details = "تم حظر الطالب $studentName (الرقم: $studentPhone) بعد 3 محاولات تخمين خاطئة للأكواد: $cleanCode"
                        )
                    )
                    return@withContext Result.failure(Exception("⛔ تم حظر رقمك وحسابك فوراً لمحاولتك تخمين أكواد التفعيل 3 مرات متتالية! لم يعد بإمكانك استخدام التطبيق إلا بعد مراجعة المالك."))
                } else {
                    db.userDao().incrementFailedCodeAttempts(userId)
                    val remaining = 3 - newAttempts
                    return@withContext Result.failure(Exception("كود الاشتراك غير صحيح. تحذير أمني: متبقي لديك $remaining محاولات فقط قبل حظر حسابك نهائياً!"))
                }
            }
            return@withContext Result.failure(Exception("كود الاشتراك غير صحيح أو غير موجود"))
        }

        if (codeEntity.status == "USED") {
            return@withContext Result.failure(Exception("هذا الكود تم استخدامه مسبقاً ولا يمكن استخدامه مرة أخرى"))
        }

        if (codeEntity.status == "CANCELLED") {
            return@withContext Result.failure(Exception("هذا الكود تم إلغاؤه من قبل الإدارة"))
        }

        if (codeEntity.status == "EXPIRED") {
            return@withContext Result.failure(Exception("هذا الكود منتهي الصلاحية"))
        }

        if (!codeEntity.tiedStudentPhone.isNullOrBlank()) {
            if (codeEntity.tiedStudentPhone != studentPhone.trim()) {
                return@withContext Result.failure(Exception("هذا الكود مخصص لحساب طالب آخر ولا يمكن استخدامه بهذا الحساب"))
            }
        }

        val now = System.currentTimeMillis()
        val currentSub = db.subscriptionDao().getActiveSubscriptionSync(userId, now)
        val durationMs = codeEntity.durationMonths.toLong() * 30L * 24 * 60 * 60 * 1000

        val startDate = if (currentSub != null && currentSub.endDate > now) currentSub.startDate else now
        val endDate = if (currentSub != null && currentSub.endDate > now) currentSub.endDate + durationMs else now + durationMs

        // Insert new / extended subscription
        db.subscriptionDao().insertSubscription(
            SubscriptionEntity(
                userId = userId,
                planName = codeEntity.planName,
                durationMonths = codeEntity.durationMonths,
                priceYmr = codeEntity.priceYmr,
                startDate = startDate,
                endDate = endDate,
                status = "ACTIVE",
                activationMethod = "CODE",
                codeUsed = cleanCode
            )
        )

        // Reset failed code attempts upon success
        db.userDao().resetFailedCodeAttempts(userId)

        // Mark code as used
        db.subscriptionCodeDao().updateCode(
            codeEntity.copy(
                status = "USED",
                usedByStudentId = userId,
                usedByStudentName = studentName,
                usedAt = now
            )
        )

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "تم تفعيل اشتراكك بالكود بنجاح 🎉",
                message = "الخطة: ${codeEntity.planName}\nتاريخ الانتهاء: ${dateFormat.format(Date(endDate))}",
                targetType = "ALL"
            )
        )

        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "CODE_ACTIVATION",
                adminName = "الطالب ذاتياً",
                studentName = studentName,
                planName = codeEntity.planName,
                details = "تفعيل الكود $cleanCode بنجاح"
            )
        )

        Result.success("تم تفعيل اشتراك ${codeEntity.planName} بنجاح حتى ${dateFormat.format(Date(endDate))}!")
    }

    // ---------------------------------------------------------
    // PAYMENTS (YEMENI WALLETS)
    // ---------------------------------------------------------

    suspend fun submitPaymentRequest(
        userId: Long,
        studentName: String,
        studentPhone: String,
        planName: String,
        durationMonths: Int,
        amountYmr: Int,
        walletName: String,
        transferRefNumber: String,
        receiptImageUri: String? = null
    ): Result<Long> = withContext(Dispatchers.IO) {
        val payment = PaymentRequestEntity(
            userId = userId,
            studentName = studentName.trim(),
            studentPhone = studentPhone.trim(),
            planName = planName,
            durationMonths = durationMonths,
            amountYmr = amountYmr,
            walletName = walletName,
            transferRefNumber = transferRefNumber.trim(),
            receiptImageUri = receiptImageUri,
            status = "PENDING"
        )
        val id = db.paymentRequestDao().insertPayment(payment)

        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "PAYMENT_SUBMITTED",
                adminName = "الطالب",
                studentName = studentName,
                planName = planName,
                details = "طلب دفع جديد عبر $walletName بمبلغ $amountYmr ريال (رقم الحوالة: $transferRefNumber)"
            )
        )

        Result.success(id)
    }

    suspend fun reviewPaymentRequest(
        paymentId: Long,
        approve: Boolean,
        createCodeInsteadOfDirectActivation: Boolean = false,
        rejectionReason: String? = null,
        adminName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val payment = db.paymentRequestDao().getPaymentById(paymentId)
            ?: return@withContext Result.failure(Exception("طلب الدفع غير موجود"))

        val now = System.currentTimeMillis()

        if (!approve) {
            val updated = payment.copy(
                status = "REJECTED",
                rejectionReason = rejectionReason ?: "بيانات التحويل غير مطابقة أو لم يصل المبلغ",
                reviewedAt = now
            )
            db.paymentRequestDao().updatePayment(updated)

            db.notificationDao().insertNotification(
                NotificationEntity(
                    title = "تم رفض طلب الدفع ❌",
                    message = "السبب: ${updated.rejectionReason}\nيرجى التواصل مع الإدارة أو إعادة الإرسال برقم حوالة صحيح.",
                    targetType = "ALL"
                )
            )

            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    actionType = "PAYMENT_REJECTED",
                    adminName = adminName,
                    studentName = payment.studentName,
                    planName = payment.planName,
                    details = "رفض طلب الدفع: ${updated.rejectionReason}"
                )
            )

            return@withContext Result.success("تم رفض طلب الدفع مع إشعار الطالب بالسبب")
        }

        // Approval branch
        if (createCodeInsteadOfDirectActivation) {
            val code = generateSubscriptionCode(
                planMonths = payment.durationMonths,
                planName = payment.planName,
                priceYmr = payment.amountYmr,
                tiedPhone = payment.studentPhone,
                adminName = adminName
            )

            val updated = payment.copy(status = "APPROVED", reviewedAt = now)
            db.paymentRequestDao().updatePayment(updated)

            db.notificationDao().insertNotification(
                NotificationEntity(
                    title = "تم قبول دفعك وإنشاء كود اشتراك لك! 🎉",
                    message = "كود اشتراكك هو: ${code.code}\nيمكنك نسخه وتفعيله الآن من صفحة الاشتراكات.",
                    targetType = "ALL"
                )
            )

            return@withContext Result.success("تم قبول الدفع وتوليد الكود (${code.code}) وإرساله للطالب!")
        } else {
            activateSubscriptionDirectly(
                userId = payment.userId,
                studentName = payment.studentName,
                planMonths = payment.durationMonths,
                planName = payment.planName,
                priceYmr = payment.amountYmr,
                adminName = adminName
            )

            val updated = payment.copy(status = "APPROVED", reviewedAt = now)
            db.paymentRequestDao().updatePayment(updated)

            return@withContext Result.success("تم قبول الدفع وتفعيل اشتراك الطالب فوراً!")
        }
    }

    // ---------------------------------------------------------
    // CURRICULUM & SEARCH
    // ---------------------------------------------------------

    fun getAllSubjects(): Flow<List<SubjectEntity>> = db.curriculumDao().getAllSubjects()

    fun getUnitsForSubject(subjectId: Long): Flow<List<UnitEntity>> = db.curriculumDao().getUnitsForSubject(subjectId)

    fun getLessonsForUnit(unitId: Long): Flow<List<LessonEntity>> = db.curriculumDao().getLessonsForUnit(unitId)

    fun getLessonsForSubject(subjectId: Long): Flow<List<LessonEntity>> = db.curriculumDao().getLessonsForSubject(subjectId)

    suspend fun getLessonById(id: Long): LessonEntity? = db.curriculumDao().getLessonById(id)

    fun searchLessons(query: String): Flow<List<LessonEntity>> = db.curriculumDao().searchLessons(query)

    // Admin Content Management
    suspend fun addSubject(name: String, description: String, track: String = "BOTH"): Long = withContext(Dispatchers.IO) {
        val s = SubjectEntity(name = name, iconName = "book", track = track, description = description)
        db.curriculumDao().insertSubject(s)
    }

    suspend fun deleteSubject(subject: SubjectEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().deleteSubject(subject)
    }

    suspend fun addLesson(lesson: LessonEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: LessonEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: LessonEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().deleteLesson(lesson)
    }

    suspend fun addUnit(unit: UnitEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().insertUnit(unit)
    }

    suspend fun updateUnit(unit: UnitEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().updateUnit(unit)
    }

    suspend fun deleteUnit(unit: UnitEntity) = withContext(Dispatchers.IO) {
        db.curriculumDao().deleteUnit(unit)
    }

    suspend fun addQuestion(question: QuestionEntity) = withContext(Dispatchers.IO) {
        db.questionDao().insertQuestion(question)
    }

    suspend fun deleteQuestion(question: QuestionEntity) = withContext(Dispatchers.IO) {
        db.questionDao().deleteQuestion(question)
    }

    suspend fun addExam(exam: ExamEntity) = withContext(Dispatchers.IO) {
        db.examDao().insertExam(exam)
    }

    // ---------------------------------------------------------
    // EXAMS & QUESTION BANK
    // ---------------------------------------------------------

    fun getAllExams(): Flow<List<ExamEntity>> = db.examDao().getAllExams()

    fun getExamsForSubject(subjectId: Long): Flow<List<ExamEntity>> = db.examDao().getExamsForSubject(subjectId)

    suspend fun getExamById(id: Long): ExamEntity? = db.examDao().getExamById(id)

    fun getAllQuestions(): Flow<List<QuestionEntity>> = db.questionDao().getAllQuestions()

    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>> = db.questionDao().getQuestionsForSubject(subjectId)

    suspend fun getRandomQuestionsForExam(subjectId: Long, limit: Int): List<QuestionEntity> = withContext(Dispatchers.IO) {
        if (subjectId > 0) {
            val list = db.questionDao().getRandomQuestionsForSubject(subjectId, limit)
            if (list.isNotEmpty()) return@withContext list
        }
        db.questionDao().getRandomQuestionsOverall(limit)
    }

    suspend fun saveExamResult(result: ExamResultEntity): Long = withContext(Dispatchers.IO) {
        db.examDao().insertExamResult(result)
    }

    fun getExamResultsForUser(userId: Long): Flow<List<ExamResultEntity>> = db.examDao().getResultsForUser(userId)

    // ---------------------------------------------------------
    // PROGRESS & FAVORITES
    // ---------------------------------------------------------

    fun getCompletedLessonIds(userId: Long): Flow<List<Long>> = db.progressDao().getCompletedLessonIds(userId)

    suspend fun toggleLessonProgress(userId: Long, lessonId: Long, subjectId: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        if (isCompleted) {
            db.progressDao().insertProgress(StudentProgressEntity(userId = userId, lessonId = lessonId, subjectId = subjectId))
        } else {
            db.progressDao().removeProgress(userId, lessonId)
        }
    }

    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>> = db.favoriteDao().getFavoritesForUser(userId)

    fun isFavorite(userId: Long, itemType: String, itemId: Long): Flow<Boolean> = db.favoriteDao().isFavorite(userId, itemType, itemId)

    suspend fun toggleFavorite(userId: Long, itemType: String, itemId: Long, title: String, subtitle: String, isFav: Boolean) = withContext(Dispatchers.IO) {
        if (isFav) {
            db.favoriteDao().insertFavorite(FavoriteEntity(userId = userId, itemType = itemType, itemId = itemId, title = title, subtitle = subtitle))
        } else {
            db.favoriteDao().removeFavorite(userId, itemType, itemId)
        }
    }

    // ---------------------------------------------------------
    // NOTIFICATIONS & LOGS
    // ---------------------------------------------------------

    fun getAllNotifications(): Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()

    suspend fun sendBroadcastNotification(title: String, message: String, targetType: String = "ALL", adminName: String) = withContext(Dispatchers.IO) {
        db.notificationDao().insertNotification(
            NotificationEntity(title = title, message = message, targetType = targetType)
        )
        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "BROADCAST_NOTIFICATION",
                adminName = adminName,
                details = "إرسال إشعار عام: $title"
            )
        )
    }

    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>> = db.activityLogDao().getAllLogs()

    // ---------------------------------------------------------
    // ADMIN DASHBOARD METRICS
    // ---------------------------------------------------------

    fun getStudentCount() = db.userDao().getStudentCount()
    fun getActiveStudentCount() = db.userDao().getActiveStudentCount()
    fun getAllSubscriptions() = db.subscriptionDao().getAllSubscriptions()
    fun getAllActiveSubscriptions() = db.subscriptionDao().getAllActiveSubscriptions()
    fun getAllExpiredSubscriptions() = db.subscriptionDao().getAllExpiredSubscriptions()
    fun getPendingPaymentCount() = db.paymentRequestDao().getPendingPaymentCount()
    fun getAllPaymentRequests() = db.paymentRequestDao().getAllPaymentRequests()
    fun getAllCodes() = db.subscriptionCodeDao().getAllCodes()
    fun getUnusedCodeCount() = db.subscriptionCodeDao().getUnusedCodeCount()
    fun getUsedCodeCount() = db.subscriptionCodeDao().getUsedCodeCount()
    fun getSubjectCount() = db.curriculumDao().getSubjectCount()
    fun getLessonCount() = db.curriculumDao().getLessonCount()
    fun getExamCount() = db.examDao().getExamCount()
    fun getAllUsers() = db.userDao().getAllUsers()

    // ---------------------------------------------------------
    // OWNER SUBSCRIBER CONTROLS (BLOCK / CANCEL / UNBLOCK)
    // ---------------------------------------------------------

    suspend fun blockStudent(userId: Long, studentName: String, reason: String, adminName: String) = withContext(Dispatchers.IO) {
        db.userDao().updateBlockStatus(userId, isBlocked = true, reason = reason)
        // Also cancel any active subscriptions
        val now = System.currentTimeMillis()
        val currentSub = db.subscriptionDao().getActiveSubscriptionSync(userId, now)
        if (currentSub != null) {
            db.subscriptionDao().updateSubscription(currentSub.copy(status = "CANCELLED"))
        }
        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "BLOCK_STUDENT",
                adminName = adminName,
                studentName = studentName,
                details = "حظر الطالب $studentName وإلغاء اشتراكه. السبب: $reason"
            )
        )
        Result.success("تم حظر المشترك $studentName بنجاح وإيقاف وصوله للتطبيق.")
    }

    suspend fun unblockStudent(userId: Long, studentName: String, adminName: String) = withContext(Dispatchers.IO) {
        db.userDao().updateBlockStatus(userId, isBlocked = false, reason = null)
        db.userDao().resetFailedCodeAttempts(userId)
        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "UNBLOCK_STUDENT",
                adminName = adminName,
                studentName = studentName,
                details = "إلغاء حظر الطالب $studentName وإعادة تعيين عداد المحاولات"
            )
        )
        Result.success("تم فك الحظر عن الطالب $studentName بنجاح.")
    }

    suspend fun cancelStudentSubscription(userId: Long, studentName: String, adminName: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val currentSub = db.subscriptionDao().getActiveSubscriptionSync(userId, now)
        if (currentSub != null) {
            db.subscriptionDao().updateSubscription(currentSub.copy(status = "CANCELLED"))
            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    actionType = "CANCEL_SUBSCRIPTION",
                    adminName = adminName,
                    studentName = studentName,
                    details = "إلغاء اشتراك الطالب $studentName من قبل المالك"
                )
            )
            Result.success("تم إلغاء اشتراك الطالب $studentName.")
        } else {
            Result.failure(Exception("لا يوجد اشتراك نشط لهذا الطالب لإلغائه."))
        }
    }

    // ---------------------------------------------------------
    // STUDY MATERIALS (BOOKS, BOOKLETS, SUMMARIES)
    // ---------------------------------------------------------

    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>> = db.studyMaterialDao().getAllMaterials()
    fun getStudyMaterialsBySubject(subjectName: String): Flow<List<StudyMaterialEntity>> = db.studyMaterialDao().getMaterialsBySubject(subjectName)
    fun getStudyMaterialsByType(type: String): Flow<List<StudyMaterialEntity>> = db.studyMaterialDao().getMaterialsByType(type)

    suspend fun addStudyMaterial(material: StudyMaterialEntity, adminName: String) = withContext(Dispatchers.IO) {
        val id = db.studyMaterialDao().insertMaterial(material)
        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "ADD_STUDY_MATERIAL",
                adminName = adminName,
                details = "إضافة ${when(material.type) { "BOOK" -> "كتاب" "BOOKLET" -> "ملزمة" "SUMMARY" -> "ملخص" else -> "ملف" }}: ${material.title} لمادة ${material.subjectName}"
            )
        )
        // Broadcast notification to students
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "تمت إضافة ملف تعليمي جديد 📚",
                message = "${material.title} (${material.subjectName}) متاح الآن في صفحة الملازم والكتب.",
                targetType = "ALL"
            )
        )
        id
    }

    suspend fun deleteStudyMaterial(material: StudyMaterialEntity, adminName: String) = withContext(Dispatchers.IO) {
        db.studyMaterialDao().deleteMaterial(material)
        db.activityLogDao().insertLog(
            ActivityLogEntity(
                actionType = "DELETE_STUDY_MATERIAL",
                adminName = adminName,
                details = "حذف الملف التعليمي: ${material.title}"
            )
        )
    }
}

