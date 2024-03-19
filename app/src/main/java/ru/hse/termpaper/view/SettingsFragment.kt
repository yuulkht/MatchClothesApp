package ru.hse.termpaper.view

import android.annotation.SuppressLint
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
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.viewmodel.AuthViewModel

class SettingsFragment(
    private var authViewModel: AuthViewModel = AuthViewModel()
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
            authViewModel.logout()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        changeEmailButton.setOnClickListener {
            val inputUserEmail = newEmail.text.toString().trim()
            Toast.makeText(requireContext(), "Проверьте вашу новую почту и подтвердите ее", Toast.LENGTH_LONG).show()

            authViewModel.changeEmail(inputUserEmail) {message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        return view


    }
}