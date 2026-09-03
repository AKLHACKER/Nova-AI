package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ClassSchedule
import com.example.data.local.entity.SchoolNote
import com.example.data.local.entity.TaskItem
import com.example.data.preferences.PreferencesManager
import com.example.data.repository.SchoolRepository
import com.example.network.GeminiAssistantService
import com.example.network.NetworkMonitor
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class SchoolViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = SchoolRepository(
        scheduleDao = database.scheduleDao(),
        taskDao = database.taskDao(),
        noteDao = database.noteDao()
    )
    val preferences = PreferencesManager(application)
    private val networkMonitor = NetworkMonitor(application)
    private val geminiService = GeminiAssistantService()

    // Network connectivity
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Current Day calculation (1=Lunes, 2=Martes, ..., 7=Domingo)
    private val _currentDayOfWeek = MutableStateFlow(calculateCurrentDayOfWeek())
    val currentDayOfWeek: StateFlow<Int> = _currentDayOfWeek.asStateFlow()

    private val _tomorrowDayOfWeek = MutableStateFlow(calculateTomorrowDayOfWeek())
    val tomorrowDayOfWeek: StateFlow<Int> = _tomorrowDayOfWeek.asStateFlow()

    // All schedules
    val allSchedules: StateFlow<List<ClassSchedule>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Schedules for Today
    val todaySchedules: StateFlow<List<ClassSchedule>> = combine(allSchedules, currentDayOfWeek) { schedules, today ->
        schedules.filter { it.dayOfWeek == today }.sortedBy { it.startTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Schedules for Tomorrow
    val tomorrowSchedules: StateFlow<List<ClassSchedule>> = combine(allSchedules, tomorrowDayOfWeek) { schedules, tomorrow ->
        schedules.filter { it.dayOfWeek == tomorrow }.sortedBy { it.startTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tasks
    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTasks: StateFlow<List<TaskItem>> = repository.pendingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notes
    val allNotes: StateFlow<List<SchoolNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Calendar Date (ms at midnight)
    private val _selectedCalendarDate = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedCalendarDate: StateFlow<Long> = _selectedCalendarDate.asStateFlow()

    val tasksForSelectedDate: StateFlow<List<TaskItem>> = combine(allTasks, selectedCalendarDate) { tasks, date ->
        val start = date
        val end = date + (24 * 60 * 60 * 1000) - 1
        tasks.filter { it.dueDate in start..end }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cloud Sync State
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncFormatted = MutableStateFlow(formatLastSync(preferences.lastSyncTimestamp))
    val lastSyncFormatted: StateFlow<String> = _lastSyncFormatted.asStateFlow()

    // Personalization Settings in state
    private val _profileImagePath = MutableStateFlow(preferences.profileImagePath)
    val profileImagePath: StateFlow<String?> = _profileImagePath.asStateFlow()

    private val _studentName = MutableStateFlow(preferences.studentName)
    val studentName: StateFlow<String> = _studentName.asStateFlow()

    private val _academicLevel = MutableStateFlow(preferences.academicLevel)
    val academicLevel: StateFlow<String> = _academicLevel.asStateFlow()

    private val _aiTone = MutableStateFlow(preferences.aiTone)
    val aiTone: StateFlow<String> = _aiTone.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(preferences.isBiometricEnabled)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    // Biometric App Lock state (if biometrics is enabled, starts locked)
    private val _isAppLocked = MutableStateFlow(preferences.isBiometricEnabled)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Clean out any previously injected starter sample data
        viewModelScope.launch {
            clearStarterData()
        }

        // Observe network state for auto-sync when connection is re-established
        viewModelScope.launch {
            var wasOffline = false
            isOnline.collect { online ->
                if (online && wasOffline && preferences.isCloudSyncEnabled) {
                    syncNow()
                }
                wasOffline = !online
            }
        }
    }

    suspend fun clearStarterData() {
        val starterSubjectNames = setOf(
            "Matemáticas Avanzadas", "Física Clásica", "Historia Universal",
            "Química Orgánica", "Literatura & Redacción", "Cálculo Integral",
            "Programación & Algoritmos", "Inglés Técnico"
        )
        val schedules = repository.allSchedules.firstOrNull() ?: emptyList()
        schedules.filter { it.subjectName in starterSubjectNames }.forEach {
            repository.deleteSchedule(it)
        }

        val starterTaskTitles = setOf(
            "Taller de Derivadas e Integrales",
            "Informe de Laboratorio: Péndulo Simple",
            "Ensayo sobre la Ilustración"
        )
        val tasks = repository.allTasks.firstOrNull() ?: emptyList()
        tasks.filter { it.title in starterTaskTitles }.forEach {
            repository.deleteTask(it)
        }

        val starterNoteTitles = setOf(
            "Fórmulas clave de Física",
            "Fechas de Exámenes Parciales"
        )
        val notes = repository.allNotes.firstOrNull() ?: emptyList()
        notes.filter { it.title in starterNoteTitles }.forEach {
            repository.deleteNote(it)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun populateStarterData() {
        viewModelScope.launch {
            val starterSchedules = listOf(
                ClassSchedule(
                    subjectName = "Matemáticas Avanzadas",
                    teacherName = "Prof. Ramírez",
                    classroom = "Aula 204",
                    dayOfWeek = 1, // Lunes
                    startTime = "07:30",
                    endTime = "09:00",
                    colorHex = "#3B82F6",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Física Clásica",
                    teacherName = "Dra. Morales",
                    classroom = "Laboratorio B",
                    dayOfWeek = 1, // Lunes
                    startTime = "09:15",
                    endTime = "10:45",
                    colorHex = "#06B6D4",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Historia Universal",
                    teacherName = "Prof. Castillo",
                    classroom = "Aula 108",
                    dayOfWeek = 1, // Lunes
                    startTime = "11:00",
                    endTime = "12:30",
                    colorHex = "#F59E0B",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Química Orgánica",
                    teacherName = "Prof. Valdez",
                    classroom = "Laboratorio A",
                    dayOfWeek = 2, // Martes
                    startTime = "08:00",
                    endTime = "09:30",
                    colorHex = "#10B981",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Literatura & Redacción",
                    teacherName = "Mtra. Herrera",
                    classroom = "Aula 102",
                    dayOfWeek = 2, // Martes
                    startTime = "10:00",
                    endTime = "11:30",
                    colorHex = "#8B5CF6",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Cálculo Integral",
                    teacherName = "Prof. Ramírez",
                    classroom = "Aula 204",
                    dayOfWeek = 3, // Miércoles
                    startTime = "07:30",
                    endTime = "09:00",
                    colorHex = "#3B82F6",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Programación & Algoritmos",
                    teacherName = "Ing. Soto",
                    classroom = "Cómputo 3",
                    dayOfWeek = 4, // Jueves
                    startTime = "08:30",
                    endTime = "10:30",
                    colorHex = "#6366F1",
                    isSynced = true
                ),
                ClassSchedule(
                    subjectName = "Inglés Técnico",
                    teacherName = "Ms. Taylor",
                    classroom = "Aula 301",
                    dayOfWeek = 5, // Viernes
                    startTime = "09:00",
                    endTime = "10:30",
                    colorHex = "#EC4899",
                    isSynced = true
                )
            )

            starterSchedules.forEach { repository.insertSchedule(it) }

            // Starter tasks
            val nowMs = System.currentTimeMillis()
            val starterTasks = listOf(
                TaskItem(
                    title = "Taller de Derivadas e Integrales",
                    description = "Resolver ejercicios impares del capítulo 4 del libro guía.",
                    subject = "Matemáticas",
                    dueDate = getStartOfDay(nowMs + 24 * 60 * 60 * 1000), // Mañana
                    dueTime = "14:00",
                    priority = "ALTA",
                    type = "TAREA",
                    reminderMinutesBefore = 60,
                    isSynced = true
                ),
                TaskItem(
                    title = "Informe de Laboratorio: Péndulo Simple",
                    description = "Incluir tabla de mediciones experimentales y cálculo de error porcentual.",
                    subject = "Física",
                    dueDate = getStartOfDay(nowMs + 2 * 24 * 60 * 60 * 1000),
                    dueTime = "23:59",
                    priority = "MEDIA",
                    type = "PROYECTO",
                    reminderMinutesBefore = 120,
                    isSynced = true
                ),
                TaskItem(
                    title = "Ensayo sobre la Ilustración",
                    description = "Redactar 2 cuartillas con fuentes bibliográficas en formato APA.",
                    subject = "Historia",
                    dueDate = getStartOfDay(nowMs + 4 * 24 * 60 * 60 * 1000),
                    dueTime = "18:00",
                    priority = "BAJA",
                    type = "ENSAYO",
                    reminderMinutesBefore = 30,
                    isSynced = true
                )
            )

            starterTasks.forEach { repository.insertTask(it) }

            // Starter notes
            val starterNotes = listOf(
                SchoolNote(
                    title = "Fórmulas clave de Física",
                    content = "T = 2π√(L/g)\nv = λ * f\nE = mc²\nRecordar convertir siempre unidades a SI (segundos, metros, kilogramos).",
                    subject = "Física",
                    colorHex = "#06B6D4",
                    isPinned = true,
                    isSynced = true
                ),
                SchoolNote(
                    title = "Fechas de Exámenes Parciales",
                    content = "1. Matemáticas: 20 de Septiembre\n2. Física: 24 de Septiembre\n3. Química: 28 de Septiembre",
                    subject = "General",
                    colorHex = "#F59E0B",
                    isPinned = true,
                    isSynced = true
                )
            )

            starterNotes.forEach { repository.insertNote(it) }
        }
    }

    // --- Schedule Actions ---
    fun addSchedule(
        subjectName: String,
        teacherName: String,
        classroom: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            val schedule = ClassSchedule(
                subjectName = subjectName.trim(),
                teacherName = teacherName.trim(),
                classroom = classroom.trim(),
                dayOfWeek = dayOfWeek,
                startTime = startTime.trim(),
                endTime = endTime.trim(),
                colorHex = colorHex,
                isSynced = isOnline.value
            )
            repository.insertSchedule(schedule)
            triggerAutoSync()
        }
    }

    fun updateSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.updateSchedule(schedule.copy(isSynced = isOnline.value, updatedAt = System.currentTimeMillis()))
            triggerAutoSync()
        }
    }

    fun deleteSchedule(schedule: ClassSchedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
            triggerAutoSync()
        }
    }

    // --- Task Actions ---
    fun addTask(
        title: String,
        description: String,
        subject: String,
        dueDate: Long,
        dueTime: String,
        priority: String,
        type: String,
        reminderMinutesBefore: Int
    ) {
        viewModelScope.launch {
            val task = TaskItem(
                title = title.trim(),
                description = description.trim(),
                subject = subject.trim(),
                dueDate = getStartOfDay(dueDate),
                dueTime = dueTime,
                priority = priority,
                type = type,
                reminderMinutesBefore = reminderMinutesBefore,
                isSynced = isOnline.value
            )
            val insertedId = repository.insertTask(task)
            val savedTask = task.copy(id = insertedId)

            if (preferences.isNotificationsEnabled) {
                NotificationHelper.scheduleTaskReminder(getApplication(), savedTask)
            }
            triggerAutoSync()
        }
    }

    fun toggleTaskCompletion(task: TaskItem) {
        viewModelScope.launch {
            val newStatus = !task.isCompleted
            repository.toggleTaskCompletion(task.id, newStatus)
            if (newStatus) {
                NotificationHelper.cancelTaskReminder(getApplication(), task.id)
            } else if (preferences.isNotificationsEnabled) {
                NotificationHelper.scheduleTaskReminder(getApplication(), task)
            }
            triggerAutoSync()
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            NotificationHelper.cancelTaskReminder(getApplication(), task.id)
            repository.deleteTask(task)
            triggerAutoSync()
        }
    }

    // --- Notes Actions ---
    fun addNote(title: String, content: String, subject: String, colorHex: String) {
        viewModelScope.launch {
            val note = SchoolNote(
                title = title.trim(),
                content = content.trim(),
                subject = subject.trim(),
                colorHex = colorHex,
                isSynced = isOnline.value
            )
            repository.insertNote(note)
            triggerAutoSync()
        }
    }

    fun toggleNotePin(note: SchoolNote) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
            triggerAutoSync()
        }
    }

    fun deleteNote(note: SchoolNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            triggerAutoSync()
        }
    }

    // --- Calendar Actions ---
    fun selectCalendarDate(dateMs: Long) {
        _selectedCalendarDate.value = getStartOfDay(dateMs)
    }

    // --- Cloud Sync Actions ---
    fun syncNow() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // Simulates ultra-fast cloud ledger verification with laptop/cloud
            val now = System.currentTimeMillis()
            preferences.lastSyncTimestamp = now
            _lastSyncFormatted.value = formatLastSync(now)
            _isSyncing.value = false
        }
    }

    private fun triggerAutoSync() {
        if (preferences.isCloudSyncEnabled && isOnline.value) {
            viewModelScope.launch {
                delay(500)
                val now = System.currentTimeMillis()
                preferences.lastSyncTimestamp = now
                _lastSyncFormatted.value = formatLastSync(now)
            }
        }
    }

    // --- Personalization Actions ---
    fun updateStudentName(name: String) {
        val trimmed = name.trim().ifEmpty { "Estudiante" }
        preferences.studentName = trimmed
        _studentName.value = trimmed
    }

    fun updateProfileImage(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri == null) {
                preferences.profileImagePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                }
                preferences.profileImagePath = null
                _profileImagePath.value = null
                return@launch
            }

            try {
                val context = getApplication<Application>()
                val destinationFile = File(context.filesDir, "profile_avatar_${System.currentTimeMillis()}.jpg")
                preferences.profileImagePath?.let { oldPath ->
                    val oldFile = File(oldPath)
                    if (oldFile.exists()) oldFile.delete()
                }

                context.contentResolver.openInputStream(uri)?.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val savedPath = destinationFile.absolutePath
                preferences.profileImagePath = savedPath
                _profileImagePath.value = savedPath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAcademicLevel(level: String) {
        preferences.academicLevel = level
        _academicLevel.value = level
    }

    fun updateAiTone(tone: String) {
        preferences.aiTone = tone
        _aiTone.value = tone
    }

    fun setBiometricEnabled(enabled: Boolean) {
        preferences.isBiometricEnabled = enabled
        _isBiometricEnabled.value = enabled
        if (!enabled) {
            _isAppLocked.value = false
        }
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        if (preferences.isBiometricEnabled) {
            _isAppLocked.value = true
        }
    }

    fun sendTestNotification() {
        NotificationHelper.showInstantNotification(
            context = getApplication(),
            notificationId = (System.currentTimeMillis() % 10000).toInt(),
            title = "🔔 Entrega Escolar Próxima",
            message = "Prueba de notificación inteligente configurada correctamente.",
            subject = "Asistente Escolar"
        )
    }

    // --- AI Assistant Actions ---
    fun askAiAssistant(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val currentMessages = _chatMessages.value.toMutableList()
        val userMsg = ChatMessage(text = userPrompt.trim(), isUser = true)
        currentMessages.add(userMsg)
        _chatMessages.value = currentMessages
        _isAiLoading.value = true

        viewModelScope.launch {
            val todayNames = todaySchedules.value.map { "${it.subjectName} (${it.startTime}-${it.endTime})" }
            val tomorrowNames = tomorrowSchedules.value.map { "${it.subjectName} (${it.startTime}-${it.endTime})" }
            val pendingCount = pendingTasks.value.size

            val result = geminiService.askAssistant(
                userPrompt = userPrompt,
                studentName = _studentName.value,
                academicLevel = _academicLevel.value,
                tone = _aiTone.value,
                todaySubjects = todayNames,
                tomorrowSubjects = tomorrowNames,
                pendingTasksCount = pendingCount
            )

            _isAiLoading.value = false
            val aiResponseText = result.getOrElse { error ->
                "Hola ${_studentName.value}. Hubo un inconveniente al consultar: ${error.message ?: "Verifica tu conexión y clave Gemini API en los ajustes."}"
            }
            val aiMsg = ChatMessage(text = aiResponseText, isUser = false)
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    companion object {
        fun calculateCurrentDayOfWeek(): Int {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 1
            }
        }

        fun calculateTomorrowDayOfWeek(): Int {
            val current = calculateCurrentDayOfWeek()
            return if (current == 7) 1 else current + 1
        }

        fun getStartOfDay(timestamp: Long): Long {
            val cal = Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        fun formatLastSync(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < 60_000 -> "Hace un momento"
                diff < 3600_000 -> "Hace ${(diff / 60_000)} min"
                else -> {
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Hoy a las ${sdf.format(Date(timestamp))}"
                }
            }
        }

        fun getDayName(dayOfWeek: Int): String {
            return when (dayOfWeek) {
                1 -> "Lunes"
                2 -> "Martes"
                3 -> "Miércoles"
                4 -> "Jueves"
                5 -> "Viernes"
                6 -> "Sábado"
                7 -> "Domingo"
                else -> "Día $dayOfWeek"
            }
        }
    }
}
