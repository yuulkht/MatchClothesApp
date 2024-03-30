package ru.hse.termpaper.viewmodel.outfits

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.basic.OutfitsAdapter
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.outfits.OutfitCardFragment

class OutfitsScreenService(
    private val outfitsModelService: OutfitsModelService = OutfitsModelService(),
    private val outfitsRepository: OutfitsRepository = OutfitsRepository(),
    private var currentSearchText: String = "",
    private var outfitsList: MutableList<Outfit> = mutableListOf(),
    private var filteredList: MutableList<Outfit> = mutableListOf(),
    private var adapter: OutfitsAdapter? = null,
) {

    fun loadOutfits(curSearchText: String) {
        currentSearchText = curSearchText
        outfitsModelService.getOutfitsForCurrentUser { outfits ->
            outfitsList = outfits.toMutableList()
            updateFilteredList(currentSearchText)
            adapter?.notifyDataSetChanged()
        }
    }

    fun updateFilteredList(searchText: String) {
        currentSearchText = searchText
        filteredList = outfitsModelService.filterOutfitsByTitle(outfitsList, searchText).toMutableList()
        adapter?.updateItems(filteredList)
    }

    fun updateOutfits(searchEditText: EditText?, outfits: MutableList<Outfit>, reset: Boolean) {
        searchEditText?.text?.clear()
        if (outfits.isEmpty() && reset) {
            filteredList = outfitsList
        } else if (outfits.isEmpty()){
            filteredList.clear()
        } else {
            filteredList = outfits
        }
        adapter?.updateItems(filteredList)
    }

    fun setupOutfitsRecyclerView(view: View, activity: Activity, ) {
        val outfitsContainer: RecyclerView = view.findViewById(R.id.outfitsContainer)

        val mainScreenActivity = activity as MainScreenActivity

        val adapter = OutfitsAdapter(filteredList.distinct(), object : OutfitsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenOutfit = filteredList[position]
                outfitsRepository.getClothesForOutfit(chosenOutfit) {clothes ->
                    mainScreenActivity.replaceFragment(OutfitCardFragment(chosenOutfit, clothes, mainScreenActivity.outfitsFragment, R.id.outfitsPage), R.id.outfitsPage)
                }
            }
        })
        outfitsContainer.adapter = adapter
        this.adapter = adapter
    }
}