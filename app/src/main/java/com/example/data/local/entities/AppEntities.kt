package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["phoneOrEmail"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phoneOrEmail: String,
    val passwordHash: String,
    val role: String = "STUDENT", // "STUDENT" or "ADMIN"
    val grade: String = "الصف الثالث الثانوي",
    val track: String = "علمي",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val iconRes: String,
    val colorHex: String,
    val track: String = "مشترك"
)

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class UnitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val unitNumber: Int,
    val title: String,
    val description: String
)

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("unitId"), Index("subjectId")]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: Long,
    val subjectId: Long,
    val lessonNumber: Int,
    val title: String,
    val coreIdea: String,
    val simplifiedExplanation: String,
    val formulas: String = "",
    val symbolsExplanation: String = "",
    val keyPoints: String = "",
    val solvedExample: String = "",
    val solvedExample2: String = "",
    val solvedExample3: String = "",
    val practiceExercise: String = "",
    val easierExplanation: String = ""
)

@Entity(
    tableName = "questions",
    indices = [Index("lessonId"), Index("subjectId")]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Long,
    val subjectId: Long,
    val questionText: String,
    val type: String = "MULTIPLE_CHOICE", // "MULTIPLE_CHOICE" or "TRUE_FALSE"
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctOption: String, // "A", "B", "C", "D" or "TRUE", "FALSE"
    val explanation: String = "",
    val difficulty: String = "MEDIUM"
)

@Entity(tableName = "exams", indices = [Index("subjectId")])
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val durationMinutes: Int = 30,
    val totalQuestions: Int = 10,
    val track: String = "علمي"
)

@Entity(
    tableName = "exam_questions",
    primaryKeys = ["examId", "questionId"],
    indices = [Index("questionId")]
)
data class ExamQuestionCrossRef(
    val examId: Long,
    val questionId: Long
)

@Entity(
    tableName = "user_progress",
    indices = [Index("userId"), Index("lessonId"), Index("subjectId")]
)
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val lessonId: Long,
    val subjectId: Long,
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "favorites",
    indices = [Index("userId")]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val itemType: String, // "LESSON", "QUESTION", "FORMULA"
    val itemId: Long,
    val title: String,
    val subtitle: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subscriptions",
    indices = [Index("userId")]
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val planName: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long,
    val isActive: Boolean = true,
    val paymentMethod: String = "WALLET",
    val paymentRef: String = ""
)

@Entity(tableName = "payment_requests", indices = [Index("userId")])
data class PaymentRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val studentName: String,
    val studentPhone: String,
    val walletType: String, // "ONE_CASH", "JAIB", "JAWALI"
    val senderNumber: String,
    val transferRef: String,
    val amount: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val requestDate: Long = System.currentTimeMillis(),
    val approvedDate: Long? = null
)

@Entity(tableName = "subscription_codes", indices = [Index(value = ["code"], unique = true)])
data class SubscriptionCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val planName: String,
    val durationDays: Int,
    val isUsed: Boolean = false,
    val usedByUserId: Long? = null,
    val usedDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val performedBy: String = "المدير العام",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "document_booklets")
data class DocumentBookletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectId: Long,
    val subjectName: String,
    val grade: String = "الصف الثالث الثانوي",
    val description: String = "",
    val fileType: String = "PDF", // "PDF", "WORD"
    val fileName: String,
    val filePath: String? = null,
    val fileSize: String = "1.2 MB",
    val uploadedAt: Long = System.currentTimeMillis(),
    val uploadedBy: String = "المدير العام للمنصة"
)

@Entity(
    tableName = "exam_results",
    indices = [Index("userId"), Index("examId")]
)
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val examId: Long,
    val subjectId: Long,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val isPassed: Boolean,
    val takenAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val target: String = "ALL", // "ALL", "STUDENTS", "OWNER"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
