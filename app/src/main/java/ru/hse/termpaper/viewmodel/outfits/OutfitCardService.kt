package ru.hse.termpaper.viewmodel.outfits

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.viewmodel.recyclerview.OutfitRecyclerViewService

class OutfitCardService(
    private val outfitsService: OutfitsModelService = OutfitsModelService(),
    private val outfitRecyclerViewService: OutfitRecyclerViewService = OutfitRecyclerViewService()
) {

    fun setupCategoryRecyclerView(outfit: Outfit, view: View, context: Context) {
        outfitRecyclerViewService.setupCategoryRecyclerView(outfit, view, context)
    }

    fun setupSeasonRecyclerView(outfit: Outfit, view: View, context: Context) {
        outfitRecyclerViewService.setupSeasonRecyclerView(outfit, view, context)
    }

    fun setupClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, fragment: Fragment) {
        outfitRecyclerViewService.setupClothesCardRecyclerView(clothes, view, activity, fragment)
    }

    fun fillOutfitInfo(outfit: Outfit, outfitTitle: TextView, additionalInfo: TextView, outfitImage: ImageView) {
        outfitTitle.text = outfit.title
        additionalInfo.text = outfit.information
        outfitsService.getImage(outfit) {imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(outfitImage)
            }
        }
    }
}