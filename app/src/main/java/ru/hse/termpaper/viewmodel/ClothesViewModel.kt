package ru.hse.termpaper.viewmodel

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.User
import ru.hse.termpaper.model.repository.ClothesRepository
import ru.hse.termpaper.model.repository.UserRepository

class ClothesViewModel(
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

}
