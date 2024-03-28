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
import ru.hse.termpaper.model.entity.Cloth


class OutfitsRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val dataStorage: StorageReference = FirebaseStorage.getInstance().reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveOutfit(outfit: Outfit, outfitImage: Uri?, clothes: List<Cloth>, callback: (Boolean, String, Outfit) -> Unit) {
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
                            saveClothesToOutfit(outfit, clothes) {success, message ->
                                if (success) {
                                    callback(true, "Новый образ и фотография успешно добавлены", outfit)
                                }
                                else {
                                    callback(false, "Ошибка при добавлении вещей в образ", outfit)
                                }

                            }
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

    fun saveClothesToOutfit(outfit: Outfit, clothes: List<Cloth>, callback: (Boolean, String) -> Unit) {
        val clothesInOutfitRef = database.child("clothes_in_outfit")
        val tasks = mutableListOf<Task<Void>>()

        for (cloth in clothes) {
            val clothesInOutfitData = hashMapOf(
                "outfit_id" to outfit.id,
                "cloth_id" to cloth.id
            )
            val clothesInOutfitRef = clothesInOutfitRef.push()
            clothesInOutfitRef.setValue(clothesInOutfitData)
                .addOnCompleteListener { task ->
                    tasks.add(task)
                }
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener { tasks ->
                if (tasks.isSuccessful) {
                    callback(true, "Вещи успешно добавлены в образ")
                } else {
                    val errorMessage = tasks.exception?.message ?: "Не удалось добавить вещи в образ"
                    callback(false, errorMessage)
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

    fun getClothesForOutfit(outfit: Outfit, callback: (MutableList<Cloth>) -> Unit) {
        val clothesInOutfitRef = database.child("clothes_in_outfit")
        val clothesRef = database.child("clothes")
        val clothesList = mutableListOf<Cloth>()

        val query = clothesInOutfitRef.orderByChild("outfit_id").equalTo(outfit.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasks = mutableListOf<Task<DataSnapshot>>()
                for (clothesInOutfitSnapshot in dataSnapshot.children) {
                    val clothId = clothesInOutfitSnapshot.child("cloth_id").value.toString()
                    val clothQuery = clothesRef.child(clothId).get()
                    tasks.add(clothQuery)
                }

                Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener { taskResults ->
                        for (result in taskResults) {
                            val clothSnapshot = result.result as DataSnapshot
                            val cloth = clothSnapshot.getValue(Cloth::class.java)
                            cloth?.let { clothesList.add(it) }
                        }
                        callback(clothesList)
                    }
                    .addOnFailureListener { exception ->
                        callback(mutableListOf())
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun deleteOutfit(outfit: Outfit, callback: (Boolean, String) -> Unit) {
        val outfitId = outfit.id
        val outfitRef = database.child("outfits").child(outfitId)
        val imageRef = dataStorage.child(outfitId)
        val clothesInOutfitRef = database.child("clothes_in_outfit")

        val tasks = mutableListOf<Task<Void>>()

        val clothesInOutfitQuery = clothesInOutfitRef.orderByChild("outfit_id").equalTo(outfitId)
        clothesInOutfitQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (clothesInOutfitSnapshot in dataSnapshot.children) {
                    tasks.add(clothesInOutfitSnapshot.ref.removeValue())
                }

                val categoryMappingQuery = database.child("outfit_category_mapping").orderByChild("outfit_id").equalTo(outfitId)
                val seasonMappingQuery = database.child("outfit_season_mapping").orderByChild("outfit_id").equalTo(outfitId)

                categoryMappingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (categoryMappingSnapshot in snapshot.children) {
                            tasks.add(categoryMappingSnapshot.ref.removeValue())
                        }
                        seasonMappingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (seasonMappingSnapshot in snapshot.children) {
                                    tasks.add(seasonMappingSnapshot.ref.removeValue())
                                }

                                tasks.add(imageRef.delete())
                                tasks.add(outfitRef.removeValue())

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

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Ошибка при удалении записей из таблицы clothes_in_outfit: ${error.message}")
            }
        })
    }



}