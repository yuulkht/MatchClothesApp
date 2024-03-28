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

class ClothesOfDayFragment(
    private var calendarEvent: CalendarEvent,
    private val calendarRepository: CalendarRepository = CalendarRepository(),
    private var clothesAdapter: ClothesAdapter? = null,

    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clothes_of_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clothesContainer = view.findViewById<RecyclerView>(R.id.clothesContainer)
        val addClothButton = view.findViewById<LinearLayout>(R.id.addClothButton)
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val date = view.findViewById<TextView>(R.id.date)

        date.text = calendarEvent.date

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backButton.setOnClickListener{
            mainScreenActivity.replaceFragment(mainScreenActivity.calendarFragment, R.id.homePage)
        }

        putClothesToDay(view, requireActivity())


        addClothButton.setOnClickListener {
            mainScreenActivity.replaceFragment(AddClothesToDayFragment(calendarEvent), R.id.homePage)
        }
    }

    fun setupClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)
        val mainScreenActivity = activity as MainScreenActivity

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = clothes[position]
                mainScreenActivity.replaceFragment(ClothCardFragment(chosenCloth, mainScreenActivity.calendarFragment, R.id.homePage), R.id.homePage)
            }
        })
        clothesContainer.adapter = adapter
        this.clothesAdapter = adapter
    }

    fun putClothesToDay(view: View, activity: Activity) {
        calendarRepository.getClothesFromCalendarEvent(calendarEvent) { _, clothes ->
            setupClothesRecyclerView(clothes, view, activity)
        }
    }

}