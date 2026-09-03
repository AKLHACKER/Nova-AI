package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.liquidGlassCard
import com.example.util.liquidPulseGlow

@Composable
fun LockScreen(
    studentName: String,
    onTriggerBiometric: () -> Unit,
    onUnlockWithPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showPinInput by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ObsidianBlack, MidnightNavy, ObsidianBlack)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(shape = RoundedCornerShape(28.dp), backgroundColor = GlassSurfaceElevated)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .liquidPulseGlow(color = NeonCyan)
                    .clip(CircleShape)
                    .background(ColorGlassBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Autenticación Biométrica",
                    tint = NeonCyan,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Acceso Protegido",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hola $studentName, autentícate para proteger tus horarios, notas y entregas escolares.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onTriggerBiometric,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("biometric_unlock_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Desbloquear con Biometría",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = { showPinInput = !showPinInput },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_pin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = if (showPinInput) "Ocultar PIN" else "Ingresar con PIN",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            AnimatedVisibility(
                visible = showPinInput,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = pinValue,
                        onValueChange = {
                            if (it.length <= 6) {
                                pinValue = it
                                pinError = false
                            }
                        },
                        label = { Text("PIN de seguridad (defecto: 1234)", fontSize = 12.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = pinError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedLabelColor = NeonCyan,
                            unfocusedLabelColor = TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pin_input_field")
                    )

                    if (pinError) {
                        Text(
                            text = "PIN incorrecto. Intenta de nuevo.",
                            color = AccentRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val success = onUnlockWithPin(pinValue)
                            if (!success) {
                                pinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_pin_button")
                    ) {
                        Text("Verificar PIN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private val ColorGlassBg = androidx.compose.ui.graphics.Color(0x2E38BDF8)
