package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ClassSchedule
import com.example.data.local.entity.TaskItem
import com.example.ui.SchoolViewModel
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.Blue400
import com.example.ui.theme.Blue500
import com.example.ui.theme.Blue600
import com.example.ui.theme.BluePillBg
import com.example.ui.theme.FrostedBorder
import com.example.ui.theme.FrostedBorderHighlight
import com.example.ui.theme.FrostedBorderSubtle
import com.example.ui.theme.FrostedSurface
import com.example.ui.theme.FrostedSurfaceElevated
import com.example.ui.theme.FrostedSurfaceSubtle
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.frostedGlassCard
import com.example.util.frostedGlassElevated
import com.example.util.liquidGlassCard
import com.example.util.liquidGlassElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    studentName: String,
    todayDayOfWeek: Int,
    tomorrowDayOfWeek: Int,
    todaySchedules: List<ClassSchedule>,
    tomorrowSchedules: List<ClassSchedule>,
    pendingTasks: List<TaskItem>,
    onToggleTask: (TaskItem) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayName = remember(todayDayOfWeek) { SchoolViewModel.getDayName(todayDayOfWeek) }
    val tomorrowName = remember(tomorrowDayOfWeek) { SchoolViewModel.getDayName(tomorrowDayOfWeek) }
    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Assistant Nova Prompt Pill (from Frosted Glass design HTML)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlassCard(shape = RoundedCornerShape(26.dp), backgroundColor = FrostedSurface)
                    .clickable { onNavigateToAi() }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Blue500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Pregúntale a ",
                                    color = Slate300,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tutor Nova",
                                    color = Blue400,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Tareas, dudas y resúmenes al instante",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FrostedSurface)
                            .border(1.dp, FrostedBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voz",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Quick Metric Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMetricPill(
                    label = "HOY",
                    count = "${todaySchedules.size} Materias",
                    subtext = todayName,
                    color = Blue400,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSchedule
                )
                QuickMetricPill(
                    label = "MAÑANA",
                    count = "${tomorrowSchedules.size} Materias",
                    subtext = "Ver programa",
                    color = Blue400,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSchedule
                )
                QuickMetricPill(
                    label = "PENDIENTES",
                    count = "${pendingTasks.size} Tareas",
                    subtext = if (pendingTasks.isNotEmpty()) "Por entregar" else "Al día",
                    color = if (pendingTasks.isNotEmpty()) AccentOrange else AccentGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )
            }
        }

        // Section: Horario de Hoy
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HORARIO DE HOY",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BluePillBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (todaySchedules.isNotEmpty()) "${todaySchedules.size} en curso" else "Libre",
                        color = Blue400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (todaySchedules.isEmpty()) {
            item {
                EmptyStateScheduleCard(
                    title = "Sin materias hoy",
                    description = "No tienes clases registradas para este día. Disfruta tu tiempo o adelanta tareas.",
                    actionLabel = "Añadir a mi horario",
                    onAction = onNavigateToSchedule
                )
            }
        } else {
            items(todaySchedules, key = { it.id }) { schedule ->
                ClassScheduleCard(
                    schedule = schedule,
                    isToday = true
                )
            }
        }

        // Section: Materias de Mañana (Separación explícita solicitada)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MATERIAS DE MAÑANA",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FrostedSurface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${tomorrowSchedules.size} para preparar",
                        color = Slate300,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (tomorrowSchedules.isEmpty()) {
            item {
                EmptyStateScheduleCard(
                    title = "Sin materias para mañana",
                    description = "Tu horario para mañana está libre o no has registrado materias aún.",
                    actionLabel = "Configurar horario",
                    onAction = onNavigateToSchedule
                )
            }
        } else {
            items(tomorrowSchedules, key = { it.id }) { schedule ->
                ClassScheduleCard(
                    schedule = schedule,
                    isToday = false
                )
            }
        }

        // Section: Pendientes & Notas
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENDIENTES & NOTAS",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "Ver todas",
                    color = Blue400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToTasks() }
                )
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                EmptyStateTaskCard(
                    title = "¡Al día con tus pendientes!",
                    description = "No tienes tareas ni entregas urgentes por completar.",
                    actionLabel = "Registrar nueva tarea",
                    onAction = onNavigateToTasks
                )
            }
        } else {
            items(pendingTasks.take(4), key = { it.id }) { task ->
                TaskRowCard(
                    task = task,
                    onToggle = { onToggleTask(task) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickMetricPill(
    label: String,
    count: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .frostedGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = FrostedSurface)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Slate500,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                color = Slate100,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ClassScheduleCard(
    schedule: ClassSchedule,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlassCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = if (isToday) FrostedSurfaceElevated else FrostedSurface
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.subjectName,
                        color = Slate100,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val classroomText = if (schedule.classroom.isNotBlank()) "Aula ${schedule.classroom}" else ""
                        val teacherText = if (schedule.teacherName.isNotBlank()) "Prof. ${schedule.teacherName}" else ""
                        val detailString = listOf(classroomText, teacherText)
                            .filter { it.isNotBlank() }
                            .joinToString(" • ")

                        Text(
                            text = if (detailString.isNotBlank()) detailString else "Horario asignado",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(FrostedSurface)
                        .border(1.dp, FrostedBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Blue400,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${schedule.startTime} - ${schedule.endTime}",
                            color = Slate100,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Authentic Frosted Progress Bar as in design HTML
            if (isToday) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(FrostedSurfaceSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(Blue500, Blue400)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRowCard(
    task: TaskItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barColor = if (task.isCompleted) Slate700 else Blue500

    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = FrostedSurface)
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Vertical accent indicator from design HTML
                Box(
                    modifier = Modifier
                        .size(4.dp, 36.dp)
                        .clip(CircleShape)
                        .background(barColor)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) Slate500 else Slate100,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildString {
                            if (task.subject.isNotBlank()) append("${task.subject} • ")
                            if (task.dueTime.isNotBlank()) append("Entrega ${task.dueTime}")
                            if (task.subject.isBlank() && task.dueTime.isBlank()) append("Guardado local")
                        },
                        color = Slate500,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (task.isCompleted) AccentGreen else Slate600,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmptyStateScheduleCard(
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = FrostedSurface)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Class,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Slate100,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Slate400,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FrostedBorder)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Blue400)
                Spacer(modifier = Modifier.width(6.dp))
                Text(actionLabel, color = Blue400, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmptyStateTaskCard(
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = FrostedSurface)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Slate100,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Slate400,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FrostedBorder)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Blue400)
                Spacer(modifier = Modifier.width(6.dp))
                Text(actionLabel, color = Blue400, fontSize = 12.sp)
            }
        }
    }
}
