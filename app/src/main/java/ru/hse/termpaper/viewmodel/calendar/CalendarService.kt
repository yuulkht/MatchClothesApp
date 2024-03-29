package ru.hse.termpaper.viewmodel.calendar
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.adapters.OutfitsAdapter
import ru.hse.termpaper.view.calendar.CalendarFragment
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.outfits.OutfitCardFragment
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService
import ru.hse.termpaper.viewmodel.recyclerview.OutfitRecyclerViewService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarService(
    private val calendarRepository: CalendarRepository = CalendarRepository(),
    private val clothRecyclerViewService: ClothRecyclerViewService = ClothRecyclerViewService(),
    private val outfitRecyclerViewService: OutfitRecyclerViewService = OutfitRecyclerViewService()
) {
    fun deleteAllClothes(calendarEvent: CalendarEvent) {
        calendarRepository.getClothesFromCalendarEvent(calendarEvent) {_,clothes ->
            for (cloth in clothes) {
                calendarRepository.deleteClothFromDay(cloth, calendarEvent){_,_->}
            }

        }
    }

    fun deleteAllOutfits(calendarEvent: CalendarEvent) {
        calendarRepository.getOutfitsFromCalendarEvent(calendarEvent) {_,outfits ->
            for (outfit in outfits) {
                calendarRepository.deleteOutfitFromDay(outfit, calendarEvent){_,_->}
            }

        }
    }

    fun saveClothesToDay(clothesInDay: MutableList<Cloth>, calendarEvent: CalendarEvent, activity: MainScreenActivity, context: Context) {
        if (clothesInDay.isEmpty()) {
            NotificationHelper(context).showToast( "Вы не выбрали вещи")
        } else {
            for (cloth in clothesInDay) {
                calendarRepository.addClothToCalendarEvent(cloth, calendarEvent){_,_->}
            }
        }
        activity.replaceFragment(CalendarFragment(calendarEvent), R.id.homePage)
    }

    fun saveOutfitsToDay(outfitsInDay: MutableList<Outfit>, calendarEvent: CalendarEvent, activity: MainScreenActivity, context: Context) {
        if (outfitsInDay.isEmpty()) {
            NotificationHelper(context).showToast( "Вы не выбрали образы")
        } else {
            for (outfit in outfitsInDay) {
                calendarRepository.addOutfitToCalendarEvent(outfit, calendarEvent){_,_->}
            }
        }
        activity.replaceFragment(CalendarFragment(calendarEvent), R.id.homePage)
    }

    fun getCalendarEvent(calendar: Calendar, callback: (CalendarEvent) -> Unit) {
        calendarRepository.getCalendarEventFromUser(calendar) { events ->
            if (events.isEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.format(calendar.time)
                calendarRepository.saveCalendarEvent(CalendarEvent("", "", date)) { _, curEvent ->
                    callback(curEvent)
                }
            } else {
                val curEvent = events[0]
                callback(curEvent)
            }
        }
    }

    fun putClothesToDay(calendarEvent: CalendarEvent, view: View, activity: Activity) {
        calendarRepository.getClothesFromCalendarEvent(calendarEvent) { _, clothes ->
            clothRecyclerViewService.setupCalendarClothesRecyclerView(clothes, view, activity)
        }
    }

    fun putOutfitToDay(calendarEvent: CalendarEvent, view: View, activity: Activity) {
        calendarRepository.getOutfitsFromCalendarEvent(calendarEvent) { _, outfits ->
            outfitRecyclerViewService.setupCalendarOutfitsRecyclerView(outfits, view, activity)
        }
    }
}