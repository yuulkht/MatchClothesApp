package ru.hse.termpaper.viewmodel

import android.widget.EditText
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.view.NotificationHelper

class ClothCategoryService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository()
) {

    fun saveCategoryWithClothes(categoryTitle:EditText, clothesInCategory: MutableList<Cloth>, notificationHelper: NotificationHelper) {
        val title = categoryTitle.text.toString().trim()

        clothCategoryRepository.saveClothCategory(ClothCategory("", "", title)) { success, message, category ->
            if (success) {
                clothesInCategory.forEach { cloth ->
                    clothCategoryRepository.addClothToCategory(cloth, category) { success, message ->
                        if (!success) {
                            notificationHelper.showToast(message)
                        }
                    }
                }
            }
            notificationHelper.showToast(message)
        }
    }

}