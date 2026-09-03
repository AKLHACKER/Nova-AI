package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_schedules")
data class ClassSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectName: String,
    val teacherName: String = "",
    val classroom: String = "",
    val dayOfWeek: Int, // 1 = Lunes, 2 = Martes, 3 = Miércoles, 4 = Jueves, 5 = Viernes, 6 = Sábado, 7 = Domingo
    val startTime: String, // HH:mm e.g. "08:00"
    val endTime: String, // HH:mm e.g. "09:30"
    val colorHex: String = "#38BDF8",
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
