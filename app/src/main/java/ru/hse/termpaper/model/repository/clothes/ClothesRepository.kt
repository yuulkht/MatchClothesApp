package ru.hse.termpaper.model.repository.clothes

import android.net.Uri
import com.google.android.gms.tasks.Tasks
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
import com.google.android.gms.tasks.Task
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository


class ClothesRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val dataStorage: StorageReference = FirebaseStorage.getInstance().reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveCloth(cloth: Cloth, clothImage: Uri?, callback: (Boolean, String, Cloth) -> Unit) {
        currentUser?.uid?.let { userId ->
            val clothRef: DatabaseReference = database.child("clothes").push()
            val clothId = clothRef.key

            if (clothId != null) {
                cloth.id = clothId
            }
            cloth.user_id = userId

            val clothData = hashMapOf(
                "id" to cloth.id,
                "user_id" to cloth.user_id,
                "title" to cloth.title,
                "photo" to cloth.id,
                "information" to cloth.information,
            )
            clothRef.setValue(clothData)
                .addOnSuccessListener {
                    saveClothImage(clothImage, clothId) { success, message ->
                        if (success) {
                            callback(true, "Новая вещь и фотография успешно добавлены", cloth)
                        } else {
                            callback(false, "Ошибка при добавлении фотографии", cloth)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    callback(false, "Не удалось добавить новую вещь", Cloth())
                }
        }
    }


    fun saveClothImage(clothImage: Uri?, clothId: String?, callback: (Boolean, String) -> Unit) {
        clothImage?.let { uri ->
            clothId?.let {
                dataStorage.child(clothId).putFile(uri)
                    .addOnSuccessListener {
                        callback(true, "Фотография успешно загружена")
                    }
                    .addOnFailureListener {
                        callback(false, "Не удалось загрузить фотографию")
                    }
            }
        } ?: run {
            callback(false, "Не удалось загрузить фотографию")
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
        val imageRef = dataStorage.child(cloth.id)
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deleteCloth(cloth: Cloth, callback: (Boolean, String) -> Unit) {
        val clothId = cloth.id
        val clothRef = database.child("clothes").child(clothId)
        val imageRef = dataStorage.child(clothId)
        val tasks = mutableListOf<Task<Void>>()

        val clothesInOutfitRef = database.child("clothes_in_outfit")
        val clothesInOutfitQuery = clothesInOutfitRef.orderByChild("cloth_id").equalTo(clothId)

        clothesInOutfitQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitIds = mutableListOf<String>()
                for (clothesInOutfitSnapshot in dataSnapshot.children) {
                    val outfitId = clothesInOutfitSnapshot.child("outfit_id").getValue(String::class.java)
                    outfitId?.let {
                        outfitIds.add(it)
                    }
                }

                for (outfitId in outfitIds) {
                    val outfitRef = database.child("outfits").child(outfitId)
                    outfitRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val outfit = snapshot.getValue(Outfit::class.java)
                            outfit?.let {
                                OutfitsRepository().deleteOutfit(it) { success, message ->
                                    if (!success) {
                                        tasks.add(Tasks.forException(Exception(message)))
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            tasks.add(Tasks.forException(Exception("Ошибка при получении аутфита")))
                        }
                    })
                }

                tasks.add(imageRef.delete())

                tasks.add(clothRef.removeValue())

                Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener { tasks ->
                        if (tasks.isSuccessful) {
                            callback(true, "Вещь успешно удалена")
                        } else {
                            val errorMessage = tasks.exception?.message ?: "Не удалось удалить вещь"
                            callback(false, errorMessage)
                        }
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при поиске аутфитов: ${databaseError.message}")
            }
        })
    }



}