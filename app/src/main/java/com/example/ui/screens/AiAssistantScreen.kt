package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.ChatMessage
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
import com.example.util.liquidPulseGlow

@Composable
fun AiAssistantScreen(
    studentName: String,
    academicLevel: String,
    aiTone: String,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val promptSuggestions = listOf(
        "🎬 Resumir video educativo",
        "📐 Explicar paso a paso",
        "📝 Resumir mis notas",
        "🎯 Crear guía de estudio",
        "🧠 Técnica Feynman",
        "⏰ Plan Pomodoro para hoy"
    )

    val isVideoLink = remember(inputText) {
        val lower = inputText.lowercase()
        (lower.contains("http://") || lower.contains("https://")) &&
            (lower.contains("youtu") || lower.contains("vimeo") || lower.contains("video") ||
             lower.contains("ted.com") || lower.contains("coursera") || lower.contains("khanacademy") ||
             lower.contains("tiktok") || lower.contains("loom.com"))
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // AI Header card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurfaceElevated)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .liquidPulseGlow(color = NeonCyan)
                            .clip(CircleShape)
                            .background(Color(0x3338BDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Tutor Escolar IA (Gemini)",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Nivel: $academicLevel • $aiTone",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                if (messages.isNotEmpty()) {
                    IconButton(onClick = onClearChat) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Limpiar conversación",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prompt Suggestions Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(promptSuggestions) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .clickable {
                            if (chip.startsWith("🎬")) {
                                inputText = "Por favor resume este video educativo, explicándome los conceptos clave y puntos para el examen: "
                            } else {
                                onSendMessage(chip)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = chip,
                        color = SoftCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat message list
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .liquidGlassCard(shape = RoundedCornerShape(20.dp), backgroundColor = GlassSurface)
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¡Hola $studentName!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Soy tu tutor personal. Conozco tus materias registradas y puedo ayudarte a resolver dudas, redactar resúmenes, planificar tus exámenes y resumir videos de clases o YouTube con solo enviar el enlace.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "El tutor está analizando...",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Video Link Detection Banner
        AnimatedVisibility(visible = isVideoLink) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2606B6D4))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎬 Enlace de video detectado: Se generará resumen, conceptos y preguntas de repaso",
                    color = SoftCyan,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Input Field and Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(shape = RoundedCornerShape(24.dp), backgroundColor = GlassSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    inputText = "Por favor resume este video educativo y extrae los puntos clave y conceptos para examen: "
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = "Resumir video",
                    tint = if (isVideoLink) NeonCyan else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Pregunta o pega enlace de video...", fontSize = 13.sp, color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = false,
                maxLines = 3,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input")
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) CyberBlue else Color.Transparent)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar pregunta",
                    tint = if (inputText.isNotBlank()) TextPrimary else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .liquidGlassCard(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    ),
                    backgroundColor = if (isUser) CyberBlue else GlassSurfaceElevated
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = if (isUser) "Tú" else "Tutor Gemini",
                    color = if (isUser) SoftCyan else NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
