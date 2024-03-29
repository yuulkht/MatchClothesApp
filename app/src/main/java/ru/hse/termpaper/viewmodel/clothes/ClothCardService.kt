package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService

class ClothCardService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothesService: ClothesModelService = ClothesModelService(),
    private val clothRecyclerViewService: ClothRecyclerViewService = ClothRecyclerViewService()
) {

    fun setupCategoryRecyclerView(cloth: Cloth, view: View, context: Context) {
        clothRecyclerViewService.setupCategoryRecyclerView(cloth,view,context)
    }

    fun setupSeasonRecyclerView(cloth: Cloth, view: View, context: Context) {
        clothRecyclerViewService.setupSeasonRecyclerView(cloth, view, context)
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