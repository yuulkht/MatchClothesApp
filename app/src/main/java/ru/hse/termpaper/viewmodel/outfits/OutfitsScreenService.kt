package ru.hse.termpaper.viewmodel.outfits

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.adapters.OutfitsAdapter
import ru.hse.termpaper.view.outfits.OutfitCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity

class OutfitsScreenService(
    private val outfitsModelService: OutfitsModelService = OutfitsModelService(),
    private var currentSearchText: String = "",
    private var outfitsList: MutableList<Outfit> = mutableListOf(),
    private var filteredList: MutableList<Outfit> = mutableListOf(),
    private var adapter: OutfitsAdapter? = null
) {
    fun setupOutfitsRecyclerView(view: View, activity: Activity, ) {
        val outfitsContainer: RecyclerView = view.findViewById(R.id.outfitsContainer)

        val mainScreenActivity = activity as MainScreenActivity

        val adapter = OutfitsAdapter(filteredList, object : OutfitsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenOutfit = filteredList[position]
                mainScreenActivity.replaceFragment(OutfitCardFragment(chosenOutfit), R.id.outfitsPage)
            }
        })
        outfitsContainer.adapter = adapter
        this.adapter = adapter
    }

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
            adapter?.updateItems(filteredList)
        } else if (outfits.isEmpty()){
            filteredList.clear()
            adapter?.updateItems(filteredList)
        } else {
            filteredList = outfits
            adapter?.updateItems(filteredList)
        }
    }
}