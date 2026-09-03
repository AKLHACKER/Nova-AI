package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.TopBarAndSyncHeader
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartCalendarScreen
import com.example.ui.screens.TasksAndNotesScreen
import com.example.ui.theme.Blue400
import com.example.ui.theme.CanvasBlack
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.FrostedBorder
import com.example.ui.theme.FrostedSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.Slate500
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BiometricHelper
import com.example.util.frostedAmbientBackground
import com.example.util.frostedGlassCard
import com.example.util.liquidGlassCard
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // State collections
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncText by viewModel.lastSyncFormatted.collectAsState()
    val profileImagePath by viewModel.profileImagePath.collectAsState()
    val studentName by viewModel.studentName.collectAsState()
    val academicLevel by viewModel.academicLevel.collectAsState()
    val aiTone by viewModel.aiTone.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    val currentDayOfWeek by viewModel.currentDayOfWeek.collectAsState()
    val tomorrowDayOfWeek by viewModel.tomorrowDayOfWeek.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val tomorrowSchedules by viewModel.tomorrowSchedules.collectAsState()

    val allTasks by viewModel.allTasks.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val allNotes by viewModel.allNotes.collectAsState()

    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    // Request notification permission if Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                try {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } catch (e: Exception) {
                    android.util.Log.e("MainScreen", "Could not request notification permission", e)
                }
            }
        }
    }

    // Biometric prompt trigger
    fun triggerBiometricAuth() {
        if (activity != null) {
            BiometricHelper.authenticate(
                activity = activity,
                onSuccess = {
                    viewModel.unlockApp()
                },
                onError = { err ->
                    scope.launch {
                        snackbarHostState.showSnackbar(err)
                    }
                }
            )
        } else {
            viewModel.unlockApp()
        }
    }

    // Auto-prompt biometrics when screen is locked
    LaunchedEffect(isAppLocked) {
        if (isAppLocked && isBiometricEnabled) {
            triggerBiometricAuth()
        }
    }

    if (isAppLocked) {
        LockScreen(
            studentName = studentName,
            onTriggerBiometric = { triggerBiometricAuth() },
            onUnlockWithPin = { inputPin ->
                if (inputPin == viewModel.preferences.pinCode) {
                    viewModel.unlockApp()
                    true
                } else {
                    false
                }
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xD9020408),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .frostedGlassCard(
                            shape = RoundedCornerShape(26.dp),
                            backgroundColor = FrostedSurface,
                            borderColor = FrostedBorder
                        )
                ) {
                    val navItems = listOf(
                        Triple(0, "Hoy", Icons.Default.Today),
                        Triple(1, "Horario", Icons.Default.Class),
                        Triple(2, "Tareas", Icons.Default.Assignment),
                        Triple(3, "Calendario", Icons.Default.Event),
                        Triple(4, "Tutor IA", Icons.Default.AutoAwesome),
                        Triple(5, "Ajustes", Icons.Default.Settings)
                    )

                    navItems.forEach { (index, label, icon) ->
                        val isSelected = selectedTabIndex == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue400,
                                unselectedIconColor = Slate500,
                                selectedTextColor = Blue400,
                                unselectedTextColor = Slate500,
                                indicatorColor = Color(0x263B82F6) // bg-blue-500/15
                            )
                        )
                    }
                }
            },
            containerColor = CanvasBlack,
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(paddingValues)
                    .frostedAmbientBackground()
            ) {
                // Top Sync Header
                TopBarAndSyncHeader(
                    studentName = studentName,
                    profileImagePath = profileImagePath,
                    isOnline = isOnline,
                    isSyncing = isSyncing,
                    lastSyncText = lastSyncText,
                    isBiometricEnabled = isBiometricEnabled,
                    onSyncClick = {
                        viewModel.syncNow()
                        scope.launch {
                            snackbarHostState.showSnackbar("Sincronización en la nube completada.")
                        }
                    },
                    onLockClick = { viewModel.lockApp() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                // Animated Tab Content
                AnimatedContent(
                    targetState = selectedTabIndex,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_animation",
                    modifier = Modifier.weight(1f)
                ) { targetTab ->
                    when (targetTab) {
                        0 -> DashboardScreen(
                            studentName = studentName,
                            todayDayOfWeek = currentDayOfWeek,
                            tomorrowDayOfWeek = tomorrowDayOfWeek,
                            todaySchedules = todaySchedules,
                            tomorrowSchedules = tomorrowSchedules,
                            pendingTasks = pendingTasks,
                            onToggleTask = { viewModel.toggleTaskCompletion(it) },
                            onNavigateToSchedule = { selectedTabIndex = 1 },
                            onNavigateToTasks = { selectedTabIndex = 2 },
                            onNavigateToAi = { selectedTabIndex = 4 }
                        )
                        1 -> ScheduleScreen(
                            schedules = allSchedules,
                            currentDayOfWeek = currentDayOfWeek,
                            onAddSchedule = { name, teacher, room, day, start, end, color ->
                                viewModel.addSchedule(name, teacher, room, day, start, end, color)
                            },
                            onDeleteSchedule = { viewModel.deleteSchedule(it) }
                        )
                        2 -> TasksAndNotesScreen(
                            tasks = allTasks,
                            notes = allNotes,
                            onAddTask = { title, desc, subject, date, time, prio, type, rem ->
                                viewModel.addTask(title, desc, subject, date, time, prio, type, rem)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Tarea guardada con recordatorio programado.")
                                }
                            },
                            onToggleTask = { viewModel.toggleTaskCompletion(it) },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onAddNote = { title, content, subject, color ->
                                viewModel.addNote(title, content, subject, color)
                            },
                            onToggleNotePin = { viewModel.toggleNotePin(it) },
                            onDeleteNote = { viewModel.deleteNote(it) }
                        )
                        3 -> SmartCalendarScreen(
                            tasks = allTasks,
                            schedules = allSchedules,
                            selectedDateMs = selectedCalendarDate,
                            onSelectDate = { viewModel.selectCalendarDate(it) },
                            onToggleTask = { viewModel.toggleTaskCompletion(it) }
                        )
                        4 -> AiAssistantScreen(
                            studentName = studentName,
                            academicLevel = academicLevel,
                            aiTone = aiTone,
                            messages = chatMessages,
                            isLoading = isAiLoading,
                            onSendMessage = { viewModel.askAiAssistant(it) },
                            onClearChat = { viewModel.clearChat() }
                        )
                        5 -> SettingsScreen(
                            studentName = studentName,
                            profileImagePath = profileImagePath,
                            academicLevel = academicLevel,
                            aiTone = aiTone,
                            isBiometricEnabled = isBiometricEnabled,
                            isOnline = isOnline,
                            lastSyncText = lastSyncText,
                            onUpdateName = {
                                viewModel.updateStudentName(it)
                                scope.launch { snackbarHostState.showSnackbar("Nombre actualizado") }
                            },
                            onPickProfileImage = { uri ->
                                viewModel.updateProfileImage(uri)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (uri != null) "Foto de perfil actualizada" else "Foto de perfil eliminada"
                                    )
                                }
                            },
                            onUpdateAcademicLevel = { viewModel.updateAcademicLevel(it) },
                            onUpdateAiTone = { viewModel.updateAiTone(it) },
                            onToggleBiometric = { viewModel.setBiometricEnabled(it) },
                            onSyncNow = {
                                viewModel.syncNow()
                                scope.launch { snackbarHostState.showSnackbar("Sincronización en la nube completada.") }
                            },
                            onTestNotification = {
                                viewModel.sendTestNotification()
                                scope.launch { snackbarHostState.showSnackbar("Notificación enviada al sistema.") }
                            },
                            onClearAllData = {
                                viewModel.clearAllData()
                                scope.launch { snackbarHostState.showSnackbar("Base de datos limpiada correctamente.") }
                            }
                        )
                    }
                }
            }
        }
    }
}
