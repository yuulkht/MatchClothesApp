package ru.hse.termpaper.view.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.calendar.CalendarService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment(
    private var calendarEvent: CalendarEvent? = null,
    private val calendarService: CalendarService = CalendarService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clothesButton = view.findViewById<Button>(R.id.clothesButton)
        val outfitsButton = view.findViewById<Button>(R.id.outfitsButton)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)

        val calendar = Calendar.getInstance()
        if (calendarEvent != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = dateFormat.parse(calendarEvent!!.date)!!
        } else {
            calendarService.getCalendarEvent(calendar) {curEvent ->
                calendarEvent = curEvent
            }
        }

        calendarView.date = calendar.timeInMillis

        val mainScreenActivity = requireActivity() as MainScreenActivity

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            calendarService.getCalendarEvent(selectedDate) {curEvent ->
                calendarEvent = curEvent
            }
        }

        clothesButton.setOnClickListener {
            mainScreenActivity.replaceFragment(ClothesOfDayFragment(calendarEvent!!), R.id.homePage)
        }

        outfitsButton.setOnClickListener{
            mainScreenActivity.replaceFragment(OutfitsOfDayFragment(calendarEvent!!), R.id.homePage)
        }
    }
}