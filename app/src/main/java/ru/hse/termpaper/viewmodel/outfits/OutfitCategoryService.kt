package ru.hse.termpaper.viewmodel.outfits

import android.widget.EditText
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.OutfitCategory
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.view.main.NotificationHelper

class OutfitCategoryService(
    private val outfitCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository()
) {

    fun saveCategoryWithOutfits(categoryTitle:EditText, outfitsInCategory: MutableList<Outfit>, notificationHelper: NotificationHelper) {
        val title = categoryTitle.text.toString().trim()

        outfitCategoryRepository.saveOutfitCategory(OutfitCategory("", "", title)) { success, message, category ->
            if (success) {
                outfitsInCategory.forEach { outfit ->
                    outfitCategoryRepository.addOutfitToCategory(outfit, category) { success, message ->
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