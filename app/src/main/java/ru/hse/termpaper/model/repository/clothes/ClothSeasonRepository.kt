package ru.hse.termpaper.model.repository.clothes

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Season

class ClothSeasonRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
) {
    fun addClothToSeason(cloth: Cloth, season: Season, callback: (Boolean, String) -> Unit) {
        val clothId = cloth.id
        val relationRef: DatabaseReference = database.child("cloth_season_mapping").push()
        val relationId = relationRef.key
        val relationData = hashMapOf(
            "cloth_id" to clothId,
            "season" to season.name,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Вещь успешно добавлена в сезон")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить вещь в сезон")
            }
    }

    fun getClothesFromSeason(season: Season, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        val seasonName = season.name

        val seasonsRef = database.child("cloth_season_mapping")
        val query = seasonsRef.orderByChild("season").equalTo(seasonName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothesList = mutableListOf<Cloth>()
                val clothIds = mutableListOf<String>()

                // Получаем список cloth_id, связанных с данным сезоном
                for (snapshot in dataSnapshot.children) {
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    clothId?.let { clothIds.add(it) }
                }

                if (clothIds.isEmpty()) {
                    callback(true, mutableListOf())
                    return
                }

                // Получаем данные о каждой одежде из таблицы clothes по их cloth_id
                for (clothId in clothIds) {
                    val clothesQuery = database.child("clothes").child(clothId)

                    clothesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(clothSnapshot: DataSnapshot) {
                            val cloth = clothSnapshot.getValue(Cloth::class.java)
                            cloth?.let { clothesList.add(it) }

                            // Проверяем, если получены данные по всем clothId
                            if (clothesList.size == clothIds.size) {
                                callback(true, clothesList)
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



    fun getSeasonsForCloth(cloth: Cloth, callback: (Boolean, MutableList<Season>) -> Unit) {
        val clothId = cloth.id
        val seasonsRef = database.child("cloth_season_mapping")
        val query = seasonsRef.orderByChild("cloth_id").equalTo(clothId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val seasonsList = mutableListOf<Season>()
                for (snapshot in dataSnapshot.children) {
                    val seasonName = snapshot.child("season").getValue(String::class.java)
                    seasonName?.let {
                        val season = Season.valueOf(it)
                        seasonsList.add(season)
                    }
                }
                callback(true, seasonsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, mutableListOf())
            }
        })
    }


    fun getSeasons(): MutableList<Season> {
        return mutableListOf(Season.WINTER, Season.AUTUMN, Season.SPRING, Season.SUMMER)
    }

}

