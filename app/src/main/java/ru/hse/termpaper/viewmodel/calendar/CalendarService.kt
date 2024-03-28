package ru.hse.termpaper.viewmodel.calendar
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity

class CalendarService(
    private val clothesModelService: ClothesModelService = ClothesModelService(),
    private var clothesList: MutableList<Cloth> = mutableListOf(),
    private var adapter: ClothesAdapter? = null
) {
    fun setupClothesRecyclerView(view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)
        val mainScreenActivity = activity as MainScreenActivity

        val adapter = ClothesAdapter(clothesList.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = clothesList[position]
                mainScreenActivity.replaceFragment(ClothCardFragment(chosenCloth, mainScreenActivity.clothesFragment, R.id.clothesPage), R.id.clothesPage)
            }
        })
        clothesContainer.adapter = adapter
        this.adapter = adapter
    }

    fun loadClothes() {

    }

    fun updateClothes() {

    }
}