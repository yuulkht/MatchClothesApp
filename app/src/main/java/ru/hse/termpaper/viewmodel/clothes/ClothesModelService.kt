package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.clothes.ClothesRepository
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService

class ClothesModelService(
    private val clothesRepository: ClothesRepository = ClothesRepository(),
    var isClothesLoaded: Boolean = false
) : ViewModel(){

    fun getImage(cloth: Cloth, callback: (String?) -> Unit) {
        clothesRepository.getImageForCloth(cloth) {uri ->
            callback(uri)
        }
    }

    fun getClothesForCurrentUser(callback: (MutableList<Cloth>) -> Unit) {
        clothesRepository.getClothes { clothesList ->
            callback(clothesList.toMutableList())
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

    fun setupClothRecyclerView(clothes: MutableList<Cloth>, view: View?, context: Context) {
        val clothRecyclerViewService = ClothRecyclerViewService()
        clothRecyclerViewService.setupClothesCheckboxRecyclerView(clothes, view, context)
        isClothesLoaded = true
    }

}
