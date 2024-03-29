package ru.hse.termpaper.model.repository.calendar

import android.app.DownloadManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarRepository (
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, CalendarEvent) -> Unit) {
        currentUser?.uid?.let { userId ->
            createCalendarEventNode(userId, calendarEvent) { success, calendarEventId ->
                if (success) {
                    calendarEvent.id = calendarEventId
                    calendarEvent.user_id = userId
                    saveCalendarEventData(userId, calendarEvent) { success ->
                        if (success) {
                            callback(true, calendarEvent)
                        } else {
                            callback(false, CalendarEvent())
                        }
                    }
                } else {
                    callback(false, CalendarEvent())
                }
            }
        }
    }

    private fun createCalendarEventNode(userId: String, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val calendarEventRef: DatabaseReference = database.child("calendar_events").push()
        val calendarEventId = calendarEventRef.key
        if (calendarEventId != null) {
            callback(true, calendarEventId)
        } else {
            callback(false, "")
        }
    }

    private fun saveCalendarEventData(userId: String, calendarEvent: CalendarEvent, callback: (Boolean) -> Unit) {
        val calendarEventData = hashMapOf(
            "id" to calendarEvent.id,
            "user_id" to userId,
            "date" to calendarEvent.date,
        )
        val calendarEventRef: DatabaseReference = database.child("calendar_events").child(calendarEvent.id)
        calendarEventRef.setValue(calendarEventData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    fun addOutfitToCalendarEvent(outfit: Outfit, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("outfits_in_calendar_event").push()
        val relationData = hashMapOf(
            "outfit_id" to outfit.id,
            "calendar_event_id" to calendarEvent.id,
        )
        saveRelation(relationRef, relationData, callback)
    }

    private fun saveRelation(relationRef: DatabaseReference, relationData: HashMap<String, String>, callback: (Boolean, String) -> Unit) {
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Данные успешно сохранены")
            }
            .addOnFailureListener { e ->
                callback(false, "Ошибка при сохранении данных: ${e.message}")
            }
    }
    fun addClothToCalendarEvent(cloth: Cloth, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("clothes_in_calendar_event").push()
        val relationData = hashMapOf(
            "cloth_id" to cloth.id,
            "calendar_event_id" to calendarEvent.id,
        )
        saveRelation(relationRef, relationData, callback)
    }

    fun getOutfitsFromCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        val query = getQuery("outfits_in_calendar_event", "outfit_id", calendarEvent)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val outfitsList = mutableListOf<Outfit>()
                dataSnapshot.children.forEach { snapshot ->
                    val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
                    outfitId?.let { fetchOutfitAndAddToList(it, outfitsList, dataSnapshot, callback) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, mutableListOf())
            }
        })
    }

    fun getClothesFromCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        val query = getQuery("clothes_in_calendar_event", "cloth_id", calendarEvent)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothesList = mutableListOf<Cloth>()
                dataSnapshot.children.forEach { snapshot ->
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    clothId?.let { fetchClothAndAddToList(it, clothesList, dataSnapshot, callback) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, mutableListOf())
            }
        })
    }

    fun getCalendarEventFromUser(calendar: Calendar, callback: (MutableList<CalendarEvent>) -> Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        val userQuery = database.child("calendar_events").orderByChild("user_id").equalTo(currentUser?.uid)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val events = mutableListOf<CalendarEvent>()
                dataSnapshot.children.forEach { eventSnapshot ->
                    val event = eventSnapshot.getValue(CalendarEvent::class.java)
                    event?.let { events.add(it) }
                }

                val filteredEvents = events.filter { it.date == date }
                callback(filteredEvents.toMutableList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    private fun getQuery(childPath: String, idKey: String, calendarEvent: CalendarEvent): Query {
        val ref = database.child(childPath)
        return ref.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
    }

    private fun fetchOutfitAndAddToList(outfitId: String, outfitsList: MutableList<Outfit>, dataSnapshot: DataSnapshot, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        database.child("outfits").child(outfitId).addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun fetchClothAndAddToList(clothId: String, clothesList: MutableList<Cloth>, dataSnapshot: DataSnapshot, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        database.child("clothes").child(clothId).addListenerForSingleValueEvent(object : ValueEventListener {
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


    fun deleteClothFromDay(cloth: Cloth, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val clothesRef = database.child("clothes_in_calendar_event")
        val query = clothesRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deleteItemFromSnapshot(dataSnapshot, "cloth_id", cloth.id, callback,
                    "Вещь успешно удалена из выбранного дня",
                    "Вещь не найдена в выбранном дне",
                    "Не удалось удалить вещь из выбранного дня"
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении вещи из выбранного дня")
            }
        })
    }

    fun deleteOutfitFromDay(outfit: Outfit, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val outfitsRef = database.child("outfits_in_calendar_event")
        val query = outfitsRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deleteItemFromSnapshot(dataSnapshot, "outfit_id", outfit.id, callback,
                    "Образ успешно удален из выбранного дня",
                    "Образ не найден в выбранном дне",
                    "Не удалось удалить образ из выбранного дня"
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении образа из выбранного дня")
            }
        })
    }

    private fun deleteItemFromSnapshot(
        dataSnapshot: DataSnapshot,
        itemIdKey: String,
        itemId: String,
        callback: (Boolean, String) -> Unit,
        successMessage: String,
        notFoundMessage: String,
        failureMessage: String
    ) {
        for (snapshot in dataSnapshot.children) {
            val itemKey = snapshot.child(itemIdKey).getValue(String::class.java)
            if (itemKey == itemId) {
                snapshot.ref.removeValue()
                    .addOnSuccessListener {
                        callback(true, successMessage)
                    }
                    .addOnFailureListener {
                        callback(false, failureMessage)
                    }
                return
            }
        }
        callback(false, notFoundMessage)
    }

}
/*
class CalendarRepository(
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    suspend fun Query.getDataSnapshot(): DataSnapshot =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            this.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    continuation.resume(data)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }

    // Функция-расширение для DatabaseReference, сохраняющая значение
    suspend fun DatabaseReference.setValueAndWait(value: Any): Unit =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            this.setValue(value) { error, _ ->
                if (error == null) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(error.toException())
                }
            }
        }

    suspend fun saveCalendarEvent(calendarEvent: CalendarEvent): Boolean {
        val userId = currentUser?.uid ?: return false
        val calendarEventRef = database.child("calendar_events").push()
        val calendarEventId = calendarEventRef.key ?: return false
        calendarEvent.id = calendarEventId
        calendarEvent.user_id = userId
        val calendarEventData = hashMapOf(
            "id" to calendarEventId,
            "user_id" to userId,
            "date" to calendarEvent.date
        )
        return try {
            calendarEventRef.setValueAndWait(calendarEventData)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addOutfitToCalendarEvent(outfit: Outfit, calendarEvent: CalendarEvent): Boolean {
        val relationRef: DatabaseReference = database.child("outfits_in_calendar_event").push()
        val relationData = hashMapOf(
            "outfit_id" to outfit.id,
            "calendar_event_id" to calendarEvent.id,
        )
        return try {
            relationRef.setValueAndWait(relationData)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addClothToCalendarEvent(cloth: Cloth, calendarEvent: CalendarEvent): Boolean {
        val relationRef: DatabaseReference = database.child("clothes_in_calendar_event").push()
        val relationData = hashMapOf(
            "cloth_id" to cloth.id,
            "calendar_event_id" to calendarEvent.id,
        )
        return try {
            relationRef.setValueAndWait(relationData)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getOutfitsFromCalendarEvent(calendarEvent: CalendarEvent): List<Outfit> {
        val query = getQuery("outfits_in_calendar_event", "outfit_id", calendarEvent) ?: return emptyList()
        val dataSnapshot = query.getDataSnapshot()
        val outfitsList = mutableListOf<Outfit>()
        dataSnapshot.children.forEach { snapshot ->
            val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
            outfitId?.let {
                fetchOutfitAndAddToList(it, outfitsList, dataSnapshot)
            }
        }
        return outfitsList
    }

    suspend fun getClothesFromCalendarEvent(calendarEvent: CalendarEvent): List<Cloth> {
        val query = getQuery("clothes_in_calendar_event", "cloth_id", calendarEvent) ?: return emptyList()
        val dataSnapshot = query.getDataSnapshot()
        val clothesList = mutableListOf<Cloth>()
        dataSnapshot.children.forEach { snapshot ->
            val clothId = snapshot.child("cloth_id").getValue(String::class.java)
            clothId?.let {
                fetchClothAndAddToList(it, clothesList, dataSnapshot)
            }
        }
        return clothesList
    }

    private fun getQuery(childPath: String, idKey: String, calendarEvent: CalendarEvent): Query {
        val ref = database.child(childPath)
        return ref.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
    }

    private suspend fun fetchOutfitAndAddToList(outfitId: String, outfitsList: MutableList<Outfit>, dataSnapshot: DataSnapshot) {
        val outfitSnapshot = database.child("outfits").child(outfitId).getDataSnapshot()
        val outfit = outfitSnapshot.getValue(Outfit::class.java)
        outfit?.let { outfitsList.add(it) }
    }

    private suspend fun fetchClothAndAddToList(clothId: String, clothesList: MutableList<Cloth>, dataSnapshot: DataSnapshot) {
        val clothSnapshot = database.child("clothes").child(clothId).getDataSnapshot()
        val cloth = clothSnapshot.getValue(Cloth::class.java)
        cloth?.let { clothesList.add(it) }
    }

    suspend fun deleteClothFromDay(cloth: Cloth, calendarEvent: CalendarEvent): Boolean {
        val clothesRef = database.child("clothes_in_calendar_event")
        val query = clothesRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id).getDataSnapshot()
        return deleteItemFromSnapshot(query, "cloth_id", cloth.id)
    }

    suspend fun deleteOutfitFromDay(outfit: Outfit, calendarEvent: CalendarEvent): Boolean {
        val outfitsRef = database.child("outfits_in_calendar_event")
        val query = outfitsRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id).getDataSnapshot()
        return deleteItemFromSnapshot(query, "outfit_id", outfit.id)
    }

    private suspend fun deleteItemFromSnapshot(dataSnapshot: DataSnapshot, itemIdKey: String, itemId: String): Boolean {
        for (snapshot in dataSnapshot.children) {
            val itemKey = snapshot.child(itemIdKey).getValue(String::class.java)
            if (itemKey == itemId) {
                val result = snapshot.ref.removeValue().await()
                return result != null
            }
        }
        return false
    }

    suspend fun getCalendarEventFromUser(calendar: Calendar): List<CalendarEvent> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        val userQuery = database.child("calendar_events").orderByChild("user_id").equalTo(currentUser?.uid).getDataSnapshot()

        val events = mutableListOf<CalendarEvent>()
        userQuery.children.forEach { eventSnapshot ->
            val event = eventSnapshot.getValue(CalendarEvent::class.java)
            event?.let { events.add(it) }
        }

        return events.filter { it.date == date }
    }
}
 */
