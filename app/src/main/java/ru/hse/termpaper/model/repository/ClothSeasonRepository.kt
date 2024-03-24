package ru.hse.termpaper.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
            "season" to season.toString(),
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
        val clothesRef = database.child("cloth_season_mapping")
        val query = clothesRef.orderByChild("season").equalTo(season.toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothesList = mutableListOf<Cloth>()
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

    fun getSeasons(): MutableList<Season> {
        return mutableListOf(Season.WINTER, Season.AUTUMN, Season.SPRING, Season.SUMMER)
    }

}

