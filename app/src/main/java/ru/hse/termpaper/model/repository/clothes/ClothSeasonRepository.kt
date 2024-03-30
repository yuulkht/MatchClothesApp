package ru.hse.termpaper.model.repository.clothes

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {
    fun addClothToSeason(cloth: Cloth, season: Season, callback: (Boolean, String) -> Unit) {
        val clothId = cloth.id
        val relationRef: DatabaseReference = database.child("cloth_season_mapping").push()
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

                for (snapshot in dataSnapshot.children) {
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    clothId?.let { clothIds.add(it) }
                }

                if (clothIds.isEmpty()) {
                    callback(true, mutableListOf())
                    return
                }

                val tasks = mutableListOf<Task<DataSnapshot>>()

                for (clothId in clothIds) {
                    val clothesQuery = database.child("clothes").child(clothId).get()
                    tasks.add(clothesQuery)
                }

                Tasks.whenAllSuccess<DataSnapshot>(tasks)
                    .addOnSuccessListener { snapshots ->
                        for (snapshot in snapshots) {
                            val cloth = snapshot.getValue(Cloth::class.java)
                            if (cloth != null && cloth.user_id == currentUser?.uid) {
                                clothesList.add(cloth)
                            }
                        }
                        callback(true, clothesList)
                    }
                    .addOnFailureListener {
                        callback(false, mutableListOf())
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