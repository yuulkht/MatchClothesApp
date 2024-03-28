package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.clothes.ClothesRepository
import ru.hse.termpaper.view.adapters.ClothesCheckboxAdapter

class ClothesModelService(
    private val clothesRepository: ClothesRepository = ClothesRepository()
) : ViewModel(){

    fun getImage(cloth: Cloth, callback: (String?) -> Unit) {
        clothesRepository.getImageForCloth(cloth) {uri ->
            callback(uri)
        }
    }

    fun getClothesForCurrentUser(callback: (MutableList<Cloth>) -> Unit) {
        clothesRepository.getClothes { clothesList ->
            callback(clothesList)
        }
    }

    fun filterClothesByTitle(clothes: List<Cloth>, searchText: String): List<Cloth> {
        return if (searchText.isEmpty()) {
            clothes
        } else {
            clothes.filter { it.title.contains(searchText, ignoreCase = true) }
        }
    }

    fun deleteCloth(cloth: Cloth, callback: (Boolean, String) -> Unit) {
        clothesRepository.deleteCloth(cloth) {success, message ->
            callback(success, message)
        }
    }

    fun setupClothRecyclerView(clothesInCategory: MutableList<Cloth>, view: View?, context: Context) {
        getClothesForCurrentUser { clothes ->
            val adapter = ClothesCheckboxAdapter(
                clothes.distinct(),
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