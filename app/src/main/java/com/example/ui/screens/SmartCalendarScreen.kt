package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ClassSchedule
import com.example.data.local.entity.TaskItem
import com.example.ui.SchoolViewModel
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.liquidGlassCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SmartCalendarScreen(
    tasks: List<TaskItem>,
    schedules: List<ClassSchedule>,
    selectedDateMs: Long,
    onSelectDate: (Long) -> Unit,
    onToggleTask: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var calendarMonth by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = selectedDateMs })
    }

    val monthTitle = remember(calendarMonth.timeInMillis) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        sdf.format(calendarMonth.time).replaceFirstChar { it.uppercase() }
    }

    // Days in current viewed month
    val daysInMonth = remember(calendarMonth.get(Calendar.YEAR), calendarMonth.get(Calendar.MONTH)) {
        val cal = calendarMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // 1=Sunday in java Calendar, let's normalize Monday=0 .. Sunday=6
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        Triple(maxDays, firstDayOfWeek, cal.get(Calendar.MONTH))
    }

    val selectedCal = remember(selectedDateMs) {
        Calendar.getInstance().apply { timeInMillis = selectedDateMs }
    }

    // Filter tasks for selected date
    val dayTasks = remember(tasks, selectedDateMs) {
        val start = SchoolViewModel.getStartOfDay(selectedDateMs)
        val end = start + (24 * 60 * 60 * 1000) - 1
        tasks.filter { it.dueDate in start..end }
    }

    // Day of week of selected date (1=Mon..7=Sun)
    val selectedDayOfWeek = remember(selectedDateMs) {
        val c = Calendar.getInstance().apply { timeInMillis = selectedDateMs }
        when (c.get(Calendar.DAY_OF_WEEK)) {
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

    val dayClasses = remember(schedules, selectedDayOfWeek) {
        schedules.filter { it.dayOfWeek == selectedDayOfWeek }.sortedBy { it.startTime }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Navigation Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurfaceElevated)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val newCal = calendarMonth.clone() as Calendar
                        newCal.add(Calendar.MONTH, -1)
                        calendarMonth = newCal
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Mes anterior",
                            tint = NeonCyan
                        )
                    }

                    Text(
                        text = monthTitle,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        val newCal = calendarMonth.clone() as Calendar
                        newCal.add(Calendar.MONTH, 1)
                        calendarMonth = newCal
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Mes siguiente",
                            tint = NeonCyan
                        )
                    }
                }
            }
        }

        // Calendar Grid Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(22.dp), backgroundColor = GlassSurface)
                    .padding(14.dp)
            ) {
                Column {
                    // Weekday headers: L M M J V S D
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("L", "M", "M", "J", "V", "S", "D").forEach { d ->
                            Text(
                                text = d,
                                color = SoftCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val (maxDays, firstDayOffset) = daysInMonth
                    val totalSlots = ((maxDays + firstDayOffset + 6) / 7) * 7

                    for (row in 0 until (totalSlots / 7)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            for (col in 0 until 7) {
                                val slotIndex = row * 7 + col
                                val dayNumber = slotIndex - firstDayOffset + 1

                                if (dayNumber in 1..maxDays) {
                                    val cellCal = (calendarMonth.clone() as Calendar).apply {
                                        set(Calendar.DAY_OF_MONTH, dayNumber)
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val cellMs = cellCal.timeInMillis
                                    val isSelected = selectedCal.get(Calendar.YEAR) == cellCal.get(Calendar.YEAR) &&
                                            selectedCal.get(Calendar.DAY_OF_YEAR) == cellCal.get(Calendar.DAY_OF_YEAR)

                                    // Check if there are tasks for this day
                                    val hasTasks = tasks.any {
                                        it.dueDate in cellMs until (cellMs + 24 * 60 * 60 * 1000)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) CyberBlue else Color.Transparent)
                                            .clickable { onSelectDate(cellMs) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNumber",
                                                color = if (isSelected) TextPrimary else TextSecondary,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (hasTasks) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) TextPrimary else AccentOrange)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Details for Selected Day
        item {
            val selectedDateText = remember(selectedDateMs) {
                val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
                sdf.format(Date(selectedDateMs)).replaceFirstChar { it.uppercase() }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDateText,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${dayTasks.size} entregas • ${dayClasses.size} clases",
                    color = NeonCyan,
                    fontSize = 12.sp
                )
            }
        }

        // Tasks for this date
        if (dayTasks.isEmpty() && dayClasses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = GlassSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Día sin actividades registradas",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (dayTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Entregas programadas",
                    color = SoftCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(dayTasks, key = { it.id }) { task ->
                CalendarTaskRow(task = task, onToggle = { onToggleTask(task) })
            }
        }

        if (dayClasses.isNotEmpty()) {
            item {
                Text(
                    text = "Clases del día",
                    color = SoftCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(dayClasses, key = { it.id }) { schedule ->
                CalendarClassRow(schedule = schedule)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CalendarTaskRow(
    task: TaskItem,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(14.dp), backgroundColor = GlassSurfaceElevated)
            .clickable { onToggle() }
            .padding(12.dp)
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
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (task.isCompleted) AccentGreen else NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) TextMuted else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${task.subject} • ${task.dueTime}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x3338BDF8))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = task.type, color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CalendarClassRow(schedule: ClassSchedule) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(14.dp), backgroundColor = GlassSurface)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = schedule.subjectName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (schedule.classroom.isNotBlank()) {
                    Text(
                        text = "Aula: ${schedule.classroom}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "${schedule.startTime} - ${schedule.endTime}",
                color = SoftCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
