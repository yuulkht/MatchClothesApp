package ru.hse.termpaper.view.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.authentication.AuthService

class LoginActivity(
    private var authService: AuthService = AuthService(),
    private var notificationHelper: NotificationHelper? = null
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        notificationHelper = NotificationHelper(baseContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userEmail: EditText = findViewById(R.id.userEmail)
        val userPassword: EditText = findViewById(R.id.userPassword)
        val loginButton: Button = findViewById(R.id.enterButton)

        loginButton.setOnClickListener {
            val inputUserEmail = userEmail.text.toString().trim()
            val inputUserPassword = userPassword.text.toString().trim()

            authService.login(authService.getUser(inputUserEmail, inputUserPassword)) { isSuccess, message ->
                notificationHelper!!.showToast(message)
                if (isSuccess) {
                    val intent = Intent(this, MainScreenActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun onRegisterLinkClicked(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}