package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SchoolNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM school_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<SchoolNote>>

    @Query("SELECT * FROM school_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): SchoolNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SchoolNote): Long

    @Update
    suspend fun updateNote(note: SchoolNote)

    @Delete
    suspend fun deleteNote(note: SchoolNote)

    @Query("DELETE FROM school_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM school_notes")
    suspend fun deleteAllNotes()
}
