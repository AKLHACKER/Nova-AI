package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ClassSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM class_schedules ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSchedules(): Flow<List<ClassSchedule>>

    @Query("SELECT * FROM class_schedules WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getSchedulesForDay(dayOfWeek: Int): Flow<List<ClassSchedule>>

    @Query("SELECT * FROM class_schedules WHERE id = :id LIMIT 1")
    suspend fun getScheduleById(id: Long): ClassSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ClassSchedule): Long

    @Update
    suspend fun updateSchedule(schedule: ClassSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: ClassSchedule)

    @Query("DELETE FROM class_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("DELETE FROM class_schedules")
    suspend fun deleteAllSchedules()
}
