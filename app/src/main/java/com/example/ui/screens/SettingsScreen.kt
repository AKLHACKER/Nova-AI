package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.Blue500
import com.example.ui.theme.Blue700
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FrostedBorderHighlight
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
fun SettingsScreen(
    studentName: String,
    profileImagePath: String? = null,
    academicLevel: String,
    aiTone: String,
    isBiometricEnabled: Boolean,
    isOnline: Boolean,
    lastSyncText: String,
    onUpdateName: (String) -> Unit,
    onPickProfileImage: (Uri?) -> Unit = {},
    onUpdateAcademicLevel: (String) -> Unit,
    onUpdateAiTone: (String) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onSyncNow: () -> Unit,
    onTestNotification: () -> Unit,
    onClearAllData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var editingName by remember(studentName) { mutableStateOf(studentName) }
    val academicLevels = listOf("Secundaria", "Bachillerato / Prepa", "Universidad")
    val aiTones = listOf("Tutor Motivador", "Académico Riguroso", "Práctico & Conciso")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickProfileImage(uri)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Perfil del Estudiante
        item {
            SectionHeader(title = "Perfil y Personalización", icon = Icons.Default.Person)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurfaceElevated)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Profile Image Selector / Avatar Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar with camera badge
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    brush = Brush.linearGradient(listOf(Blue500, Blue700))
                                )
                                .border(1.5.dp, FrostedBorderHighlight, RoundedCornerShape(22.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profileImagePath.isNullOrBlank()) {
                                AsyncImage(
                                    model = profileImagePath,
                                    contentDescription = "Foto de perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(22.dp))
                                )
                            } else {
                                val initial = studentName.trim().firstOrNull()?.uppercase() ?: "A"
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Camera badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(3.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue)
                                    .border(1.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Cambiar foto",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Foto de Perfil",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Se mostrará en tu saludo diario y credencial.",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("upload_profile_photo_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = SoftCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (profileImagePath.isNullOrBlank()) "Subir Foto" else "Cambiar",
                                        color = SoftCyan,
                                        fontSize = 11.sp
                                    )
                                }

                                if (!profileImagePath.isNullOrBlank()) {
                                    OutlinedButton(
                                        onClick = { onPickProfileImage(null) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("delete_profile_photo_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Quitar foto",
                                            tint = AccentRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "Tu nombre (cómo te llamará el tutor IA):",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editingName,
                            onValueChange = { editingName = it },
                            singleLine = true,
                            colors = dialogTextFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("student_name_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onUpdateName(editingName) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberBlue,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("save_name_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guardar", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Nivel Académico:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        academicLevels.forEach { level ->
                            val isSel = academicLevel == level
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) CyberBlue else GlassSurface)
                                    .clickable { onUpdateAcademicLevel(level) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level,
                                    color = if (isSel) TextPrimary else TextSecondary,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Personalidad del Asistente IA:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        aiTones.forEach { tone ->
                            val isSel = aiTone == tone
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) ElectricBlue else GlassSurface)
                                    .clickable { onUpdateAiTone(tone) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tone,
                                    color = if (isSel) TextPrimary else TextSecondary,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Seguridad y Biometría
        item {
            SectionHeader(title = "Seguridad y Privacidad", icon = Icons.Default.Security)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurface)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x3338BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Autenticación Biométrica",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Exigir huella o rostro al abrir la app",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = onToggleBiometric,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightNavy,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = GlassBorder
                        ),
                        modifier = Modifier.testTag("biometric_switch")
                    )
                }
            }
        }

        // Section: Sincronización en la Nube y Multi-dispositivo
        item {
            SectionHeader(title = "Sincronización Multi-dispositivo", icon = Icons.Default.Devices)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurfaceElevated)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Laptop,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Laptop y otros dispositivos",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isOnline) "Sincronización activa • $lastSyncText" else "Modo offline • Guardando localmente",
                                    color = if (isOnline) AccentGreen else AccentOrange,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = onSyncNow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberBlue,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("settings_sync_button")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sincronizar", fontSize = 11.sp)
                        }
                    }

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
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = SoftCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Base de datos Room SQLite local activa con guardado automático.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = onClearAllData,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Limpiar Datos", color = AccentRed, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section: Notificaciones Push
        item {
            SectionHeader(title = "Alertas y Notificaciones", icon = Icons.Default.NotificationsActive)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Probar Notificación de Entrega",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Genera una alerta instantánea para validar el canal push del sistema.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Probar", color = NeonCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
