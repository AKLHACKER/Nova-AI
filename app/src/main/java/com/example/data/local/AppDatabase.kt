package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.ScheduleDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.ClassSchedule
import com.example.data.local.entity.SchoolNote
import com.example.data.local.entity.TaskItem

@Database(
    entities = [ClassSchedule::class, TaskItem::class, SchoolNote::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_assistant.db"
                ).fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
