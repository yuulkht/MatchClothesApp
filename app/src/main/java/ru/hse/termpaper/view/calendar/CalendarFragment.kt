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
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.view.main.MainScreenActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment(
    private var calendarEvent: CalendarEvent? = null,
    private val calendarRepository: CalendarRepository = CalendarRepository(),
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
            calendarView.date = calendar.timeInMillis
        } else {
            calendarView.date = calendar.timeInMillis
            getCalendarEvent(calendar)
        }

        val mainScreenActivity = requireActivity() as MainScreenActivity

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            getCalendarEvent(selectedDate)
        }

        clothesButton.setOnClickListener {
            mainScreenActivity.replaceFragment(ClothesOfDayFragment(calendarEvent!!), R.id.homePage)
        }

        outfitsButton.setOnClickListener{
            mainScreenActivity.replaceFragment(OutfitsOfDayFragment(calendarEvent!!), R.id.homePage)
        }
    }

    fun getCalendarEvent(calendar: Calendar) {
        calendarRepository.getCalendarEventFromUser(calendar) { events ->
            if (events.isEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.format(calendar.time)
                calendarRepository.saveCalendarEvent(CalendarEvent("", "", date)) {_, curEvent->
                    calendarEvent = curEvent
                }
            } else {
                val curEvent = events[0]
                calendarEvent = curEvent
            }
        }
    }
}