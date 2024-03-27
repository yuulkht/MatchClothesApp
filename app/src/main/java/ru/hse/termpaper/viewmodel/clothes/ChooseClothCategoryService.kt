package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.adapters.ClothCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.view.clothes.ClothesFragment
import ru.hse.termpaper.view.outfits.ChooseClothCategoryDialogFragment

class ChooseClothCategoryService (
    private val clothesCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository()
) {

    fun setupCategoryRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
        clothesCategoryRepository.getClothCategories { categories ->
            val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryButtonRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            val categoryAdapter = ClothCategoryButtonAdapter(categories, object : ClothCategoryButtonAdapter.OnItemClickListener {
                override fun onItemClick(category: ClothCategory) {
                    clothesCategoryRepository.getClothesFromCategory(category) { _, clothes ->
                        val parent = parentFragment as? ClothesFragment
                        parent?.clothesScreenService?.updateClothes(parent.searchEditText, clothes, false)
                        curFragment?.dismiss()
                    }
                }

                override fun onDeleteClick(category: ClothCategory) {
                    clothesCategoryRepository.deleteCategory(category) {message ->
                        NotificationHelper(context).showToast(message)
                        curFragment?.dismiss()

                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
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