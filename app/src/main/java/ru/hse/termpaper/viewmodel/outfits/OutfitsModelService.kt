package ru.hse.termpaper.viewmodel.outfits

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.OutfitsCheckboxAdapter

class OutfitsModelService(
    private val outfitsRepository: OutfitsRepository = OutfitsRepository()
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
        getOutfitsForCurrentUser { outfits ->
            val adapter = OutfitsCheckboxAdapter(
                outfits.distinct(),
                object : OutfitsCheckboxAdapter.OnCheckboxClickListener {
                    override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                        if (isChecked) {
                            chosenOutfits.add(outfits[position])
                        } else {
                            chosenOutfits.remove(outfits[position])
                        }
                    }
                })

            val recyclerView = view?.findViewById<RecyclerView>(R.id.outfitsCheckboxContainer)
            recyclerView?.adapter = adapter
        }
    }

}
