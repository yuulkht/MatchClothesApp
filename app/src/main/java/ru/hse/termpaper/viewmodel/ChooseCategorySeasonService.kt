package ru.hse.termpaper.viewmodel

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.model.repository.ClothSeasonRepository
import ru.hse.termpaper.model.repository.ClothesRepository
import ru.hse.termpaper.view.adapters.CategoryButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.view.clothes.ChooseCategorySeasonDialogFragment
import ru.hse.termpaper.view.clothes.ClothesFragment

class ChooseCategorySeasonService (
    private val clothesCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository()
) {

    fun setupCategoryRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseCategorySeasonDialogFragment?) {
        clothesCategoryRepository.getClothCategories { categories ->
            val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryButtonRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            val categoryAdapter = CategoryButtonAdapter(categories, object : CategoryButtonAdapter.OnItemClickListener {
                override fun onItemClick(category: ClothCategory) {
                    clothesCategoryRepository.getClothesFromCategory(category) { _, clothes ->
                        val parent = parentFragment as? ClothesFragment
                        parent?.clothesScreenService?.updateClothes(parent.searchEditText, clothes, false)
                        curFragment?.dismiss()
                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseCategorySeasonDialogFragment?) {
        val seasons = clothSeasonRepository.getSeasons()
        val seasonRecyclerView: RecyclerView = view.findViewById(R.id.seasonButtonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        val seasonAdapter = SeasonButtonAdapter(seasons, object : SeasonButtonAdapter.OnItemClickListener {
            override fun onItemClick(season: Season) {
                clothSeasonRepository.getClothesFromSeason(season) { _, clothes ->
                    val parent = parentFragment as? ClothesFragment
                    parent?.clothesScreenService?.updateClothes(parent.searchEditText, clothes, false)
                    curFragment?.dismiss()
                }
            }
        })
        seasonRecyclerView.adapter = seasonAdapter
    }
}