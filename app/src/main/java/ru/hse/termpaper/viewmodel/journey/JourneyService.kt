package ru.hse.termpaper.viewmodel.journey

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Journey
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.journey.JourneyRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.journeys.ChooseClothesForJourneyFragment
import ru.hse.termpaper.view.journeys.ChooseOutfitsForJourneyFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService
import ru.hse.termpaper.viewmodel.recyclerview.JourneyRecyclerViewService

class JourneyService (
    private val journeyRepository: JourneyRepository = JourneyRepository(),
    private val outfitsRepository: OutfitsRepository = OutfitsRepository(),
    private val clothRecyclerViewService: ClothRecyclerViewService = ClothRecyclerViewService(),
    private val journeyRecyclerViewService: JourneyRecyclerViewService = JourneyRecyclerViewService(),
    var isSuitcaseLoaded: Boolean = false,
    var isJourneysLoaded: Boolean = false
) {

    fun saveJourney(journeyTitle: String, mainScreenActivity: MainScreenActivity) {
        journeyRepository.saveJourney(Journey("","", journeyTitle)){ success, journey ->
            if (success) {
                mainScreenActivity.replaceFragment(ChooseClothesForJourneyFragment(journey, ChooseOutfitsForJourneyFragment(journey)), R.id.homePage)
            }
        }
    }

    fun saveClothesToJourney(clothes: MutableList<Cloth>, journey: Journey) {
        for (cloth in clothes) {
            journeyRepository.addClothToJourney(cloth, journey){_,_ ->}
        }
    }

    fun saveOutfitsToJourney(outfits: MutableList<Outfit>, journey: Journey) {
        for (outfit in outfits) {
            journeyRepository.addOutfitToJourney(outfit, journey){_,_ ->}
        }
    }

    fun getSuitcaseForJourney(journey: Journey, callback: (List<Cloth>) -> Unit) {
        val suitcase: MutableList<Cloth> = mutableListOf()
        val tasks = mutableListOf<Task<Void>>()

        val clothesTask = TaskCompletionSource<Void>()
        journeyRepository.getClothesFromJourney(journey) { _, clothes ->
            suitcase.addAll(clothes)
            clothesTask.setResult(null)
        }
        tasks.add(clothesTask.task)

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

        Tasks.whenAll(tasks).addOnCompleteListener {
            callback(suitcase.distinct())
        }
    }

    fun setupClothesRecyclerView(clothes: List<Cloth>, view: View, activity: Activity, fragment: Fragment) {
        clothRecyclerViewService.setupJourneyClothesRecyclerView(clothes, view, activity, fragment)
        isSuitcaseLoaded = true
    }

    fun deleteClothesFromJourney(journey: Journey, context: Context, mainScreenActivity: MainScreenActivity, nextFragment: Fragment) {
        journeyRepository.getClothesFromJourney(journey) {_,clothes ->
            for (cloth in clothes) {
                journeyRepository.deleteClothFromJourney(cloth, journey) {success, message ->
                    if(!success) {
                        NotificationHelper(context).showToast(message)
                    }

                }
            }
            mainScreenActivity.replaceFragment(ChooseClothesForJourneyFragment(journey, nextFragment), R.id.homePage)
        }
    }

    fun deleteOutfitsFromJourney(journey: Journey, context: Context, mainScreenActivity: MainScreenActivity, nextFragment: Fragment) {
        journeyRepository.getOutfitsFromJourney(journey) {_,outfits ->
            for (outfit in outfits) {
                journeyRepository.deleteOutfitFromJourney(outfit, journey) {success, message ->
                    if(!success) {
                        NotificationHelper(context).showToast(message)
                    }

                }
            }
            mainScreenActivity.replaceFragment(ChooseOutfitsForJourneyFragment(journey, nextFragment), R.id.homePage)
        }
    }

    fun setupJourneyRecyclerView(view: View, context: Context, activity: MainScreenActivity) {
        journeyRecyclerViewService.setupJourneyRecyclerView(view, context, activity)
        isJourneysLoaded = true
    }

}