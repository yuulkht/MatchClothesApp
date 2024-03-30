package ru.hse.termpaper.view.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.CalendarEvent
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.calendar.CalendarService
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class AddOutfitsToDayFragment(
    private val calendarEvent: CalendarEvent,
    private val chooseOutfitsForService: OutfitsModelService = OutfitsModelService(),
    private val outfitsInDay: MutableList<Outfit> = mutableListOf(),
    private val calendarService: CalendarService = CalendarService()
) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_outfits, container, false)

        val nextButton = view.findViewById<Button>(R.id.next)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val addingText = view.findViewById<TextView>(R.id.adding)

        addingText.text = "Добавление образов"

        calendarService.deleteAllOutfits(calendarEvent)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        chooseOutfitsForService.setupOutfitRecyclerView(outfitsInDay,view, requireContext())

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        nextButton.setOnClickListener {
            val mainActivity = requireActivity() as MainScreenActivity
            calendarService.saveOutfitsToDay(outfitsInDay, calendarEvent, mainActivity, requireContext())
        }

        return view
    }
}