package ru.hse.termpaper.model.repository.outfits

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.Season

class OutfitSeasonRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
) {
    fun addOutfitToSeason(outfit: Outfit, season: Season, callback: (Boolean, String) -> Unit) {
        val outfitId = outfit.id
        val relationRef: DatabaseReference = database.child("outfit_season_mapping").push()
        val relationData = hashMapOf(
            "outfit_id" to outfitId,
            "season" to season.name,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Образ успешно добавлен в сезон")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить образ в сезон")
            }
    }

    fun getOutfitsFromSeason(season: Season, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        val seasonName = season.name

        val seasonsRef = database.child("outfit_season_mapping")
        val query = seasonsRef.orderByChild("season").equalTo(seasonName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitsList = mutableListOf<Outfit>()
                val outfitIds = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
                    outfitId?.let { outfitIds.add(it) }
                }

                if (outfitIds.isEmpty()) {
                    callback(true, mutableListOf())
                    return
                }

                for (outfitId in outfitIds) {
                    val outfitsQuery = database.child("outfits").child(outfitId)

                    outfitsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(outfitSnapshot: DataSnapshot) {
                            val outfit = outfitSnapshot.getValue(Outfit::class.java)
                            outfit?.let { outfitsList.add(it) }

                            if (outfitsList.size == outfitIds.size) {
                                callback(true, outfitsList)
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



    fun getSeasonsForOutfit(outfit: Outfit, callback: (Boolean, MutableList<Season>) -> Unit) {
        val outfitId = outfit.id
        val seasonsRef = database.child("outfit_season_mapping")
        val query = seasonsRef.orderByChild("outfit_id").equalTo(outfitId)
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

