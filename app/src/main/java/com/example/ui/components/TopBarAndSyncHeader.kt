package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.Blue400
import com.example.ui.theme.Blue500
import com.example.ui.theme.Blue700
import com.example.ui.theme.FrostedBorder
import com.example.ui.theme.FrostedBorderHighlight
import com.example.ui.theme.FrostedSurface
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500

@Composable
fun TopBarAndSyncHeader(
    studentName: String,
    profileImagePath: String? = null,
    isOnline: Boolean,
    isSyncing: Boolean,
    lastSyncText: String,
    isBiometricEnabled: Boolean,
    onSyncClick: () -> Unit,
    onLockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by if (isSyncing) {
        val transition = rememberInfiniteTransition(label = "sync_spin")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val initialLetter = studentName.trim().firstOrNull()?.uppercase() ?: "A"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Avatar + Greeting & Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rounded avatar with gradient or custom image
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Blue500, Blue700)
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = FrostedBorderHighlight,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!profileImagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = profileImagePath,
                        contentDescription = "Foto de perfil de $studentName",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Text(
                        text = initialLetter,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = "BIENVENIDO",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Text(
                    text = studentName.ifBlank { "Estudiante" },
                    color = Slate100,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) AccentGreen else AccentOrange)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "Sincronizado • $lastSyncText" else "Offline (guardado local)",
                        color = Slate500,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Right Action Buttons: Cloud Sync & Biometric / Lock
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cloud Sync Box (w-10 h-10 rounded-xl bg-white/5 border border-white/10)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FrostedSurface)
                    .border(1.dp, FrostedBorder, RoundedCornerShape(12.dp))
                    .clickable { onSyncClick() }
                    .testTag("sync_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSyncing) Icons.Default.Sync else if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                    contentDescription = "Sincronizar ahora",
                    tint = if (isSyncing || isOnline) Blue400 else Slate400,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation)
                )
            }

            // Lock / Biometric Box
            if (isBiometricEnabled) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FrostedSurface)
                        .border(1.dp, FrostedBorder, RoundedCornerShape(12.dp))
                        .clickable { onLockClick() }
                        .testTag("lock_app_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Bloquear aplicación",
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

