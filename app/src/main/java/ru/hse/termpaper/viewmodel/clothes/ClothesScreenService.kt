package ru.hse.termpaper.viewmodel.clothes

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService

class ClothesScreenService(
    private val clothesModelService: ClothesModelService = ClothesModelService(),
    private var currentSearchText: String = "",
    private var clothesList: MutableList<Cloth> = mutableListOf(),
    private var filteredList: MutableList<Cloth> = mutableListOf(),
    private var adapter: ClothesAdapter? = null,
) {
    fun loadClothes(curSearchText: String) {
        currentSearchText = curSearchText
        clothesModelService.getClothesForCurrentUser { clothes ->
            clothesList = clothes.toMutableList()
            updateFilteredList(currentSearchText)
            adapter?.notifyDataSetChanged()
        }
    }

    fun updateFilteredList(searchText: String) {
        currentSearchText = searchText
        filteredList = clothesModelService.filterClothesByTitle(clothesList, searchText).toMutableList()
        adapter?.updateItems(filteredList)
    }

    fun updateClothes(searchEditText: EditText?, clothes: MutableList<Cloth>, reset: Boolean) {
        searchEditText?.text?.clear()
        if (clothes.isEmpty() && reset) {
            filteredList = clothesList
        } else if (clothes.isEmpty()){
            filteredList.clear()
        } else {
            filteredList = clothes
        }
        adapter?.updateItems(filteredList)
    }

    fun setupClothesRecyclerView(view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)

        val mainScreenActivity = activity as MainScreenActivity

        val adapter = ClothesAdapter(filteredList.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = filteredList[position]
                mainScreenActivity.replaceFragment(ClothCardFragment(chosenCloth, mainScreenActivity.clothesFragment, R.id.clothesPage), R.id.clothesPage)
            }
        })
        clothesContainer.adapter = adapter
        this.adapter = adapter
    }
}