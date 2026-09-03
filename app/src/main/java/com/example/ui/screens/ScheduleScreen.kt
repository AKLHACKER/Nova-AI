package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ClassSchedule
import com.example.ui.SchoolViewModel
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.liquidGlassCard

@Composable
fun ScheduleScreen(
    schedules: List<ClassSchedule>,
    currentDayOfWeek: Int,
    onAddSchedule: (String, String, String, Int, String, String, String) -> Unit,
    onDeleteSchedule: (ClassSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableIntStateOf(currentDayOfWeek) }
    var showAddDialog by remember { mutableStateOf(false) }

    val days = listOf(
        1 to "Lun",
        2 to "Mar",
        3 to "Mié",
        4 to "Jue",
        5 to "Vie",
        6 to "Sáb",
        7 to "Dom"
    )

    val filteredSchedules = remember(schedules, selectedDay) {
        schedules.filter { it.dayOfWeek == selectedDay }.sortedBy { it.startTime }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Days of the week row selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { (dayNum, dayLabel) ->
                    val isSelected = dayNum == selectedDay
                    val isToday = dayNum == currentDayOfWeek

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyberBlue else GlassSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonCyan else GlassBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedDay = dayNum }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayLabel,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) TextPrimary else NeonCyan)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-header for selected day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Horario: ${SchoolViewModel.getDayName(selectedDay)}",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${filteredSchedules.size} materias",
                    color = NeonCyan,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List of schedules
            if (filteredSchedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurface)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Sin clases registradas para este día",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Agrega tus materias y asignaturas para organizar tu jornada escolar.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberBlue,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Añadir Materia")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredSchedules, key = { it.id }) { item ->
                        ScheduleDetailCard(
                            schedule = item,
                            onDelete = { onDeleteSchedule(item) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Class
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NeonCyan,
            contentColor = MidnightNavy,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_schedule_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir materia al horario")
        }
    }

    if (showAddDialog) {
        AddScheduleDialog(
            defaultDay = selectedDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, teacher, room, day, start, end, color ->
                onAddSchedule(name, teacher, room, day, start, end, color)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ScheduleDetailCard(
    schedule: ClassSchedule,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(schedule.colorHex))
    } catch (_: Exception) {
        NeonCyan
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = GlassSurfaceElevated)
            .padding(16.dp)
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
                Box(
                    modifier = Modifier
                        .size(4.dp, 48.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = schedule.subjectName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = SoftCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${schedule.startTime} a ${schedule.endTime}",
                            color = SoftCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (schedule.classroom.isNotBlank() || schedule.teacherName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = buildString {
                                if (schedule.classroom.isNotBlank()) append("Salón: ${schedule.classroom}  ")
                                if (schedule.teacherName.isNotBlank()) append("Prof: ${schedule.teacherName}")
                            },
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar materia",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddScheduleDialog(
    defaultDay: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, String, String, String) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var classroom by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableIntStateOf(defaultDay) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:30") }
    var selectedColor by remember { mutableStateOf("#38BDF8") }

    val colorOptions = listOf(
        "#38BDF8", "#3B82F6", "#10B981", "#8B5CF6", "#F59E0B", "#EC4899"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightNavy,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Nueva Materia en Horario",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Nombre de la Materia", fontSize = 12.sp) },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_name_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classroom,
                        onValueChange = { classroom = it },
                        label = { Text("Aula / Salón", fontSize = 12.sp) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Profesor(a)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Inicio (HH:mm)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Fin (HH:mm)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Color de etiqueta:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 0.dp,
                                    color = if (isSelected) TextPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subjectName.isNotBlank()) {
                        onConfirm(subjectName, teacherName, classroom, dayOfWeek, startTime, endTime, selectedColor)
                    }
                },
                enabled = subjectName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = NeonCyan,
    unfocusedLabelColor = TextMuted
)
