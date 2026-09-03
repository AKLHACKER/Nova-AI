package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subject: String = "",
    val dueDate: Long, // timestamp in ms (midnight of that day)
    val dueTime: String = "12:00", // HH:mm e.g. "14:30"
    val priority: String = "MEDIA", // ALTA, MEDIA, BAJA
    val type: String = "TAREA", // TAREA, ENTREGA, EXAMEN, PROYECTO
    val isCompleted: Boolean = false,
    val reminderMinutesBefore: Int = 60, // e.g. 15, 60, 1440 (1 day)
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
