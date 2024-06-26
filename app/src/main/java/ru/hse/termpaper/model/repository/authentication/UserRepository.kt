package ru.hse.termpaper.model.repository.authentication

import com.google.firebase.auth.FirebaseAuth
import ru.hse.termpaper.model.entity.User

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun register(user: User, callback: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(callback)
                } else {
                    callback(false, "Неуспешная попытка регистрации")
                }
            }
            .addOnFailureListener {
                callback(false, "Неуспешная попытка регистрации")
            }
    }

    fun login(user: User, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    checkEmailVerification(callback)
                } else {
                    callback(false, "Не удалось выполнить вход")
                }
            }
            .addOnFailureListener {
                callback(false, "Не удалось выполнить вход")
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun changeEmail(newEmail: String, callback: (String) -> Unit) {
        val user = auth.currentUser

        user?.verifyBeforeUpdateEmail(newEmail)
            ?.addOnCompleteListener { updateEmailTask ->
                if (updateEmailTask.isSuccessful) {
                    callback("Email успешно изменен")
                } else {
                    callback("Ошибка при изменении email: ${updateEmailTask.exception?.message}")
                }
            }
            ?.addOnFailureListener {
                callback("Не удалось изменить email")
            }
    }

    fun sendVerificationEmail(callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Письмо для подтверждения email отправлено")
                } else {
                    callback(false, "Не удалось отправить письмо для подтверждения email")
                }
            }
    }

    fun checkEmailVerification(callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                val isEmailVerified = auth.currentUser?.isEmailVerified ?: false
                if (isEmailVerified) {
                    callback(true, "Успешно")
                } else {
                    callback(false, "Ваш email не подтвержден")
                }
            } else {
                callback(false, "Не удалось загрузить пользователя: ${reloadTask.exception?.message}")
            }
        }
    }

    fun getCurrentUserName(): String {
        return auth.currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercaseChar() } ?: ""
    }
}
