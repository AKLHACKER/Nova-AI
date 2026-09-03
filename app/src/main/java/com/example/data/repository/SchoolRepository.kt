package com.example.data.repository

import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.ScheduleDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.ClassSchedule
import com.example.data.local.entity.SchoolNote
import com.example.data.local.entity.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SchoolRepository(
    private val scheduleDao: ScheduleDao,
    private val taskDao: TaskDao,
    private val noteDao: NoteDao
) {
    // Schedules
    val allSchedules: Flow<List<ClassSchedule>> = scheduleDao.getAllSchedules()

    fun getSchedulesForDay(dayOfWeek: Int): Flow<List<ClassSchedule>> =
        scheduleDao.getSchedulesForDay(dayOfWeek)

    suspend fun insertSchedule(schedule: ClassSchedule): Long = withContext(Dispatchers.IO) {
        scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: ClassSchedule) = withContext(Dispatchers.IO) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: ClassSchedule) = withContext(Dispatchers.IO) {
        scheduleDao.deleteSchedule(schedule)
    }

    suspend fun deleteScheduleById(id: Long) = withContext(Dispatchers.IO) {
        scheduleDao.deleteScheduleById(id)
    }

    // Tasks
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskItem>> = taskDao.getPendingTasks()

    fun getTasksForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<TaskItem>> =
        taskDao.getTasksForDateRange(startOfDay, endOfDay)

    suspend fun insertTask(task: TaskItem): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskItem) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskItem) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(id)
    }

    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.toggleTaskCompletion(id, isCompleted)
    }

    // Notes
    val allNotes: Flow<List<SchoolNote>> = noteDao.getAllNotes()

    suspend fun insertNote(note: SchoolNote): Long = withContext(Dispatchers.IO) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: SchoolNote) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: SchoolNote) = withContext(Dispatchers.IO) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) = withContext(Dispatchers.IO) {
        noteDao.deleteNoteById(id)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        scheduleDao.deleteAllSchedules()
        taskDao.deleteAllTasks()
        noteDao.deleteAllNotes()
    }
}
