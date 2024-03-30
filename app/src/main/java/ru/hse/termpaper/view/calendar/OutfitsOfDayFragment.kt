package ru.hse.termpaper.view.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.calendar.CalendarService

class OutfitsOfDayFragment(
    private var calendarEvent: CalendarEvent,
    private val calendarService: CalendarService = CalendarService()

    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_outfits_of_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addOutfitButton = view.findViewById<LinearLayout>(R.id.addOutfitButton)
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val date = view.findViewById<TextView>(R.id.date)

        date.text = calendarEvent.date

        val mainScreenActivity = requireActivity() as MainScreenActivity

        calendarService.putOutfitToDay(calendarEvent, view, requireActivity())

        backButton.setOnClickListener{
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        addOutfitButton.setOnClickListener {
            mainScreenActivity.replaceFragment(AddOutfitsToDayFragment(calendarEvent), R.id.homePage)
        }
    }

}