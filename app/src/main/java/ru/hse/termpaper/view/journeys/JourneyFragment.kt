package ru.hse.termpaper.view.journeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.journey.JourneyService

class JourneyFragment(
    private val journeyService: JourneyService = JourneyService()
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_journeys, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = view.findViewById<LinearLayout>(R.id.addButton)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        journeyService.setupJourneyRecyclerView(view, requireContext(), mainScreenActivity)

        addButton.setOnClickListener {
            if (journeyService.isJourneysLoaded) {
                mainScreenActivity.replaceFragment(AddJourneyFragment(),R.id.homePage)
            }
        }
    }
}