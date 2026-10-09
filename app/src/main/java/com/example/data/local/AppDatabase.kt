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
        SubjectEntity::class,
        UnitEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        ExamEntity::class,
        ExamQuestionCrossRef::class,
        UserProgressEntity::class,
        FavoriteEntity::class,
        SubscriptionEntity::class,
        PaymentRequestEntity::class,
        SubscriptionCodeEntity::class,
        AdminLogEntity::class,
        DocumentBookletEntity::class,
        ExamResultEntity::class,
        NotificationEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun curriculumDao(): CurriculumDao
    abstract fun questionDao(): QuestionDao
    abstract fun examDao(): ExamDao
    abstract fun progressDao(): ProgressDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun adminLogDao(): AdminLogDao
    abstract fun documentBookletDao(): DocumentBookletDao
    abstract fun examResultDao(): ExamResultDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raqeeb_academy_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
