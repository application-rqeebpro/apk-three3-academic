package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        SubscriptionEntity::class,
        SubscriptionPlanEntity::class,
        SubscriptionCodeEntity::class,
        PaymentRequestEntity::class,
        SubjectEntity::class,
        UnitEntity::class,
        ChapterEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        ExamEntity::class,
        ExamResultEntity::class,
        StudentProgressEntity::class,
        FavoriteEntity::class,
        NotificationEntity::class,
        ActivityLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun subscriptionPlanDao(): SubscriptionPlanDao
    abstract fun subscriptionCodeDao(): SubscriptionCodeDao
    abstract fun paymentRequestDao(): PaymentRequestDao
    abstract fun curriculumDao(): CurriculumDao
    abstract fun questionDao(): QuestionDao
    abstract fun examDao(): ExamDao
    abstract fun progressDao(): ProgressDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun notificationDao(): NotificationDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yemen_secondary_academy.db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
