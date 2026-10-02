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

        if (!user.isActive) {
            return@withContext Result.failure(Exception("تم تعطيل هذا الحساب من قبل الإدارة. يرجى التواصل مع الدعم."))
        }

        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("كلمة المرور غير صحيحة"))
        }

        _currentUser.value = user
        Result.success(user)
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

        // Send official welcome notification
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "مرحباً بك يا ${created.fullName} في منصة رقيب للتعليم الثانوي! 🇾🇪",
                message = "تم إنشاء حسابك بنجاح. لتفعيل حسابك والوصول الكامل لجميع الدروس والمسائل، يرجى إدخال كود التفعيل أو تقديم طلب اشتراك رسمي عبر المحافظ المعتمدة.",
                targetType = "ALL"
            )
        )

        Result.success(created)
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
        val cleanCode = enteredCode.trim().uppercase()
        val codeEntity = db.subscriptionCodeDao().getCodeByValue(cleanCode)
            ?: return@withContext Result.failure(Exception("كود الاشتراك غير صحيح أو غير موجود"))

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
}
