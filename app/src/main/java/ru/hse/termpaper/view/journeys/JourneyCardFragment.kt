package ru.hse.termpaper.view.journeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.journey.JourneyService

class JourneyCardFragment(
    private val journey: Journey,
    private val journeyService: JourneyService = JourneyService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_journey_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val journeyTitle = view.findViewById<TextView>(R.id.journeyTitle)
        val chooseClothesButton = view.findViewById<Button>(R.id.chooseClothes)
        val chooseOutfitsButton = view.findViewById<Button>(R.id.chooseOutfits)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        journeyTitle.text = journey.title

        journeyService.getSuitcaseForJourney(journey) {clothes ->
            journeyService.setupClothesRecyclerView(clothes, view, requireActivity(), this)
        }

        backButton.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        chooseClothesButton.setOnClickListener {
            journeyService.deleteClothesFromJourney(journey, requireContext(), mainScreenActivity, this)
        }

        chooseOutfitsButton.setOnClickListener {
            journeyService.deleteOutfitsFromJourney(journey, requireContext(), mainScreenActivity, this)
        }
    }
}