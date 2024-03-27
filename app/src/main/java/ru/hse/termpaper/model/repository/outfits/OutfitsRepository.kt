package ru.hse.termpaper.model.repository.outfits

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
import ru.hse.termpaper.model.entity.Outfit
import com.google.android.gms.tasks.Task


class OutfitsRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val dataStorage: StorageReference = FirebaseStorage.getInstance().reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveOutfit(outfit: Outfit, outfitImage: Uri?, callback: (Boolean, String, Outfit) -> Unit) {
        currentUser?.uid?.let { userId ->
            val outfitRef: DatabaseReference = database.child("outfits").push()
            val outfitId = outfitRef.key

            if (outfitId != null) {
                outfit.id = outfitId
            }
            outfit.user_id = userId

            val outfitData = hashMapOf(
                "id" to outfit.id,
                "user_id" to outfit.user_id,
                "title" to outfit.title,
                "photo" to outfit.id,
                "information" to outfit.information,
            )
            outfitRef.setValue(outfitData)
                .addOnSuccessListener {
                    saveOutfitImage(outfitImage, outfitId) { success, message ->
                        if (success) {
                            callback(true, "Новый образ и фотография успешно добавлены", outfit)
                        } else {
                            callback(false, "Ошибка при добавлении фотографии", outfit)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    callback(false, "Не удалось добавить новый образ", Outfit())
                }
        }
    }


    fun saveOutfitImage(outfitImage: Uri?, outfitId: String?, callback: (Boolean, String) -> Unit) {
        outfitImage?.let { uri ->
            outfitId?.let {
                dataStorage.child(outfitId).putFile(uri)
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


    fun getOutfits(callback: (MutableList<Outfit>) -> Unit) {
        val userId = currentUser?.uid
        val outfitsRef = database.child("outfits")
        val query = outfitsRef.orderByChild("user_id").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitsList = mutableListOf<Outfit>()
                for (snapshot in dataSnapshot.children) {
                    val outfit = snapshot.getValue(Outfit::class.java)
                    outfit?.let { outfitsList.add(it) }
                }
                callback(outfitsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun getImageForOutfit(outfit: Outfit, callback: (String?) -> Unit) {
        val imageRef = dataStorage.child(outfit.id)
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deleteOutfit(outfit: Outfit, callback: (Boolean, String) -> Unit) {
        val outfitId = outfit.id
        val outfitRef = database.child("outfits").child(outfitId)
        val imageRef = dataStorage.child(outfitId)
        val tasks = mutableListOf<Task<Void>>()

        val categoryMappingQuery = database.child("outfit_category_mapping").orderByChild("outfit_id").equalTo(outfitId)
        val seasonMappingQuery = database.child("outfit_season_mapping").orderByChild("outfit_id").equalTo(outfitId)

        // Удаляем записи из таблицы outfit_category_mapping
        categoryMappingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categoryMappingSnapshot in snapshot.children) {
                    tasks.add(categoryMappingSnapshot.ref.removeValue())
                }

                // Удаляем записи из таблицы outfit_season_mapping
                seasonMappingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (seasonMappingSnapshot in snapshot.children) {
                            tasks.add(seasonMappingSnapshot.ref.removeValue())
                        }

                        // Удаляем изображение одежды из хранилища
                        tasks.add(imageRef.delete())

                        // Удаляем саму одежду из базы данных
                        tasks.add(outfitRef.removeValue())

                        // Дожидаемся выполнения всех задач
                        Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener { tasks ->
                                if (tasks.isSuccessful) {
                                    callback(true, "Образ успешно удален")
                                } else {
                                    val errorMessage = tasks.exception?.message ?: "Не удалось удалить образ"
                                    callback(false, errorMessage)
                                }
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false, "Ошибка при удалении записей из таблицы outfit_season_mapping: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Ошибка при удалении записей из таблицы outfit_category_mapping: ${error.message}")
            }
        })
    }


}