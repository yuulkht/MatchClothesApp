package ru.hse.termpaper.viewmodel

import androidx.lifecycle.ViewModel
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.model.repository.UserRepository

class AuthService(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    fun getUser(login: String, password: String) : User {
        return User(login, password)
    }

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

    fun logout() {
        userRepository.logout()
    }

    fun changeEmail(email: String, callback: (String) -> Unit) {
        isValidNewEmail(email) { isValid, validationMessage ->
            if (isValid) {
                userRepository.changeEmail(email) {message ->
                    callback(message)
                }
            } else {
                callback(validationMessage)
            }

        }
    }

    fun checkEmail(callback: (Boolean, String) -> Unit) {
        userRepository.checkEmailVerification() { isSuccess, message ->
            callback(isSuccess, message)
        }
    }

    fun getUsername() : String {
        return userRepository.getCurrentUserName()
    }

    private fun isValidNewEmail(email: String, callback: (Boolean, String) -> Unit) {
        if (email == "") {
            callback(false, "email пуст")
        } else if(!isValidNewEmail(email)) {
            callback(false, "Неправильный email")
        } else {
            callback(true, "Данные корректны")
        }
    }

    private fun isValidUserData(user: User, callback: (Boolean, String) -> Unit) {
        if (user.email == "" || user.password == "") {
            callback(false, "Не все поля заполнены")
        } else if(!isValidNewEmail(user.email)) {
            callback(false, "Неправильный email")
        } else if (user.password.length < 6) {
            callback(false, "Пароль должен быть не менее 6 символов")
        } else {
            callback(true, "Данные корректны")
        }
    }

    private fun isValidNewEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return emailRegex.matches(email)
    }

}
