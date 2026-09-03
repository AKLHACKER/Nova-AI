package com.example.network

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAssistantService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askAssistant(
        userPrompt: String,
        studentName: String,
        academicLevel: String,
        tone: String,
        todaySubjects: List<String> = emptyList(),
        tomorrowSubjects: List<String> = emptyList(),
        pendingTasksCount: Int = 0
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Configura tu GEMINI_API_KEY en el panel de Secrets de Google AI Studio para activar las respuestas en tiempo real.")
            )
        }

        try {
            val systemInstructions = buildString {
                append("Eres el Asistente Escolar y Tutor IA personal de $studentName. ")
                append("Nivel académico del estudiante: $academicLevel. ")
                append("Tono de comunicación: $tone. ")
                append("Dirígete siempre a él/ella por su nombre ($studentName) de forma cercana, respetuosa y muy motivadora. ")
                append("Tu objetivo es ayudarle con explicaciones académicas claras, resolución paso a paso de problemas, resúmenes concisos, consejos de organización y técnicas de estudio (como Pomodoro, Feynman, etc.). ")
                if (todaySubjects.isNotEmpty()) {
                    append("Materias que cursa hoy: ${todaySubjects.joinToString(", ")}. ")
                }
                if (tomorrowSubjects.isNotEmpty()) {
                    append("Materias que cursará mañana: ${tomorrowSubjects.joinToString(", ")}. ")
                }
                append("Tareas pendientes registradas: $pendingTasksCount. ")
                append("CAPACIDAD ESPECIAL DE RESUMEN DE VIDEOS: Si el estudiante te envía un enlace o URL de un video (por ejemplo de YouTube, Khan Academy, Coursera, Vimeo o clases grabadas), o te pide 'resumir este video', analiza el enlace y su temática. Genera un resumen educativo de alto valor pedagógico con esta estructura: ")
                append("1. 🎬 **Tema Central**: De qué trata el video y a qué materia corresponde.\n")
                append("2. 💡 **Conceptos Fundamentales**: Las ideas más importantes que enseña el video.\n")
                append("3. 📝 **Puntos Clave Explicados**: Desglose temático ordenado para estudiar rápidamente.\n")
                append("4. 🧠 **Fórmulas, Definiciones o Reglas**: Lo que debe anotarse en los apuntes.\n")
                append("5. 🎯 **Preguntas Rápidas de Repaso**: 3 preguntas tipo examen con respuesta para comprobar el aprendizaje.\n")
                append("Si el video es privado o inaccesible directamente, proporciona la síntesis del tema evidente en el enlace e invita al estudiante a compartir fragmentos o dudas puntuales.\n")
                append("Sé estructurado, usa viñetas claras, fórmulas legibles y destaca conceptos clave en negrita.")
            }

            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userPrompt)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstructions)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Error del servidor Gemini (${response.code}): $responseBody")
                )
            }

            val jsonObject = JSONObject(responseBody)
            val candidates = jsonObject.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("Respuesta vacía del asistente."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
