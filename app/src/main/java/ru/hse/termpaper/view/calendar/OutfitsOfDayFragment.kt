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
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.calendar.CalendarRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.OutfitsAdapter
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.outfits.OutfitCardFragment

class OutfitsOfDayFragment(
    private var calendarEvent: CalendarEvent,
    private val calendarRepository: CalendarRepository = CalendarRepository(),
    private var outfitsAdapter: OutfitsAdapter? = null,
    private val outfitsRepository: OutfitsRepository = OutfitsRepository()

    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_outfits_of_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val outfitsContainer = view.findViewById<RecyclerView>(R.id.outfitsContainer)
        val addOutfitButton = view.findViewById<LinearLayout>(R.id.addOutfitButton)
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val date = view.findViewById<TextView>(R.id.date)

        date.text = calendarEvent.date

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backButton.setOnClickListener{
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        putOutfitToDay(view, requireActivity())


        addOutfitButton.setOnClickListener {
            mainScreenActivity.replaceFragment(AddOutfitsToDayFragment(calendarEvent), R.id.homePage)
        }
    }

    fun setupOutfitsRecyclerView(outfits: MutableList<Outfit>, view: View, activity: Activity, ) {
        val outfitsContainer: RecyclerView = view.findViewById(R.id.outfitsContainer)
        val mainScreenActivity = activity as MainScreenActivity

        val adapter = OutfitsAdapter(outfits.distinct(), object : OutfitsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenOutfit = outfits[position]
                outfitsRepository.getClothesForOutfit(chosenOutfit) {clothes ->
                    mainScreenActivity.replaceFragment(OutfitCardFragment(chosenOutfit, clothes, mainScreenActivity.calendarFragment, R.id.homePage), R.id.homePage)
                }

            }
        })
        outfitsContainer.adapter = adapter
        this.outfitsAdapter = adapter
    }

    fun putOutfitToDay(view: View, activity: Activity) {
        calendarRepository.getOutfitsFromCalendarEvent(calendarEvent) { _, outfits ->
            setupOutfitsRecyclerView(outfits, view, activity)
        }
    }

}