package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.entity.TaskItem
import com.example.receiver.ReminderNotificationReceiver
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID = "school_tasks_channel"
    const val CHANNEL_NAME = "Recordatorios de Entregas y Clases"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notificaciones inteligentes para entregas, tareas y horarios de clase"
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showInstantNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        subject: String = ""
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val subText = if (subject.isNotBlank()) "Materia: $subject" else null

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_school_1788389828429)
            .setContentTitle(title)
            .setContentText(message)
            .setSubText(subText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted or restricted
        }
    }

    fun scheduleTaskReminder(context: Context, task: TaskItem) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Calculate trigger timestamp
        val parts = task.dueTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            timeInMillis = task.dueDate
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Subtract reminderMinutesBefore
            add(Calendar.MINUTE, -task.reminderMinutesBefore)
        }

        val triggerTime = calendar.timeInMillis
        if (triggerTime <= System.currentTimeMillis()) {
            return // In the past
        }

        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra(ReminderNotificationReceiver.EXTRA_TASK_ID, task.id)
            putExtra(ReminderNotificationReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(ReminderNotificationReceiver.EXTRA_TASK_SUBJECT, task.subject)
            putExtra(ReminderNotificationReceiver.EXTRA_TASK_TYPE, task.type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelTaskReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
