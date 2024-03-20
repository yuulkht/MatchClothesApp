package ru.hse.termpaper.model.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ru.hse.termpaper.model.entity.Cloth


class ClothesRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val dataStorage: StorageReference = FirebaseStorage.getInstance().reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveCloth(cloth: Cloth, callback: (Boolean, String) -> Unit) {
        currentUser?.uid?.let { userId ->
            val clothRef: DatabaseReference = database.child("clothes").push()
            val clothId = clothRef.key
            val clothData = hashMapOf(
                "id" to clothId,
                "user_id" to userId,
                "title" to cloth.title,
                "photo" to cloth.photo,
                "information" to cloth.information,
                "season" to cloth.season.toString()
            )
            clothRef.setValue(clothData)
                .addOnSuccessListener {
                    callback(true, "Новая вещь успешно добавлена")
                }
                .addOnFailureListener { e ->
                    callback(false, "Не удалось добавить новую вещь")
                }
        }
    }

    fun getClothes(callback: (MutableList<Cloth>) -> Unit) {
        val userId = currentUser?.uid
        val clothesRef = database.child("clothes")
        val query = clothesRef.orderByChild("user_id").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothesList = mutableListOf<Cloth>()
                for (snapshot in dataSnapshot.children) {
                    val cloth = snapshot.getValue(Cloth::class.java)
                    cloth?.let { clothesList.add(it) }
                }
                callback(clothesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun getImageForCloth(cloth: Cloth, callback: (String?) -> Unit) {
        if (cloth.photo.isNotEmpty()) {
            val storageRef = dataStorage.child("${cloth.photo}.jpg")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }


}