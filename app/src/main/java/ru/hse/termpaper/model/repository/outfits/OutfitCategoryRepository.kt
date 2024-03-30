package ru.hse.termpaper.model.repository.outfits

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.OutfitCategory

class OutfitCategoryRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveOutfitCategory(outfitCategory: OutfitCategory, callback: (Boolean, String, OutfitCategory) -> Unit) {
        currentUser?.uid?.let { userId ->
            val outfitCategoryRef: DatabaseReference = database.child("outfit_categories").push()
            val outfitCategoryId = outfitCategoryRef.key
            if (outfitCategoryId != null) {
                outfitCategory.id = outfitCategoryId
            }
            outfitCategory.userId = userId
            val outfitCategoryData = hashMapOf(
                "id" to outfitCategoryId,
                "user_id" to userId,
                "title" to outfitCategory.title,
            )
            outfitCategoryRef.setValue(outfitCategoryData)
                .addOnSuccessListener {
                    callback(true, "Новая категория успешно добавлена", outfitCategory)
                }
                .addOnFailureListener { e ->
                    callback(false, "Не удалось добавить новую категорию", OutfitCategory())
                }
        }
    }

    fun getOutfitCategories(callback: (MutableList<OutfitCategory>) -> Unit) {
        val userId = currentUser?.uid
        val outfitsRef = database.child("outfit_categories")
        val query = outfitsRef.orderByChild("user_id").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitCategoryList = mutableListOf<OutfitCategory>()
                for (snapshot in dataSnapshot.children) {
                    val outfitCategory = snapshot.getValue(OutfitCategory::class.java)
                    outfitCategory?.let { outfitCategoryList.add(it) }
                }
                callback(outfitCategoryList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun addOutfitToCategory(outfit: Outfit, outfitCategory: OutfitCategory, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("outfit_category_mapping").push()
        val relationId = relationRef.key
        val relationData = hashMapOf(
            "outfit_id" to outfit.id,
            "outfit_category_id" to outfitCategory.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Образ успешно добавлен в категорию")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить образ в категорию")
            }
    }

    fun getOutfitsFromCategory(outfitCategory: OutfitCategory, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        val outfitsRef = database.child("outfit_category_mapping")
        val query = outfitsRef.orderByChild("outfit_category_id").equalTo(outfitCategory.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitsList = mutableListOf<Outfit>()

                if (dataSnapshot.childrenCount.toInt() == 0) {
                    callback(true, mutableListOf())
                    return
                }
                for (snapshot in dataSnapshot.children) {
                    val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
                    outfitId?.let {
                        database.child("outfits").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(outfitSnapshot: DataSnapshot) {
                                val outfit = outfitSnapshot.getValue(Outfit::class.java)
                                outfit?.let { outfitsList.add(it) }
                                if (outfitsList.size == dataSnapshot.childrenCount.toInt()) {
                                    callback(true, outfitsList)
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

    fun getCategoriesForOutfit(outfit: Outfit, callback: (Boolean, MutableList<OutfitCategory>) -> Unit) {
        val categoriesRef = database.child("outfit_category_mapping")
        val query = categoriesRef.orderByChild("outfit_id").equalTo(outfit.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoryIds = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val categoryId =
                        snapshot.child("outfit_category_id").getValue(String::class.java)
                    categoryId?.let { categoryIds.add(it) }
                }
                val categoriesList = mutableListOf<OutfitCategory>()
                val categoryQueryList = categoryIds.map { categoryId ->
                    database.child("outfit_categories").child(categoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(categorySnapshot: DataSnapshot) {
                                val category = categorySnapshot.getValue(OutfitCategory::class.java)
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


    fun deleteCategory(category: OutfitCategory, callback: (String) -> Unit) {
        val categoryRef = database.child("outfit_categories").child(category.id)
        val mappingRef = database.child("outfit_category_mapping")
        val query = mappingRef.orderByChild("outfit_category_id").equalTo(category.id)

        categoryRef.removeValue()
            .addOnSuccessListener {
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { child ->
                            child.ref.removeValue()
                        }
                        callback("Категория и связанные записи успешно удалены")
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        callback("Ошибка при удалении связанных записей: ${databaseError.message}")
                    }
                })
            }
            .addOnFailureListener { e ->
                callback("Ошибка при удалении категории: ${e.message}")
            }
    }
}