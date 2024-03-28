package ru.hse.termpaper.model.repository.calendar

import com.google.android.gms.tasks.Task
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
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.OutfitCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarRepository (
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://matchclothes-d0c67-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) {

    fun saveCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, CalendarEvent) -> Unit) {
        currentUser?.uid?.let { userId ->
            val calendarEventRef: DatabaseReference = database.child("calendar_events").push()
            val calendarEventId = calendarEventRef.key
            if (calendarEventId != null) {
                calendarEvent.id = calendarEventId
            }
            calendarEvent.user_id = userId
            val calendarEventData = hashMapOf(
                "id" to calendarEventId,
                "user_id" to userId,
                "date" to calendarEvent.date,
            )
            calendarEventRef.setValue(calendarEventData)
                .addOnSuccessListener {
                    callback(true, calendarEvent)
                }
                .addOnFailureListener { e ->
                    callback(false, CalendarEvent())
                }
        }
    }

    fun addOutfitToCalendarEvent(outfit: Outfit, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("outfits_in_calendar_event").push()
        val relationData = hashMapOf(
            "outfit_id" to outfit.id,
            "calendar_event_id" to calendarEvent.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Образ успешно добавлен в выбранный день")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить образ в выбранный день")
            }
    }

    fun addClothToCalendarEvent(cloth: Cloth, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val relationRef: DatabaseReference = database.child("clothes_in_calendar_event").push()
        val relationData = hashMapOf(
            "cloth_id" to cloth.id,
            "calendar_event_id" to calendarEvent.id,
        )
        relationRef.setValue(relationData)
            .addOnSuccessListener {
                callback(true, "Вещь успешно добавлена в выбранный день")
            }
            .addOnFailureListener { e ->
                callback(false, "Не удалось добавить вещь в выбранный день")
            }
    }

    fun getOutfitsFromCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, MutableList<Outfit>) -> Unit) {
        val outfitsRef = database.child("outfits_in_calendar_event")
        val query = outfitsRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
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

    fun getClothesFromCalendarEvent(calendarEvent: CalendarEvent, callback: (Boolean, MutableList<Cloth>) -> Unit) {
        val clothesRef = database.child("clothes_in_calendar_event")
        val query = clothesRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
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

    fun getCalendarEventFromUser(calendar: Calendar, callback: (MutableList<CalendarEvent>) -> Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        val userQuery = database.child("calendar_events")
            .orderByChild("user_id")
            .equalTo(currentUser?.uid)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val events = mutableListOf<CalendarEvent>()
                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.getValue(CalendarEvent::class.java)
                    event?.let {
                        events.add(it)
                    }
                }

                val filteredEvents = events.filter { it.date == date }
                callback(filteredEvents.toMutableList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun deleteClothFromDay(cloth: Cloth, calendarEvent: CalendarEvent, callback: (Boolean, String) -> Unit) {
        val clothesRef = database.child("clothes_in_calendar_event")
        val query = clothesRef.orderByChild("calendar_event_id").equalTo(calendarEvent.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val clothId = snapshot.child("cloth_id").getValue(String::class.java)
                    if (clothId == cloth.id) {
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                callback(true, "Вещь успешно удалена из выбранного дня")
                            }
                            .addOnFailureListener {
                                callback(false, "Не удалось удалить вещь из выбранного дня")
                            }
                        return
                    }
                }
                callback(false, "Вещь не найдена в выбранном дне")
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
                for (snapshot in dataSnapshot.children) {
                    val outfitId = snapshot.child("outfit_id").getValue(String::class.java)
                    if (outfitId == outfit.id) {
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                callback(true, "Образ успешно удален из выбранного дня")
                            }
                            .addOnFailureListener {
                                callback(false, "Не удалось удалить образ из выбранного дня")
                            }
                        return
                    }
                }
                callback(false, "Образ не найден в выбранном дне")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, "Ошибка при удалении образа из выбранного дня")
            }
        })
    }



}
