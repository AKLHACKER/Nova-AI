package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC, dueTime ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC, dueTime ASC")
    fun getPendingTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay ORDER BY dueTime ASC")
    fun getTasksForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :id")
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
