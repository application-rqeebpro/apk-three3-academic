package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {

    // Users & Auth
    suspend fun getUserByPhoneOrEmail(query: String): UserEntity? {
        return database.userDao().getUserByPhoneOrEmail(query)
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return database.userDao().getUserById(id)
    }

    suspend fun registerUser(user: UserEntity): Long {
        return database.userDao().insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        database.userDao().updateUser(user)
    }

    suspend fun changeUserPassword(userId: Long, newPasswordHash: String) {
        database.userDao().updatePassword(userId, newPasswordHash)
    }

    suspend fun updateUserActiveStatus(userId: Long, isActive: Boolean) {
        database.userDao().updateUserActiveStatus(userId, isActive)
    }

    fun getAllStudents(): Flow<List<UserEntity>> {
        return database.userDao().getAllStudents()
    }

    fun getAllUsers(): Flow<List<UserEntity>> {
        return database.userDao().getAllUsers()
    }

    // Curriculum
    fun getAllSubjects(): Flow<List<SubjectEntity>> {
        return database.curriculumDao().getAllSubjects()
    }

    fun getUnitsForSubject(subjectId: Long): Flow<List<UnitEntity>> {
        return database.curriculumDao().getUnitsForSubject(subjectId)
    }

    fun getLessonsForUnit(unitId: Long): Flow<List<LessonEntity>> {
        return database.curriculumDao().getLessonsForUnit(unitId)
    }

    fun getLessonsForSubject(subjectId: Long): Flow<List<LessonEntity>> {
        return database.curriculumDao().getLessonsForSubject(subjectId)
    }

    fun searchLessons(query: String): Flow<List<LessonEntity>> {
        return database.curriculumDao().searchLessons(query)
    }

    suspend fun addLesson(lesson: LessonEntity): Long {
        return database.curriculumDao().insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: LessonEntity) {
        database.curriculumDao().updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: LessonEntity) {
        database.curriculumDao().deleteLesson(lesson)
    }

    // Questions
    fun getQuestionsForLesson(lessonId: Long): Flow<List<QuestionEntity>> {
        return database.questionDao().getQuestionsForLesson(lessonId)
    }

    fun getAllQuestions(): Flow<List<QuestionEntity>> {
        return database.questionDao().getAllQuestions()
    }

    suspend fun getRandomQuestionsForSubject(subjectId: Long, limit: Int): List<QuestionEntity> {
        return database.questionDao().getRandomQuestionsForSubject(subjectId, limit)
    }

    fun getAllQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>> {
        return database.questionDao().getAllQuestionsForSubject(subjectId)
    }

    suspend fun getQuestionsForExam(examId: Long): List<QuestionEntity> {
        return database.questionDao().getQuestionsForExam(examId)
    }

    suspend fun getRandomQuestionsForExam(subjectId: Long, count: Int): List<QuestionEntity> {
        return database.questionDao().getRandomQuestionsForSubject(subjectId, count)
    }

    suspend fun addQuestion(question: QuestionEntity): Long {
        return database.questionDao().insertQuestion(question)
    }

    suspend fun deleteQuestion(question: QuestionEntity) {
        database.questionDao().deleteQuestion(question)
    }

    // Exams
    fun getExamsForSubject(subjectId: Long): Flow<List<ExamEntity>> {
        return database.examDao().getExamsForSubject(subjectId)
    }

    fun getAllExams(): Flow<List<ExamEntity>> {
        return database.examDao().getAllExams()
    }

    suspend fun addExam(exam: ExamEntity): Long {
        return database.examDao().insertExam(exam)
    }

    suspend fun saveExamResult(result: ExamResultEntity): Long {
        return database.examResultDao().insertExamResult(result)
    }

    fun getUserExamResults(userId: Long): Flow<List<ExamResultEntity>> {
        return database.examResultDao().getUserExamResults(userId)
    }

    // Progress
    fun getProgressForSubject(userId: Long, subjectId: Long): Flow<List<UserProgressEntity>> {
        return database.progressDao().getProgressForSubject(userId, subjectId)
    }

    fun getAllProgressForUser(userId: Long): Flow<List<UserProgressEntity>> {
        return database.progressDao().getAllProgressForUser(userId)
    }

    fun getCompletedLessonIds(userId: Long): Flow<List<Long>> {
        return database.progressDao().getCompletedLessonIds(userId)
    }

    fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return database.notificationDao().getAllNotifications()
    }

    suspend fun toggleLessonProgress(userId: Long, lessonId: Long, subjectId: Long, completed: Boolean) {
        database.progressDao().insertOrUpdateProgress(
            UserProgressEntity(
                userId = userId,
                lessonId = lessonId,
                subjectId = subjectId,
                isCompleted = completed,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    // Favorites
    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>> {
        return database.favoriteDao().getFavoritesForUser(userId)
    }

    fun getUserFavorites(userId: Long): Flow<List<FavoriteEntity>> {
        return database.favoriteDao().getFavoritesForUser(userId)
    }

    suspend fun toggleFavorite(userId: Long, type: String, itemId: Long, title: String, subtitle: String, isFav: Boolean) {
        if (isFav) {
            database.favoriteDao().insertFavorite(
                FavoriteEntity(
                    userId = userId,
                    itemType = type,
                    itemId = itemId,
                    title = title,
                    subtitle = subtitle
                )
            )
        } else {
            database.favoriteDao().deleteFavorite(userId, type, itemId)
        }
    }

    // Subscriptions
    fun getActiveSubscription(userId: Long): Flow<SubscriptionEntity?> {
        return database.subscriptionDao().getActiveSubscription(userId)
    }

    suspend fun activateSubscription(subscription: SubscriptionEntity): Long {
        return database.subscriptionDao().insertSubscription(subscription)
    }

    suspend fun revokeStudentSubscription(userId: Long) {
        database.subscriptionDao().revokeUserSubscriptions(userId)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "إلغاء اشتراك طالب",
                details = "تم إلغاء وتجميد اشتراك الطالب صاحب المعرف $userId من قبل الإدارة"
            )
        )
    }

    suspend fun revokeUserSubscriptions(userId: Long) {
        revokeStudentSubscription(userId)
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return database.subscriptionDao().getAllSubscriptions()
    }

    // Payment Requests
    suspend fun submitPaymentRequest(request: PaymentRequestEntity): Long {
        return database.subscriptionDao().insertPaymentRequest(request)
    }

    fun getPendingPaymentRequests(): Flow<List<PaymentRequestEntity>> {
        return database.subscriptionDao().getPendingPaymentRequests()
    }

    fun getAllPaymentRequests(): Flow<List<PaymentRequestEntity>> {
        return database.subscriptionDao().getAllPaymentRequests()
    }

    suspend fun updatePaymentRequestStatus(requestId: Long, status: String, approvedDate: Long?) {
        database.subscriptionDao().updatePaymentRequestStatus(requestId, status, approvedDate)
    }

    suspend fun reviewPaymentRequest(paymentId: Long, approve: Boolean, createCode: Boolean = false, reason: String = "") {
        val now = System.currentTimeMillis()
        val status = if (approve) "APPROVED" else "REJECTED"
        database.subscriptionDao().updatePaymentRequestStatus(paymentId, status, if (approve) now else null)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = if (approve) "الموافقة على طلب اشتراك" else "رفض طلب اشتراك",
                details = "تمت معالجة الطلب #$paymentId بحالة: $status ${if (reason.isNotBlank()) "السبب: $reason" else ""}"
            )
        )
    }

    suspend fun activateSubscriptionDirectly(studentId: Long, studentName: String, months: Int, planName: String, price: Int) {
        val now = System.currentTimeMillis()
        val end = now + (months.toLong() * 30 * 24 * 60 * 60 * 1000L)
        val sub = SubscriptionEntity(
            userId = studentId,
            planName = planName,
            startDate = now,
            endDate = end,
            isActive = true,
            paymentMethod = "DIRECT_ADMIN",
            paymentRef = "MANUAL"
        )
        database.subscriptionDao().insertSubscription(sub)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "تفعيل اشتراك مباشر",
                details = "تم تفعيل اشتراك مباشر للطالب $studentName (ID: $studentId) لمدة $months شهر بقيمة $price ريال"
            )
        )
    }

    suspend fun extendSubscription(studentId: Long, studentName: String, days: Int) {
        val now = System.currentTimeMillis()
        val end = now + (days.toLong() * 24 * 60 * 60 * 1000L)
        val sub = SubscriptionEntity(
            userId = studentId,
            planName = "تمديد إداري",
            startDate = now,
            endDate = end,
            isActive = true,
            paymentMethod = "ADMIN_EXTENSION"
        )
        database.subscriptionDao().insertSubscription(sub)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "تمديد اشتراك",
                details = "تم تمديد اشتراك الطالب $studentName (ID: $studentId) بمقدار $days يوماً"
            )
        )
    }

    // Subscription Codes
    suspend fun generateSubscriptionCodes(codes: List<SubscriptionCodeEntity>) {
        database.subscriptionDao().insertCodes(codes)
    }

    suspend fun generateSubscriptionCode(months: Int, planName: String, price: Int, tiedPhone: String = ""): SubscriptionCodeEntity {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val randomStr = (1..6).map { chars.random() }.joinToString("")
        val codeStr = "RAQ-$randomStr"
        val codeEntity = SubscriptionCodeEntity(
            code = codeStr,
            planName = planName,
            durationDays = months * 30
        )
        database.subscriptionDao().insertCode(codeEntity)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "توليد كود اشتراك",
                details = "تم توليد كود جديد: $codeStr للباقة $planName ($months شهر) بقيمة $price ريال"
            )
        )
        return codeEntity
    }

    suspend fun activateCodeByStudent(userId: Long, codeStr: String): Result<SubscriptionEntity> {
        val foundCode = database.subscriptionDao().getCode(codeStr.trim().uppercase())
            ?: return Result.failure(Exception("كود التفعيل غير صحيح أو غير موجود"))
        if (foundCode.isUsed) {
            return Result.failure(Exception("هذا الكود تم استخدامه مسبقاً"))
        }
        val duration = foundCode.durationDays
        val now = System.currentTimeMillis()
        val end = now + (duration.toLong() * 24 * 60 * 60 * 1000L)
        val sub = SubscriptionEntity(
            userId = userId,
            planName = foundCode.planName,
            startDate = now,
            endDate = end,
            isActive = true,
            paymentMethod = "CODE",
            paymentRef = foundCode.code
        )
        database.subscriptionDao().insertSubscription(sub)
        database.subscriptionDao().updateCode(
            foundCode.copy(
                isUsed = true,
                usedByUserId = userId,
                usedDate = now
            )
        )
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "تفعيل كود اشتراك",
                details = "تم تفعيل كود ${foundCode.code} للطالب ID: $userId لمدة $duration يوماً"
            )
        )
        return Result.success(sub)
    }

    suspend fun cancelSubscriptionCode(codeId: Long, code: String) {
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "إلغاء كود اشتراك",
                details = "تم إلغاء وتعطيل الكود: $code (ID: $codeId)"
            )
        )
    }

    suspend fun getCode(code: String): SubscriptionCodeEntity? {
        return database.subscriptionDao().getCode(code)
    }

    suspend fun updateCode(code: SubscriptionCodeEntity) {
        database.subscriptionDao().updateCode(code)
    }

    fun getAllCodes(): Flow<List<SubscriptionCodeEntity>> {
        return database.subscriptionDao().getAllCodes()
    }

    // Document Booklets (PDF & Word)
    fun getAllDocumentBooklets(): Flow<List<DocumentBookletEntity>> {
        return database.documentBookletDao().getAllDocumentBooklets()
    }

    fun getBookletsBySubject(subjectId: Long): Flow<List<DocumentBookletEntity>> {
        return database.documentBookletDao().getBookletsBySubject(subjectId)
    }

    suspend fun addDocumentBooklet(booklet: DocumentBookletEntity): Long {
        val id = database.documentBookletDao().insertBooklet(booklet)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "إضافة ملف دراسي",
                details = "تمت إضافة ملف ${booklet.fileType}: ${booklet.title} (${booklet.fileName})"
            )
        )
        return id
    }

    suspend fun deleteDocumentBooklet(booklet: DocumentBookletEntity) {
        database.documentBookletDao().deleteBooklet(booklet)
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = "حذف ملف دراسي",
                details = "تم حذف ملف ${booklet.title}"
            )
        )
    }

    // Admin Logs
    fun getAllLogs(): Flow<List<AdminLogEntity>> {
        return database.adminLogDao().getAllLogs()
    }

    suspend fun addAdminLog(action: String, details: String) {
        database.adminLogDao().insertLog(
            AdminLogEntity(
                action = action,
                details = details
            )
        )
    }
}
