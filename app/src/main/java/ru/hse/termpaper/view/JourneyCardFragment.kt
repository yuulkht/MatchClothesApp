package ru.hse.termpaper.view

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.repository.JourneyRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper

class JourneyCardFragment(
    private val journey: Journey,
    private val journeyRepository: JourneyRepository = JourneyRepository(),
    private val outfitsRepository: OutfitsRepository = OutfitsRepository()
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

        getSuitcaseForJourney(journey){clothes ->
            setupClothesRecyclerView(clothes, view, requireActivity(), this)
        }

        backButton.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.journeyFragment, R.id.homePage)
        }

        chooseClothesButton.setOnClickListener {
            journeyRepository.getClothesFromJourney(journey) {_,clothes ->
                for (cloth in clothes) {
                    journeyRepository.deleteClothFromJourney(cloth, journey) {success, message ->
                        if(!success) {
                            NotificationHelper(requireContext()).showToast(message)
                        }

                    }
                }
                mainScreenActivity.replaceFragment(ChooseClothesForJourneyFragment(journey, this), R.id.homePage)
            }
        }

        chooseOutfitsButton.setOnClickListener {
            journeyRepository.getOutfitsFromJourney(journey) {_,outfits ->
                for (outfit in outfits) {
                    journeyRepository.deleteOutfitFromJourney(outfit, journey) {success, message ->
                        if(!success) {
                            NotificationHelper(requireContext()).showToast(message)
                        }

                    }
                }
                mainScreenActivity.replaceFragment(ChooseOutfitsForJourneyFragment(journey, this), R.id.homePage)
            }
        }
    }

    fun setupClothesRecyclerView(clothes: List<Cloth>, view: View, activity: Activity, fragment: Fragment) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)

        val mainScreenActivity = activity as? MainScreenActivity

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = clothes.distinct()[position]
                mainScreenActivity?.replaceFragment(ClothCardFragment(chosenCloth, fragment, R.id.outfitsPage), R.id.outfitsPage)
            }
        })
        clothesContainer.adapter = adapter
    }

    fun getSuitcaseForJourney(journey: Journey, callback: (List<Cloth>) -> Unit) {
        val suitcase: MutableList<Cloth> = mutableListOf()
        val tasks = mutableListOf<Task<Void>>()

        // Получаем список вещей из путешествия
        val clothesTask = TaskCompletionSource<Void>()
        journeyRepository.getClothesFromJourney(journey) { _, clothes ->
            suitcase.addAll(clothes)
            clothesTask.setResult(null)
        }
        tasks.add(clothesTask.task)

        // Получаем список образов из путешествия
        val outfitsTask = TaskCompletionSource<Void>()
        journeyRepository.getOutfitsFromJourney(journey) { _, outfits ->
            val outfitTasks = mutableListOf<Task<Void>>()
            for (outfit in outfits) {
                val outfitTask = TaskCompletionSource<Void>()
                outfitsRepository.getClothesForOutfit(outfit) { clothes ->
                    suitcase.addAll(clothes)
                    outfitTask.setResult(null)
                }
                outfitTasks.add(outfitTask.task)
            }
            Tasks.whenAll(outfitTasks).addOnCompleteListener {
                outfitsTask.setResult(null)
            }
        }
        tasks.add(outfitsTask.task)

        // Когда все асинхронные операции завершатся, вызываем callback с результатом
        Tasks.whenAll(tasks).addOnCompleteListener {
            callback(suitcase.distinct())
        }
    }
}