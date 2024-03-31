package ru.hse.termpaper.view.journeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.journey.JourneyService
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class ChooseOutfitsForJourneyFragment(
    private val journey: Journey,
    private val nextFragment: Fragment = JourneyFragment(),
    private val outfitsForJourney: MutableList<Outfit> = mutableListOf(),
    private val chooseOutfitsForService: OutfitsModelService = OutfitsModelService(),
    private val journeyService: JourneyService = JourneyService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_outfits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val nextButton = view.findViewById<Button>(R.id.next)
        val outfitsContainer = view.findViewById<RecyclerView>(R.id.outfitsContainer)
        val addText = view.findViewById<TextView>(R.id.adding)

        addText.text = journey.title

        val mainScreenActivity = requireActivity() as MainScreenActivity

        chooseOutfitsForService.setupOutfitRecyclerView(outfitsForJourney, view, requireContext())

        backButton.setOnClickListener {
            if (chooseOutfitsForService.isOutfitsLoaded) {
                mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
            }
        }

        nextButton.setOnClickListener {
            if (chooseOutfitsForService.isOutfitsLoaded) {
                journeyService.saveOutfitsToJourney(outfitsForJourney, journey)
                mainScreenActivity.replaceFragment(nextFragment, R.id.homePage)
            }
        }
    }
}