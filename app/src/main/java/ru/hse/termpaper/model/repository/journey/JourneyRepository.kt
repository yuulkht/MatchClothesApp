package ru.hse.termpaper.model.repository.journey

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.entity.Outfit

class JourneyRepository (
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveJourney(journey: Journey, callback: (Boolean, Journey) -> Unit) {
        currentUser?.uid?.let { userId ->
            val journeyRef: DatabaseReference = database.child("journeys").push()
            val journeyId = journeyRef.key
            if (journeyId != null) {
                journey.id = journeyId
            }
            journey.user_id = userId
            val journeyData = hashMapOf(
                "id" to journeyId,
                "user_id" to userId,
                "title" to journey.title,
            )
            journeyRef.setValue(journeyData)
                .addOnSuccessListener {
                    callback(true, journey)
                }
                .addOnFailureListener { e ->
                    callback(false, Journey())
                }
        }
    }

    fun addOutfitToJourney(outfit: Outfit, journey: Journey, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("outfits_in_journey").push()
        val relationData = hashMapOf(
            "outfit_id" to outfit.id,
            "journey_id" to journey.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Образ успешно добавлен в выбранное путешествие")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить образ в выбранное путешествие")
            }
    }

    fun addClothToJourney(cloth: Cloth, journey: Journey, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("clothes_in_journey").push()
        val relationData = hashMapOf(
            "cloth_id" to cloth.id,
            "journey_id" to journey.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Вещь успешно добавлена в выбранное путешествие")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить вещь в выбранное путешествие")
            }
    }

    fun getOutfitsFromJourney(journey: Journey, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        val outfitsRef = database.child("outfits_in_journey")
        val query = outfitsRef.orderByChild("journey_id").equalTo(journey.id)
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

    fun getClothesFromJourney(journey: Journey, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        val clothesRef = database.child("clothes_in_journey")
        val query = clothesRef.orderByChild("journey_id").equalTo(journey.id)
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
                            override fun onDataChange(outfitSnapshot: DataSnapshot) {
                                val cloth = outfitSnapshot.getValue(Cloth::class.java)
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

    fun getJourneysFromUser(callback: (MutableList<Journey>) -> Unit) {
        val userQuery = database.child("journeys")
            .orderByChild("user_id")
            .equalTo(currentUser?.uid)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val journeys = mutableListOf<Journey>()
                for (journeySnapshot in dataSnapshot.children) {
                    val gottenJourney = journeySnapshot.getValue(Journey::class.java)
                    gottenJourney?.let {
                        journeys.add(it)
                    }
                }
                callback(journeys.toMutableList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun deleteClothFromJourney(cloth: Cloth, journey: Journey, callback: (Boolean, String) -> Unit) {
        val clothesRef = database.child("clothes_in_journey")
        val query = clothesRef.orderByChild("journey_id").equalTo(journey.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    if (clothId == cloth.id) {
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                callback(true, "Вещь успешно удалена из выбранного путешествия")
                            }
                            .addOnFailureListener {
                                callback(false, "Не удалось удалить вещь из выбранного путешествия")
                            }
                        return
                    }
                }
                callback(false, "Вещь не найдена в выбранном путешествии")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении вещи из выбранного путешествия")
            }
        })
    }

    fun deleteOutfitFromJourney(outfit: Outfit, journey: Journey, callback: (Boolean, String) -> Unit) {
        val outfitsRef = database.child("outfits_in_journey")
        val query = outfitsRef.orderByChild("journey_id").equalTo(journey.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
                    if (outfitId == outfit.id) {
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                callback(true, "Образ успешно удален из выбранного путешествия")
                            }
                            .addOnFailureListener {
                                callback(false, "Не удалось удалить образ из выбранного путешествия")
                            }
                        return
                    }
                }
                callback(false, "Образ не найден в выбранном путешествии")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении образа из выбранного путешествия")
            }
        })
    }

    fun deleteJourney(journey: Journey, callback: (Boolean, String) -> Unit) {
        val journeyRef = database.child("journeys").child(journey.id)
        val clothesRef = database.child("clothes_in_journey")
        val outfitsRef = database.child("outfits_in_journey")

        val clothesQuery = clothesRef.orderByChild("journey_id").equalTo(journey.id)
        clothesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }

                val outfitsQuery = outfitsRef.orderByChild("journey_id").equalTo(journey.id)
                outfitsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            snapshot.ref.removeValue()
                        }

                        journeyRef.removeValue()
                            .addOnSuccessListener {
                                callback(true, "Путешествие успешно удалено")
                            }
                            .addOnFailureListener {
                                callback(false, "Не удалось удалить путешествие")
                            }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        callback(false, "Ошибка при удалении образа из выбранного путешествия")
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении вещи из выбранного путешествия")
            }
        })
    }




}