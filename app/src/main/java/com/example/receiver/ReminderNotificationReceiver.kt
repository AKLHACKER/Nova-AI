package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class ReminderNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_SUBJECT = "extra_task_subject"
        const val EXTRA_TASK_TYPE = "extra_task_type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Recordatorio escolar"
        val subject = intent.getStringExtra(EXTRA_TASK_SUBJECT) ?: ""
        val type = intent.getStringExtra(EXTRA_TASK_TYPE) ?: "Entrega"

        val notificationTitle = "¡Atención: $type pendiente!"
        val notificationMessage = "Recuerda entregar: $title"

        NotificationHelper.showInstantNotification(
            context = context,
            notificationId = taskId.toInt().takeIf { it != 0 } ?: (System.currentTimeMillis() % 10000).toInt(),
            title = notificationTitle,
            message = notificationMessage,
            subject = subject
        )
    }
}
