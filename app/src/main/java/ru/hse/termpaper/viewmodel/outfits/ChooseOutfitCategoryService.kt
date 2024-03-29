package ru.hse.termpaper.viewmodel.outfits

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.view.outfits.ChooseOutfitCategoryDialogFragment
import ru.hse.termpaper.viewmodel.recyclerview.OutfitRecyclerViewService

class ChooseOutfitCategoryService (
    private val outfitsCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository(),
    private val outfitSeasonRepository: OutfitSeasonRepository = OutfitSeasonRepository(),
    private val outfitRecyclerViewService: OutfitRecyclerViewService = OutfitRecyclerViewService()
) {

    fun setupCategoryRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
        outfitRecyclerViewService.setupCategoryClickRecyclerView(view, context,parentFragment,curFragment)
    }

    fun setupSeasonRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
        outfitRecyclerViewService.setupSeasonClickRecyclerView(view, context,parentFragment,curFragment)
    }
}