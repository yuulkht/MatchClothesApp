package ru.hse.termpaper.viewmodel.clothes

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.model.repository.clothes.ClothesRepository
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.recyclerview.ClothRecyclerViewService

class AddClothService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothesRepository: ClothesRepository = ClothesRepository(),
    private val clothCategories: MutableList<ClothCategory> = mutableListOf(),
    private val clothSeasons: MutableList<Season> = mutableListOf(),
    private var selectedImageUri: Uri? = null,
    private val clothRecyclerViewService: ClothRecyclerViewService = ClothRecyclerViewService()
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

    // Как будто общая штука
    fun setImage(uri: Uri?, view: View?, notificationHelper: NotificationHelper) {
        if (uri == null) {
            notificationHelper.showToast("Не удалось загрузить фотографию")
        } else {
            selectedImageUri = uri
            view?.findViewById<ImageView>(R.id.clothImage)?.setImageURI(uri)
        }
    }
    fun setupCategoryRecyclerView(view: View, context: Context) {
        clothRecyclerViewService.setupCategoryCheckboxRecyclerView(clothCategories, view, context)
    }

    fun setupSeasonRecyclerView(view: View, context: Context) {
        clothRecyclerViewService.setupSeasonCheckboxRecyclerView(clothSeasons, view, context)
    }

    fun saveCloth(title: String, info: String, notificationHelper: NotificationHelper) {
        selectedImageUri?.let{
            clothesRepository.saveCloth(Cloth("", "", title,"", info), selectedImageUri) { success, message, cloth ->
                notificationHelper.showToast(message)
                for (category: ClothCategory in clothCategories.distinct()) {
                    clothCategoryRepository.addClothToCategory(cloth, category) {success,message ->
                        if (!success) {
                            notificationHelper.showToast(message)
                        }
                    }
                }
                for (season: Season in clothSeasons.distinct()) {
                    clothSeasonRepository.addClothToSeason(cloth, season) {success,message ->
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