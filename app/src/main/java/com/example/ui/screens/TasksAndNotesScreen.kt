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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SchoolNote
import com.example.data.local.entity.TaskItem
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.ElectricBlue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksAndNotesScreen(
    tasks: List<TaskItem>,
    notes: List<SchoolNote>,
    onAddTask: (String, String, String, Long, String, String, String, Int) -> Unit,
    onToggleTask: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onAddNote: (String, String, String, String) -> Unit,
    onToggleNotePin: (SchoolNote) -> Unit,
    onDeleteNote: (SchoolNote) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var taskFilter by remember { mutableStateOf("PENDIENTES") } // TODAS, PENDIENTES, COMPLETADAS
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(tasks, taskFilter) {
        when (taskFilter) {
            "PENDIENTES" -> tasks.filter { !it.isCompleted }
            "COMPLETADAS" -> tasks.filter { it.isCompleted }
            else -> tasks
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Main Top Tabs: Tareas vs Notas
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Tareas & Entregas (${tasks.count { !it.isCompleted }})",
                            color = if (selectedTab == 0) TextPrimary else TextSecondary,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Notas y Apuntes (${notes.size})",
                            color = if (selectedTab == 1) TextPrimary else TextSecondary,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Filter chips for Tasks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PENDIENTES" to "Pendientes", "TODAS" to "Todas", "COMPLETADAS" to "Completadas").forEach { (key, label) ->
                        val isSelected = taskFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyberBlue else GlassSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) NeonCyan else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { taskFilter = key }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredTasks.isEmpty()) {
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
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Sin tareas en esta vista",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Organiza tus deberes escolares y configura notificaciones personalizadas.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddTaskDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberBlue,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Añadir Tarea")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            TaskDetailCard(
                                task = task,
                                onToggle = { onToggleTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }
                }
            } else {
                // Notes View
                if (notes.isEmpty()) {
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
                                imageVector = Icons.Default.Note,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Sin notas guardadas",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Guarda resúmenes, ideas para proyectos o fórmulas importantes.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddNoteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberBlue,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Crear Nota")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onTogglePin = { onToggleNotePin(note) },
                                onDelete = { onDeleteNote(note) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                if (selectedTab == 0) showAddTaskDialog = true else showAddNoteDialog = true
            },
            containerColor = NeonCyan,
            contentColor = MidnightNavy,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_item_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir elemento")
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, subject, dueDate, dueTime, priority, type, reminder ->
                onAddTask(title, desc, subject, dueDate, dueTime, priority, type, reminder)
                showAddTaskDialog = false
            }
        )
    }

    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { title, content, subject, color ->
                onAddNote(title, content, subject, color)
                showAddNoteDialog = false
            }
        )
    }
}

@Composable
fun TaskDetailCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        "ALTA" -> AccentRed
        "MEDIA" -> AccentOrange
        else -> AccentGreen
    }

    val formattedDueDate = remember(task.dueDate) {
        val sdf = SimpleDateFormat("d MMM", Locale("es", "ES"))
        sdf.format(Date(task.dueDate))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = GlassSurfaceElevated)
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
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) AccentGreen else NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) TextMuted else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.subject.isNotBlank()) {
                            Text(
                                text = task.subject,
                                color = SoftCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "•", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = "$formattedDueDate ${task.dueTime}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(priorityColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = task.type,
                        color = priorityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: SchoolNote,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(note.colorHex))
    } catch (_: Exception) {
        NeonCyan
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(18.dp), backgroundColor = GlassSurfaceElevated)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Fijar nota",
                            tint = if (note.isPinned) NeonCyan else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar nota",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (note.subject.isNotBlank()) {
                Text(
                    text = "Materia: ${note.subject}",
                    color = SoftCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = note.content,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("14:00") }
    var priority by remember { mutableStateOf("MEDIA") }
    var type by remember { mutableStateOf("TAREA") }
    var reminderMinutes by remember { mutableIntStateOf(60) } // 15, 60, 1440

    val priorityList = listOf("ALTA", "MEDIA", "BAJA")
    val typeList = listOf("TAREA", "ENTREGA", "EXAMEN", "PROYECTO")
    val reminderOptions = listOf(15 to "15 min", 60 to "1 hora", 1440 to "1 día antes")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightNavy,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Nueva Tarea o Entrega",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la entrega / tarea", fontSize = 12.sp) },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("task_title_input")
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Materia o asignatura", fontSize = 12.sp) },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción o detalles", fontSize = 12.sp) },
                    maxLines = 3,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Hora límite (HH:mm)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = dialogTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Prioridad:", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorityList.forEach { p ->
                        val isSel = priority == p
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CyberBlue else GlassSurface)
                                .clickable { priority = p }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = p, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }

                Text("Tipo de entrega:", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    typeList.forEach { t ->
                        val isSel = type == t
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricBlue else GlassSurface)
                                .clickable { type = t }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = t, color = TextPrimary, fontSize = 10.sp)
                        }
                    }
                }

                Text("Alerta push personalizada:", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    reminderOptions.forEach { (mins, label) ->
                        val isSel = reminderMinutes == mins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonCyan else GlassSurface)
                                .clickable { reminderMinutes = mins }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) MidnightNavy else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            description,
                            subject,
                            System.currentTimeMillis(),
                            dueTime,
                            priority,
                            type,
                            reminderMinutes
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Tarea")
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
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#38BDF8") }

    val colors = listOf("#38BDF8", "#3B82F6", "#10B981", "#8B5CF6", "#F59E0B")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightNavy,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Nueva Nota Escolar",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la nota", fontSize = 12.sp) },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("note_title_input")
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Materia (opcional)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido de la nota", fontSize = 12.sp) },
                    minLines = 3,
                    maxLines = 5,
                    colors = dialogTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        val isSel = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(
                                    width = if (isSel) 2.5.dp else 0.dp,
                                    color = if (isSel) TextPrimary else Color.Transparent,
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
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title, content, subject, selectedColor)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Nota")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
