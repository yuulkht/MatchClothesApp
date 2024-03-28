package ru.hse.termpaper.view.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class AddClothesToDayFragment(
    private val calendarEvent: CalendarEvent,
    private val calendarRepository: CalendarRepository = CalendarRepository(),
    private val chooseClothesForService: ClothesModelService = ClothesModelService(),
    private val clothesInDay: MutableList<Cloth> = mutableListOf(),
): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_clothes, container, false)

        val nextButton = view.findViewById<Button>(R.id.next)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val addingText = view.findViewById<TextView>(R.id.adding)
        addingText.text = "Добавление вещей"

        deleteAllClothes(calendarEvent)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        chooseClothesForService.setupClothRecyclerView(clothesInDay,view, requireContext())

        nextButton.setOnClickListener {
            val mainActivity = requireActivity() as MainScreenActivity
            if (clothesInDay.isEmpty()) {
                NotificationHelper( requireContext()).showToast( "Вы не выбрали вещи")
            } else {
                for (cloth in clothesInDay) {
                    calendarRepository.addClothToCalendarEvent(cloth, calendarEvent){_,_->}
                }
            }
            mainActivity.replaceFragment(CalendarFragment(calendarEvent), R.id.homePage)
        }

        return view
    }

    fun deleteAllClothes(calendarEvent: CalendarEvent) {
        calendarRepository.getClothesFromCalendarEvent(calendarEvent) {_,clothes ->
            for (cloth in clothes) {
                calendarRepository.deleteClothFromDay(cloth, calendarEvent){_,_->}
            }

        }
    }
}