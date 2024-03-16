package ru.hse.termpaper.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.model.repository.UserRepository

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    fun register(user: User, callback: (Boolean, String) -> Unit) {
        isValidUserData(user) { isValid, validationMessage ->
            if (isValid) {
                userRepository.register(user) { isSuccess, message ->
                    callback(isSuccess, message)
                }
            } else {
                callback(false, validationMessage)
            }

        }
    }

    fun login(user: User, callback: (Boolean, String) -> Unit) {
        isValidUserData(user) { isValid, validationMessage ->
            if (isValid) {
                userRepository.login(user) { isSuccess, message ->
                    callback(isSuccess, message)
                }
            } else {
                callback(false, validationMessage)
            }

        }
    }

    fun checkEmail(callback: (Boolean, String) -> Unit) {
        userRepository.checkEmailVerification() { isSuccess, message ->
            callback(isSuccess, message)
        }
    }

    private fun isValidUserData(user: User, callback: (Boolean, String) -> Unit) {
        if (user.email == "" || user.password == "") {
            callback(false, "Не все поля заполнены")
        } else if(!isValidEmail(user.email)) {
            callback(false, "Неправильный email")
        } else if (user.password.length < 6) {
            callback(false, "Пароль должен быть не менее 6 символов")
        } else {
            callback(true, "Данные корректны")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return emailRegex.matches(email)
    }

}
