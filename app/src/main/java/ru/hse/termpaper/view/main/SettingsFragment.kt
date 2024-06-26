package ru.hse.termpaper.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.authentication.LoginActivity
import ru.hse.termpaper.viewmodel.authentication.AuthService

class SettingsFragment(
    private var authService: AuthService = AuthService()
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        val changeEmailButton: Button = view.findViewById(R.id.changeEmailButton)
        val newEmail: EditText = view.findViewById(R.id.editedEmail)
        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        val backLink = view.findViewById<ImageView>(R.id.backButton)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.mainScreenFragment, R.id.homePage)
        }

        logoutButton.setOnClickListener {
            authService.logout()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        changeEmailButton.setOnClickListener {
            val inputUserEmail = newEmail.text.toString().trim()
            NotificationHelper(requireContext()).showToast("Проверьте вашу новую почту и подтвердите ее")
            authService.changeEmail(inputUserEmail) {message ->
                NotificationHelper(requireContext()).showToast(message)
            }
        }

        return view


    }
}