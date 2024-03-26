package ru.hse.termpaper.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.model.repository.ClothSeasonRepository
import ru.hse.termpaper.model.repository.ClothesRepository
import ru.hse.termpaper.view.NotificationHelper
import ru.hse.termpaper.view.adapters.CategoryCheckboxAdapter
import ru.hse.termpaper.view.adapters.SeasonCheckboxAdapter

class AddClothService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothesRepository: ClothesRepository = ClothesRepository(),
    private val clothCategories: MutableList<ClothCategory> = mutableListOf(),
    private val clothSeasons: MutableList<Season> = mutableListOf(),
    private var selectedImageUri: Uri? = null
) {

    fun setImage(result: ActivityResult, view: View?) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                view?.findViewById<ImageView>(R.id.clothImage)?.setImageURI(uri)
            }
        }
    }

    fun setupCategoryRecyclerView(view: View, context: Context) {
        clothCategoryRepository.getClothCategories { categories ->
            val categoryAdapter = CategoryCheckboxAdapter(categories, object : CategoryCheckboxAdapter.OnCheckboxClickListener{
                override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                    val chosenCategory = categories[position]
                    if (isChecked) {
                        clothCategories.add(chosenCategory)
                    } else {
                        clothCategories.remove(chosenCategory)
                    }
                }
            })
            val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryCheckboxRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonRecyclerView(view: View, context: Context) {
        val seasons = clothSeasonRepository.getSeasons()
        val seasonAdapter = SeasonCheckboxAdapter(seasons, object: SeasonCheckboxAdapter.OnCheckboxClickListener{
            override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                val chosenSeason = seasons[position]
                if (isChecked) {
                    clothSeasons.add(chosenSeason)
                } else {
                    clothSeasons.remove(chosenSeason)
                }
            }
        })
        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonCheckboxRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        seasonRecyclerView.adapter = seasonAdapter
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