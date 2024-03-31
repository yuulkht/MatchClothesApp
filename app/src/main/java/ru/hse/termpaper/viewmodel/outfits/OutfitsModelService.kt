package ru.hse.termpaper.viewmodel.outfits

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.viewmodel.recyclerview.OutfitRecyclerViewService

class OutfitsModelService(
    private val outfitsRepository: OutfitsRepository = OutfitsRepository(),
    var isOutfitsLoaded: Boolean = false
) : ViewModel() {

    fun getOutfitsForCurrentUser(callback: (MutableList<Outfit>) -> Unit) {
        outfitsRepository.getOutfits { outfitsList ->
            callback(outfitsList)
        }
    }

    fun getImage(outfit: Outfit, callback: (String?) -> Unit) {
        outfitsRepository.getImageForOutfit(outfit) {uri ->
            callback(uri)
        }
    }

    fun filterOutfitsByTitle(outfits: List<Outfit>, searchText: String): List<Outfit> {
        return if (searchText.isEmpty()) {
            outfits
        } else {
            outfits.filter { it.title.contains(searchText, ignoreCase = true) }
        }
    }

    fun deleteOutfit(outfit: Outfit, callback: (Boolean, String) -> Unit) {
        outfitsRepository.deleteOutfit(outfit) {success, message ->
            callback(success, message)
        }
    }

    fun setupOutfitRecyclerView(chosenOutfits: MutableList<Outfit>, view: View?, context: Context) {
        val outfitRecyclerViewService = OutfitRecyclerViewService()
        outfitRecyclerViewService.setupOutfitCheckboxRecyclerView(chosenOutfits, view, context)
        isOutfitsLoaded = true
    }

}
