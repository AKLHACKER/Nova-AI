package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("school_assistant_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_STUDENT_NAME = "key_student_name"
        private const val KEY_ACADEMIC_LEVEL = "key_academic_level"
        private const val KEY_AI_TONE = "key_ai_tone"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_CLOUD_SYNC_ENABLED = "key_cloud_sync_enabled"
        private const val KEY_LAST_SYNC_TIME = "key_last_sync_time"
        private const val KEY_LAPTOP_SYNCED = "key_laptop_synced"
        private const val KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled"
        private const val KEY_PIN_CODE = "key_pin_code"
        private const val KEY_PROFILE_IMAGE_PATH = "key_profile_image_path"
    }

    var profileImagePath: String?
        get() = prefs.getString(KEY_PROFILE_IMAGE_PATH, null)
        set(value) = prefs.edit().putString(KEY_PROFILE_IMAGE_PATH, value).apply()

    var studentName: String
        get() = prefs.getString(KEY_STUDENT_NAME, "Estudiante") ?: "Estudiante"
        set(value) = prefs.edit().putString(KEY_STUDENT_NAME, value).apply()

    var academicLevel: String
        get() = prefs.getString(KEY_ACADEMIC_LEVEL, "Bachillerato / Preparatoria") ?: "Bachillerato / Preparatoria"
        set(value) = prefs.edit().putString(KEY_ACADEMIC_LEVEL, value).apply()

    var aiTone: String
        get() = prefs.getString(KEY_AI_TONE, "Tutor Motivador") ?: "Tutor Motivador"
        set(value) = prefs.edit().putString(KEY_AI_TONE, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var isCloudSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_CLOUD_SYNC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_CLOUD_SYNC_ENABLED, value).apply()

    var lastSyncTimestamp: Long
        get() = prefs.getLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_TIME, value).apply()

    var isLaptopSynced: Boolean
        get() = prefs.getBoolean(KEY_LAPTOP_SYNCED, true)
        set(value) = prefs.edit().putBoolean(KEY_LAPTOP_SYNCED, value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var pinCode: String
        get() = prefs.getString(KEY_PIN_CODE, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()
}
