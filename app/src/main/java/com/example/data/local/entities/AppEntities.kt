package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phoneOrEmail: String,
    val passwordHash: String,
    val role: String = "STUDENT", // "STUDENT" or "ADMIN"
    val grade: String = "الصف الثالث الثانوي",
    val track: String = "علمي", // "علمي" or "أدبي"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val planName: String, // "3 أشهر", "6 أشهر", "اشتراك سنوي"
    val durationMonths: Int,
    val priceYmr: Int,
    val startDate: Long,
    val endDate: Long,
    val status: String = "ACTIVE", // "ACTIVE", "EXPIRED", "CANCELLED"
    val activationMethod: String = "DIRECT_ADMIN", // "DIRECT_ADMIN", "CODE", "PAYMENT_APPROVAL"
    val codeUsed: String? = null
)

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationMonths: Int,
    val priceYmr: Int,
    val description: String,
    val isPopular: Boolean = false,
    val isActive: Boolean = true
)

@Entity(tableName = "subscription_codes")
data class SubscriptionCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. YEM-8K4P-29MX
    val planName: String,
    val durationMonths: Int,
    val priceYmr: Int,
    val status: String = "UNUSED", // "UNUSED", "USED", "CANCELLED", "EXPIRED"
    val tiedStudentPhone: String? = null,
    val createdByAdmin: String = "المدير العام",
    val createdAt: Long = System.currentTimeMillis(),
    val usedByStudentId: Long? = null,
    val usedByStudentName: String? = null,
    val usedAt: Long? = null
)

@Entity(tableName = "payment_requests")
data class PaymentRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val studentName: String,
    val studentPhone: String,
    val planName: String,
    val durationMonths: Int,
    val amountYmr: Int,
    val walletName: String, // "جيب", "جوالي", "ون كاش"
    val transferRefNumber: String,
    val receiptImageUri: String? = null,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String, // "math", "physics", "chemistry", "arabic", "english", "biology", "islamic", "history", "geography"
    val track: String = "BOTH", // "BOTH", "SCIENTIFIC", "LITERARY"
    val description: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val description: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitId: Long,
    val subjectId: Long,
    val title: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterId: Long = 0,
    val unitId: Long,
    val subjectId: Long,
    val title: String,
    val coreIdea: String = "", // المفاهيم الأساسية
    val simplifiedExplanation: String = "", // شرح مختصر وسهل خطوة بخطوة
    val easierExplanation: String = "", // الشرح بطريقة أسهل مع تشبيهات وأمثلة واقعية
    val keyPoints: String = "", // ملاحظات مهمة للطالب للحفظ والفهم
    val formulas: String = "", // نصوص القوانين المهمة والمعادلات
    val symbolsExplanation: String = "", // شرح معنى كل رمز في القانون
    val solvedExample: String = "", // مثال محلول أول خطوة بخطوة
    val solvedExample2: String = "", // مثال محلول ثانٍ خطوة بخطوة
    val solvedExample3: String = "", // مثال ثالث عند الحاجة
    val practiceExercise: String = "", // تمارين للتدريب مع الحل النموذجي
    val isPremium: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val unitId: Long,
    val lessonId: Long? = null,
    val questionType: String, // "MCQ", "TRUE_FALSE", "FILL_BLANK", "SHORT_ANSWER", "MATH_PHYSICS"
    val questionText: String,
    val options: String, // مفصولة بـ "||"
    val correctAnswer: String,
    val explanation: String,
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val isPremium: Boolean = false
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val unitId: Long? = null,
    val title: String,
    val durationMinutes: Int = 30,
    val totalQuestions: Int = 10,
    val passingScore: Int = 50,
    val isPremium: Boolean = false
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val examId: Long,
    val examTitle: String,
    val subjectName: String,
    val scorePercentage: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val dateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_progress")
data class StudentProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val lessonId: Long,
    val subjectId: Long,
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val itemType: String, // "LESSON", "QUESTION", "FORMULA"
    val itemId: Long,
    val title: String,
    val subtitle: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val targetType: String = "ALL", // "ALL", "SCIENTIFIC", "LITERARY", "FREE", "PAID"
    val targetSubjectId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String, // "ACTIVATE_SUBSCRIPTION", "EXTEND_SUBSCRIPTION", "GENERATE_CODE", "PAYMENT_REVIEW", "CONTENT_EDIT"
    val adminName: String = "المدير العام",
    val studentName: String? = null,
    val planName: String? = null,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
