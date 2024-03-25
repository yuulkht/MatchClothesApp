package ru.hse.termpaper.view.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ru.hse.termpaper.R
import ru.hse.termpaper.view.NotificationHelper
import ru.hse.termpaper.viewmodel.AuthService

class RegistrationActivity (
    private var authViewModel: AuthService = AuthService(),
    private var notificationHelper: NotificationHelper? = null
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        notificationHelper = NotificationHelper(baseContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userEmail: EditText = findViewById(R.id.userEmail)
        val userPassword: EditText = findViewById(R.id.userPassword)
        val registrationButton: Button = findViewById(R.id.registerButton)

        registrationButton.setOnClickListener {
            val inputUserEmail = userEmail.text.toString().trim()
            val inputUserPassword = userPassword.text.toString().trim()

            authViewModel.register(authViewModel.getUser(inputUserEmail, inputUserPassword)) { isSuccess, message ->
                notificationHelper!!.showToast(message)
                if (isSuccess) {
                    val intent = Intent(this, ValidationEmailActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    fun onEnterLinkClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
