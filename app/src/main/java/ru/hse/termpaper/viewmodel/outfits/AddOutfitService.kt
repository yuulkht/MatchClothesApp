package ru.hse.termpaper.viewmodel.outfits

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.OutfitCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.adapters.ClothCategoryCheckboxAdapter
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryCheckboxAdapter
import ru.hse.termpaper.view.adapters.SeasonCheckboxAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity

class AddOutfitService(
    private val outfitCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository(),
    private val outfitSeasonRepository: OutfitSeasonRepository = OutfitSeasonRepository(),
    private val outfitsRepository: OutfitsRepository = OutfitsRepository(),
    private val outfitCategories: MutableList<OutfitCategory> = mutableListOf(),
    private val outfitSeasons: MutableList<Season> = mutableListOf(),
    private var selectedImageUri: Uri? = null
) {
    fun startCrop(cropImage: ActivityResultLauncher<CropImageContractOptions>) {
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = true,
                    imageSourceIncludeGallery = true,
                    guidelines = CropImageView.Guidelines.ON,
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    fixAspectRatio = true,
                    showCropLabel = true,
                    showCropOverlay = true,
                    showIntentChooser = true,
                ),
            ),
        )
    }
    fun setImage(uri: Uri?, view: View?, notificationHelper: NotificationHelper) {
        if (uri == null) {
            notificationHelper.showToast("Не удалось загрузить фотографию")
        } else {
            selectedImageUri = uri
            view?.findViewById<ImageView>(R.id.outfitImage)?.setImageURI(uri)
        }
    }

    fun setupClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {}
        })
        clothesContainer.adapter = adapter
    }

    fun setupCategoryRecyclerView(view: View, context: Context) {
        outfitCategoryRepository.getOutfitCategories { categories ->
            val categoryAdapter = OutfitCategoryCheckboxAdapter(categories.distinct(), object : OutfitCategoryCheckboxAdapter.OnCheckboxClickListener{
                override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                    val chosenCategory = categories[position]
                    if (isChecked) {
                        outfitCategories.add(chosenCategory)
                    } else {
                        outfitCategories.remove(chosenCategory)
                    }
                }
            })
            val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryCheckboxRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(view: View, context: Context) {
        val seasons = outfitSeasonRepository.getSeasons()
        val seasonAdapter = SeasonCheckboxAdapter(seasons.distinct(), object: SeasonCheckboxAdapter.OnCheckboxClickListener{
            override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                val chosenSeason = seasons[position]
                if (isChecked) {
                    outfitSeasons.add(chosenSeason)
                } else {
                    outfitSeasons.remove(chosenSeason)
                }
            }
        })
        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonCheckboxRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        seasonRecyclerView.adapter = seasonAdapter
    }

    fun saveOutfit(title: String, info: String, clothes: MutableList<Cloth>, notificationHelper: NotificationHelper) {
        selectedImageUri?.let{
            outfitsRepository.saveOutfit(Outfit("", "", title,"", info), selectedImageUri,  clothes.distinct()) { success, message, outfit ->
                notificationHelper.showToast(message)
                for (category: OutfitCategory in outfitCategories.distinct()) {
                    outfitCategoryRepository.addOutfitToCategory(outfit, category) {success,message ->
                        if (!success) {
                            notificationHelper.showToast(message)
                        }
                    }
                }
                for (season: Season in outfitSeasons.distinct()) {
                    outfitSeasonRepository.addOutfitToSeason(outfit, season) {success,message ->
                        if (!success) {
                            notificationHelper.showToast(message)
                        }
                    }
                }
            }
        } ?: run {
            notificationHelper.showToast("Фотография не загружена")
        }
    }
}