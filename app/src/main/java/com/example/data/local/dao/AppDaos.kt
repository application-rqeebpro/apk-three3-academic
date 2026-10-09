package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phoneOrEmail = :query LIMIT 1")
    suspend fun getUserByPhoneOrEmail(query: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET passwordHash = :newPasswordHash WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newPasswordHash: String): Int

    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserActiveStatus(userId: Long, isActive: Boolean): Int

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY id DESC")
    fun getAllStudents(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>
}

@Dao
interface CurriculumDao {
    @Query("SELECT * FROM subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Query("SELECT * FROM units WHERE subjectId = :subjectId ORDER BY unitNumber ASC")
    fun getUnitsForSubject(subjectId: Long): Flow<List<UnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Query("SELECT * FROM lessons WHERE unitId = :unitId ORDER BY lessonNumber ASC")
    fun getLessonsForUnit(unitId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getLessonsForSubject(subjectId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR coreIdea LIKE '%' || :query || '%'")
    fun searchLessons(query: String): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lessonId = :lessonId")
    fun getQuestionsForLesson(lessonId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsForSubject(subjectId: Long, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    fun getAllQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT questionId FROM exam_questions WHERE examId = :examId)")
    suspend fun getQuestionsForExam(examId: Long): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE subjectId = :subjectId")
    fun getExamsForSubject(subjectId: Long): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND subjectId = :subjectId")
    fun getProgressForSubject(userId: Long, subjectId: Long): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getAllProgressForUser(userId: Long): Flow<List<UserProgressEntity>>

    @Query("SELECT lessonId FROM user_progress WHERE userId = :userId AND isCompleted = 1")
    fun getCompletedLessonIds(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesForUser(userId: Long): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemType = :type AND itemId = :itemId")
    suspend fun deleteFavorite(userId: Long, type: String, itemId: Long): Int
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE userId = :userId AND isActive = 1 ORDER BY endDate DESC LIMIT 1")
    fun getActiveSubscription(userId: Long): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET isActive = 0 WHERE userId = :userId")
    suspend fun revokeUserSubscriptions(userId: Long): Int

    @Query("SELECT * FROM subscriptions ORDER BY id DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRequest(request: PaymentRequestEntity): Long

    @Query("SELECT * FROM payment_requests WHERE status = 'PENDING' ORDER BY requestDate DESC")
    fun getPendingPaymentRequests(): Flow<List<PaymentRequestEntity>>

    @Query("SELECT * FROM payment_requests ORDER BY requestDate DESC")
    fun getAllPaymentRequests(): Flow<List<PaymentRequestEntity>>

    @Query("UPDATE payment_requests SET status = :status, approvedDate = :approvedDate WHERE id = :requestId")
    suspend fun updatePaymentRequestStatus(requestId: Long, status: String, approvedDate: Long?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(code: SubscriptionCodeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodes(codes: List<SubscriptionCodeEntity>)

    @Query("SELECT * FROM subscription_codes WHERE code = :code LIMIT 1")
    suspend fun getCode(code: String): SubscriptionCodeEntity?

    @Update
    suspend fun updateCode(code: SubscriptionCodeEntity)

    @Query("SELECT * FROM subscription_codes ORDER BY id DESC")
    fun getAllCodes(): Flow<List<SubscriptionCodeEntity>>
}

@Dao
interface AdminLogDao {
    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdminLogEntity)
}

@Dao
interface DocumentBookletDao {
    @Query("SELECT * FROM document_booklets ORDER BY uploadedAt DESC")
    fun getAllDocumentBooklets(): Flow<List<DocumentBookletEntity>>

    @Query("SELECT * FROM document_booklets WHERE subjectId = :subjectId ORDER BY uploadedAt DESC")
    fun getBookletsBySubject(subjectId: Long): Flow<List<DocumentBookletEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooklet(booklet: DocumentBookletEntity): Long

    @Delete
    suspend fun deleteBooklet(booklet: DocumentBookletEntity)
}

@Dao
interface ExamResultDao {
    @Query("SELECT * FROM exam_results WHERE userId = :userId ORDER BY takenAt DESC")
    fun getUserExamResults(userId: Long): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity): Long

    @Query("SELECT * FROM exam_results ORDER BY takenAt DESC")
    fun getAllExamResults(): Flow<List<ExamResultEntity>>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long
}
