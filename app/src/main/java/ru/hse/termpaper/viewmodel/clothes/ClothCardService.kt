package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.view.adapters.ClothCategoryAdapter
import ru.hse.termpaper.view.adapters.SeasonAdapter

class ClothCardService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothesService: ClothesModelService = ClothesModelService()
) {

    fun setupCategoryRecyclerView(cloth: Cloth, view: View, context: Context) {
        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        clothCategoryRepository.getCategoriesForCloth(cloth) { _, categories ->
            val categoryAdapter = ClothCategoryAdapter(categories.distinct())
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(cloth: Cloth, view: View, context: Context) {
        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)

        clothSeasonRepository.getSeasonsForCloth(cloth) { _, seasons ->
            val seasonAdapter = SeasonAdapter(seasons.distinct())
            seasonRecyclerView.adapter = seasonAdapter
        }
    }

    fun fillClothInfo(cloth: Cloth, clothTitle: TextView, additionalInfo: TextView, clothImage: ImageView) {
        clothTitle.text = cloth.title
        additionalInfo.text = cloth.information
        clothesService.getImage(cloth) {imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(clothImage)
            }
        }
    }
}