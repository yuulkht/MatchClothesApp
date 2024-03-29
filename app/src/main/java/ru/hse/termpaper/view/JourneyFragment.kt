package ru.hse.termpaper.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.repository.JourneyRepository
import ru.hse.termpaper.view.adapters.ClothCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.JourneyAdapter
import ru.hse.termpaper.view.clothes.ClothesFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.outfits.ChooseClothCategoryDialogFragment

class JourneyFragment(
    private val journeyRepository: JourneyRepository = JourneyRepository()
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = view.findViewById<LinearLayout>(R.id.addButton)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        setupJourneyRecyclerView(view, requireContext(), mainScreenActivity)

        addButton.setOnClickListener {
            mainScreenActivity.replaceFragment(AddJourneyFragment(),R.id.homePage)
        }


    }

    fun setupJourneyRecyclerView(view: View, context: Context, activity: MainScreenActivity) {
        journeyRepository.getJourneysFromUser() { journeys ->
            val journeyRecyclerView: RecyclerView = view.findViewById(R.id.journeysContainer)
            journeyRecyclerView.layoutManager = LinearLayoutManager(context)
            val journeyAdapter = JourneyAdapter(journeys.distinct(), object : JourneyAdapter.OnItemClickListener {
                override fun onItemClick(journey: Journey) {
                    activity.replaceFragment(JourneyCardFragment(journey), R.id.homePage)
                }

                override fun onDeleteClick(journey: Journey) {
                    journeyRepository.deleteJourney(journey) {_,message ->
                        NotificationHelper(context).showToast(message)
                    }
                }
            })
            journeyRecyclerView.adapter = journeyAdapter
        }
    }


}