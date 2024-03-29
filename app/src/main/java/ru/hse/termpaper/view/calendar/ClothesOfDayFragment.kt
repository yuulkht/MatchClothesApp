package ru.hse.termpaper.view.calendar

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.calendar.CalendarService

class ClothesOfDayFragment(
    private var calendarEvent: CalendarEvent,
    private val calendarService: CalendarService = CalendarService()

    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clothes_of_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addClothButton = view.findViewById<LinearLayout>(R.id.addClothButton)
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val date = view.findViewById<TextView>(R.id.date)

        date.text = calendarEvent.date

        val mainScreenActivity = requireActivity() as MainScreenActivity

        calendarService.putClothesToDay(calendarEvent, view, requireActivity())

        backButton.setOnClickListener{
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        addClothButton.setOnClickListener {
            mainScreenActivity.replaceFragment(AddClothesToDayFragment(calendarEvent), R.id.homePage)
        }
    }

}