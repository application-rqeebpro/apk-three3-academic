package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phoneOrEmail = :query LIMIT 1")
    suspend fun getUserByPhoneOrEmail(query: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT'")
    fun getStudentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT' AND isActive = 1")
    fun getActiveStudentCount(): Flow<Int>
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE userId = :userId AND status = 'ACTIVE' AND endDate > :currentTime ORDER BY endDate DESC LIMIT 1")
    fun getActiveSubscription(userId: Long, currentTime: Long = System.currentTimeMillis()): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY startDate DESC")
    fun getUserSubscriptions(userId: Long): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions ORDER BY startDate DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE status = 'ACTIVE' AND endDate > :currentTime")
    fun getAllActiveSubscriptions(currentTime: Long = System.currentTimeMillis()): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE status = 'EXPIRED' OR endDate <= :currentTime")
    fun getAllExpiredSubscriptions(currentTime: Long = System.currentTimeMillis()): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE userId = :userId AND status = 'ACTIVE' AND endDate > :currentTime ORDER BY endDate DESC LIMIT 1")
    suspend fun getActiveSubscriptionSync(userId: Long, currentTime: Long = System.currentTimeMillis()): SubscriptionEntity?
}

@Dao
interface SubscriptionPlanDao {
    @Query("SELECT * FROM subscription_plans WHERE isActive = 1 ORDER BY durationMonths ASC")
    fun getActivePlans(): Flow<List<SubscriptionPlanEntity>>

    @Query("SELECT * FROM subscription_plans WHERE id = :id")
    suspend fun getPlanById(id: Long): SubscriptionPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: SubscriptionPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<SubscriptionPlanEntity>)

    @Update
    suspend fun updatePlan(plan: SubscriptionPlanEntity)
}

@Dao
interface SubscriptionCodeDao {
    @Query("SELECT * FROM subscription_codes ORDER BY createdAt DESC")
    fun getAllCodes(): Flow<List<SubscriptionCodeEntity>>

    @Query("SELECT * FROM subscription_codes WHERE code = :code LIMIT 1")
    suspend fun getCodeByValue(code: String): SubscriptionCodeEntity?

    @Query("SELECT COUNT(*) FROM subscription_codes WHERE status = 'UNUSED'")
    fun getUnusedCodeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM subscription_codes WHERE status = 'USED'")
    fun getUsedCodeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(code: SubscriptionCodeEntity): Long

    @Update
    suspend fun updateCode(code: SubscriptionCodeEntity)
}

@Dao
interface PaymentRequestDao {
    @Query("SELECT * FROM payment_requests ORDER BY createdAt DESC")
    fun getAllPaymentRequests(): Flow<List<PaymentRequestEntity>>

    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsByUserId(userId: Long): Flow<List<PaymentRequestEntity>>

    @Query("SELECT * FROM payment_requests WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingPayments(): Flow<List<PaymentRequestEntity>>

    @Query("SELECT COUNT(*) FROM payment_requests WHERE status = 'PENDING'")
    fun getPendingPaymentCount(): Flow<Int>

    @Query("SELECT * FROM payment_requests WHERE id = :id")
    suspend fun getPaymentById(id: Long): PaymentRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRequestEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentRequestEntity)
}

@Dao
interface CurriculumDao {
    @Query("SELECT * FROM subjects ORDER BY sortOrder ASC, id ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    // Units
    @Query("SELECT * FROM units WHERE subjectId = :subjectId ORDER BY sortOrder ASC, id ASC")
    fun getUnitsForSubject(subjectId: Long): Flow<List<UnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Update
    suspend fun updateUnit(unit: UnitEntity)

    @Delete
    suspend fun deleteUnit(unit: UnitEntity)

    // Chapters
    @Query("SELECT * FROM chapters WHERE unitId = :unitId ORDER BY sortOrder ASC, id ASC")
    fun getChaptersForUnit(unitId: Long): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    // Lessons
    @Query("SELECT * FROM lessons WHERE chapterId = :chapterId ORDER BY sortOrder ASC, id ASC")
    fun getLessonsForChapter(chapterId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE unitId = :unitId ORDER BY sortOrder ASC, id ASC")
    fun getLessonsForUnit(unitId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE subjectId = :subjectId ORDER BY sortOrder ASC, id ASC")
    fun getLessonsForSubject(subjectId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): LessonEntity?

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR coreIdea LIKE '%' || :query || '%' OR keyPoints LIKE '%' || :query || '%'")
    fun searchLessons(query: String): Flow<List<LessonEntity>>

    @Query("SELECT COUNT(*) FROM lessons")
    fun getLessonCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId AND difficulty = :difficulty")
    fun getQuestionsByDifficulty(subjectId: Long, difficulty: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsForSubject(subjectId: Long, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsOverall(limit: Int): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionCount(): Flow<Int>
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY id ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getExamsForSubject(subjectId: Long): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: Long): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Query("SELECT COUNT(*) FROM exams")
    fun getExamCount(): Flow<Int>

    // Exam Results
    @Query("SELECT * FROM exam_results WHERE userId = :userId ORDER BY dateTimestamp DESC")
    fun getResultsForUser(userId: Long): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity): Long
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM student_progress WHERE userId = :userId")
    fun getProgressForUser(userId: Long): Flow<List<StudentProgressEntity>>

    @Query("SELECT lessonId FROM student_progress WHERE userId = :userId AND isCompleted = 1")
    fun getCompletedLessonIds(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: StudentProgressEntity): Long

    @Query("DELETE FROM student_progress WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun removeProgress(userId: Long, lessonId: Long)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY savedAt DESC")
    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND itemType = :itemType AND itemId = :itemId)")
    fun isFavorite(userId: Long, itemType: String, itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemType = :itemType AND itemId = :itemId")
    suspend fun removeFavorite(userId: Long, itemType: String, itemId: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity): Long
}
