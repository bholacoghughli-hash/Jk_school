package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Student::class,
        Teacher::class,
        Attendance::class,
        FeeRecord::class,
        Homework::class,
        ExamResult::class,
        LibraryBook::class,
        BookIssue::class,
        TransportRoute::class,
        Hostel::class,
        AdmissionApplication::class,
        Notice::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "jk_school_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
