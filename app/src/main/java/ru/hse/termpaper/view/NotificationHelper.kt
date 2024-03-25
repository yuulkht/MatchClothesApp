package ru.hse.termpaper.view

import android.content.Context
import android.widget.Toast

class NotificationHelper(private val context: Context) {
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
