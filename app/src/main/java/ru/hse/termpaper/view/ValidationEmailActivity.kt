package ru.hse.termpaper.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.viewmodel.AuthViewModel

class ValidationEmailActivity(
    private var authViewModel: AuthViewModel = AuthViewModel()
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_validation_email)

        val validateEmailButton: Button = findViewById(R.id.validateEmailButton)

        validateEmailButton.setOnClickListener {
            authViewModel.checkEmail() {isSuccess, message ->
                Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
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