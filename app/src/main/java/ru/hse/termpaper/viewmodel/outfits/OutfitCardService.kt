package ru.hse.termpaper.viewmodel.outfits

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.view.adapters.ClothCategoryAdapter
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryAdapter
import ru.hse.termpaper.view.adapters.SeasonAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity

class OutfitCardService(
    private val outfitCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository(),
    private val outfitSeasonRepository: OutfitSeasonRepository = OutfitSeasonRepository(),
    private val outfitsService: OutfitsModelService = OutfitsModelService()
) {

    fun setupCategoryRecyclerView(outfit: Outfit, view: View, context: Context) {
        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        outfitCategoryRepository.getCategoriesForOutfit(outfit) { _, categories ->
            val categoryAdapter = OutfitCategoryAdapter(categories.distinct())
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(outfit: Outfit, view: View, context: Context) {
        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)

        outfitSeasonRepository.getSeasonsForOutfit(outfit) { _, seasons ->
            val seasonAdapter = SeasonAdapter(seasons.distinct())
            seasonRecyclerView.adapter = seasonAdapter
        }
    }

    fun setupClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, fragment: Fragment) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)

        val mainScreenActivity = activity as? MainScreenActivity

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = clothes.distinct()[position]
                mainScreenActivity?.replaceFragment(ClothCardFragment(chosenCloth, fragment, R.id.outfitsPage), R.id.outfitsPage)
            }
        })
        clothesContainer.adapter = adapter
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