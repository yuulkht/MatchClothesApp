package ru.hse.termpaper.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.repository.JourneyRepository
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ChooseClothesForJourneyFragment(
    private val journey: Journey,
    private val nextFragment: Fragment,
    private val clothesForJourney: MutableList<Cloth> = mutableListOf(),
    private val chooseClothesForService: ClothesModelService = ClothesModelService(),
    private  val journeyRepository: JourneyRepository = JourneyRepository()
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
        val clothesContainer = view.findViewById<RecyclerView>(R.id.clothesContainer)
        val addText = view.findViewById<TextView>(R.id.adding)

        addText.text = journey.title

        val mainScreenActivity = requireActivity() as MainScreenActivity

        chooseClothesForService.setupClothRecyclerView(clothesForJourney, view, requireContext())

        backButton.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        nextButton.setOnClickListener {
            for (cloth in clothesForJourney) {
                journeyRepository.addClothToJourney(cloth, journey){_,_ ->}
            }
            mainScreenActivity.replaceFragment(nextFragment, R.id.homePage)
        }
    }
}