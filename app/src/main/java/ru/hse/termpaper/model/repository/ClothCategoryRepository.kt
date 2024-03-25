package ru.hse.termpaper.model.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory

class ClothCategoryRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveClothCategory(clothCategory: ClothCategory, callback: (Boolean, String, ClothCategory) -> Unit) {
        currentUser?.uid?.let { userId ->
            val clothCategoryRef: DatabaseReference = database.child("cloth_categories").push()
            val clothCategoryId = clothCategoryRef.key
            if (clothCategoryId != null) {
                clothCategory.id = clothCategoryId
            }
            clothCategory.user_id = userId
            val clothCategoryData = hashMapOf(
                "id" to clothCategoryId,
                "user_id" to userId,
                "title" to clothCategory.title,
            )
            clothCategoryRef.setValue(clothCategoryData)
                .addOnSuccessListener {
                    callback(true, "Новая категория успешно добавлена", clothCategory)
                }
                .addOnFailureListener { e ->
                    callback(false, "Не удалось добавить новую категорию", ClothCategory())
                }
        }
    }

    fun getClothCategories(callback: (MutableList<ClothCategory>) -> Unit) {
        val userId = currentUser?.uid
        val clothesRef = database.child("cloth_categories")
        val query = clothesRef.orderByChild("user_id").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothCategoryList = mutableListOf<ClothCategory>()
                for (snapshot in dataSnapshot.children) {
                    val clothCategory = snapshot.getValue(ClothCategory::class.java)
                    clothCategory?.let { clothCategoryList.add(it) }
                }
                callback(clothCategoryList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun addClothToCategory(cloth: Cloth, clothCategory: ClothCategory, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("cloth_category_mapping").push()
        val relationId = relationRef.key
        val relationData = hashMapOf(
            "cloth_id" to cloth.id,
            "cloth_category_id" to clothCategory.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Вещь успешно добавлена в категорию")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить вещь в категорию")
            }
    }

    fun getClothesFromCategory(clothCategory: ClothCategory, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        val clothesRef = database.child("cloth_category_mapping")
        val query = clothesRef.orderByChild("cloth_category_id").equalTo(clothCategory.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothesList = mutableListOf<Cloth>()

                if (dataSnapshot.childrenCount.toInt() == 0) {
                    callback(true, mutableListOf())
                    return
                }
                for (snapshot in dataSnapshot.children) {
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    clothId?.let {
                        database.child("clothes").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(clothSnapshot: DataSnapshot) {
                                val cloth = clothSnapshot.getValue(Cloth::class.java)
                                cloth?.let { clothesList.add(it) }
                                if (clothesList.size == dataSnapshot.childrenCount.toInt()) {
                                    callback(true, clothesList)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                callback(false, mutableListOf())
                            }
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, mutableListOf())
            }
        })
    }

    fun getCategoriesForCloth(cloth: Cloth, callback: (Boolean, MutableList<ClothCategory>) -> Unit) {
        val categoriesRef = database.child("cloth_category_mapping")
        val query = categoriesRef.orderByChild("cloth_id").equalTo(cloth.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoryIds = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val categoryId =
                        snapshot.child("cloth_category_id").getValue(String::class.java)
                    categoryId?.let { categoryIds.add(it) }
                }
                // Теперь у вас есть список идентификаторов категорий, к которым относится данная вещь
                // Далее, для каждого идентификатора категории, вы можете получить соответствующую категорию из базы данных
                val categoriesList = mutableListOf<ClothCategory>()
                val categoryQueryList = categoryIds.map { categoryId ->
                    database.child("cloth_categories").child(categoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(categorySnapshot: DataSnapshot) {
                                val category = categorySnapshot.getValue(ClothCategory::class.java)
                                category?.let { categoriesList.add(it) }
                                if (categoriesList.size == categoryIds.size) {
                                    callback(true, categoriesList)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                callback(false, mutableListOf())
                            }
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, mutableListOf())
            }
        })
    }
}