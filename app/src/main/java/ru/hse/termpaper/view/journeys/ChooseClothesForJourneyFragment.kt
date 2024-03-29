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
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService
import ru.hse.termpaper.viewmodel.journey.JourneyService

class ChooseClothesForJourneyFragment(
    private val journey: Journey,
    private val nextFragment: Fragment,
    private val clothesForJourney: MutableList<Cloth> = mutableListOf(),
    private val chooseClothesForService: ClothesModelService = ClothesModelService(),
    private val journeyService: JourneyService = JourneyService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_clothes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val nextButton = view.findViewById<Button>(R.id.next)
        val addText = view.findViewById<TextView>(R.id.adding)

        addText.text = journey.title

        val mainScreenActivity = requireActivity() as MainScreenActivity

        chooseClothesForService.setupClothRecyclerView(clothesForJourney, view, requireContext())

        backButton.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        nextButton.setOnClickListener {

            journeyService.saveClothesToJourney(clothesForJourney, journey)

            mainScreenActivity.replaceFragment(nextFragment, R.id.homePage)
        }
    }
}