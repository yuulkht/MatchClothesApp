package ru.hse.termpaper.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.ClothesRepository
import ru.hse.termpaper.view.adapters.ClothesCheckboxAdapter

class ClothesModelService(
    private val clothesRepository: ClothesRepository = ClothesRepository()
) : ViewModel() {

    fun getClothesForCurrentUser(callback: (MutableList<Cloth>) -> Unit) {
        clothesRepository.getClothes { clothesList ->
            callback(clothesList)
        }
    }

    fun getImage(cloth: Cloth, callback: (String?) -> Unit) {
        clothesRepository.getImageForCloth(cloth) {uri ->
            callback(uri)
        }
    }

    fun filterClothesByTitle(clothes: List<Cloth>, searchText: String): List<Cloth> {
        return if (searchText.isEmpty()) {
            clothes
        } else {
            clothes.filter { it.title.contains(searchText, ignoreCase = true) }
        }
    }

    fun setupClothRecyclerView(clothesInCategory: MutableList<Cloth>, view: View?, context: Context) {
        getClothesForCurrentUser { clothes ->
            val adapter = ClothesCheckboxAdapter(
                clothes,
                object : ClothesCheckboxAdapter.OnCheckboxClickListener {
                    override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                        if (isChecked) {
                            clothesInCategory.add(clothes[position])
                        } else {
                            clothesInCategory.remove(clothes[position])
                        }
                    }
                })

            val recyclerView = view?.findViewById<RecyclerView>(R.id.clothesCheckboxContainer)
            recyclerView?.adapter = adapter
        }
    }

}
