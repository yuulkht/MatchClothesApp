package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.view.outfits.ChooseClothCategoryDialogFragment
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService

class ChooseClothCategoryService (
    private val clothesCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothRecyclerViewService: ClothRecyclerViewService = ClothRecyclerViewService()
) {

    fun setupCategoryRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
        clothRecyclerViewService.setupCategoryClickRecyclerView(view,context,parentFragment,curFragment)
    }

    fun setupSeasonRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
        clothRecyclerViewService.setupSeasonClickRecyclerView(view,context,parentFragment,curFragment)
    }
}