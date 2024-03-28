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
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class AddOutfitsToDayFragment(
    private val calendarEvent: CalendarEvent,
    private val calendarRepository: CalendarRepository = CalendarRepository(),
    private val chooseOutfitsForService: OutfitsModelService = OutfitsModelService(),
    private val outfitsInDay: MutableList<Outfit> = mutableListOf(),
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

        deleteAllOutfits(calendarEvent)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        chooseOutfitsForService.setupOutfitRecyclerView(outfitsInDay,view, requireContext())

        nextButton.setOnClickListener {
            val mainActivity = requireActivity() as MainScreenActivity
            if (outfitsInDay.isEmpty()) {
                NotificationHelper(requireContext()).showToast("Вы не выбрали образы")
            }
            else {
                for (outfit in outfitsInDay) {
                    calendarRepository.addOutfitToCalendarEvent(outfit, calendarEvent) {_,_-> }
                }
            }
            mainActivity.replaceFragment(CalendarFragment(calendarEvent), R.id.homePage)
        }

        return view
    }

    fun deleteAllOutfits(calendarEvent: CalendarEvent) {
        calendarRepository.getOutfitsFromCalendarEvent(calendarEvent) {_,outfits ->
            for (outfit in outfits) {
                calendarRepository.deleteOutfitFromDay(outfit, calendarEvent){_,_->}
            }
        }
    }
}