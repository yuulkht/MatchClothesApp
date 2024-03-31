package ru.hse.termpaper.viewmodel.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.repository.journey.JourneyRepository
import ru.hse.termpaper.view.adapters.button.JourneyAdapter
import ru.hse.termpaper.view.journeys.JourneyCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper

class JourneyRecyclerViewService(
    private val journeyRepository: JourneyRepository = JourneyRepository()
) {
    fun setupJourneyRecyclerView(view: View, context: Context, activity: MainScreenActivity) {
        journeyRepository.getJourneysFromUser { journeys ->
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