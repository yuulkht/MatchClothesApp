package ru.hse.termpaper.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.viewmodel.AuthViewModel

class LoginActivity(
    private var authViewModel: AuthViewModel = AuthViewModel()
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userEmail: EditText = findViewById(R.id.userEmail)
        val userPassword: EditText = findViewById(R.id.userPassword)
        val loginButton: Button = findViewById(R.id.enterButton)

        loginButton.setOnClickListener {
            val inputUserEmail = userEmail.text.toString().trim()
            val inputUserPassword = userPassword.text.toString().trim()

            val user = User(inputUserEmail, inputUserPassword)
            authViewModel.login(user) { isSuccess, message ->
                Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
                if (isSuccess) {
                    val intent = Intent(this, MainScreenActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
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