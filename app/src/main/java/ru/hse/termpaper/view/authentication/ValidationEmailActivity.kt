package ru.hse.termpaper.view.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.authentication.AuthService

class ValidationEmailActivity(
    private var authService: AuthService = AuthService(),
    private var notificationHelper: NotificationHelper? = null
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        notificationHelper = NotificationHelper(baseContext)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_validation_email)

        val validateEmailButton: Button = findViewById(R.id.validateEmailButton)

        validateEmailButton.setOnClickListener {
            authService.checkEmail { isSuccess, message ->
                notificationHelper!!.showToast(message)
                if (isSuccess) {
                    val intent = Intent(this, MainScreenActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, RegistrationActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }
}