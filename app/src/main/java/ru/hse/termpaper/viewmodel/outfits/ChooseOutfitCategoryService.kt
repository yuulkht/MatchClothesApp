package ru.hse.termpaper.viewmodel.outfits

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.OutfitCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.adapters.ClothCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.view.outfits.ChooseOutfitCategoryDialogFragment
import ru.hse.termpaper.view.outfits.OutfitsFragment

class ChooseOutfitCategoryService (
    private val outfitsCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository(),
    private val outfitSeasonRepository: OutfitSeasonRepository = OutfitSeasonRepository()
) {

    fun setupCategoryRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
        outfitsCategoryRepository.getOutfitCategories { categories ->
            val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryButtonRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            val categoryAdapter = OutfitCategoryButtonAdapter(categories.distinct(), object : OutfitCategoryButtonAdapter.OnItemClickListener {
                override fun onItemClick(category: OutfitCategory) {
                    outfitsCategoryRepository.getOutfitsFromCategory(category) { _, outfits ->
                        val parent = parentFragment as? OutfitsFragment
                        parent?.outfitsScreenService?.updateOutfits(parent.searchEditText, outfits, false)
                        curFragment?.dismiss()
                    }
                }

                override fun onDeleteClick(category: OutfitCategory) {
                    outfitsCategoryRepository.deleteCategory(category) {message ->
                        NotificationHelper(context).showToast(message)
                        curFragment?.dismiss()

                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
        val seasons = outfitSeasonRepository.getSeasons()
        val seasonRecyclerView: RecyclerView = view.findViewById(R.id.seasonButtonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        val seasonAdapter = SeasonButtonAdapter(seasons.distinct(), object : SeasonButtonAdapter.OnItemClickListener {
            override fun onItemClick(season: Season) {
                outfitSeasonRepository.getOutfitsFromSeason(season) { _, outfits ->
                    val parent = parentFragment as? OutfitsFragment
                    parent?.outfitsScreenService?.updateOutfits(parent.searchEditText, outfits, false)
                    curFragment?.dismiss()
                }
            }
        })
        seasonRecyclerView.adapter = seasonAdapter
    }
}