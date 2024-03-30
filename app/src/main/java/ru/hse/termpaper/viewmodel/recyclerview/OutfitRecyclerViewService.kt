package ru.hse.termpaper.viewmodel.recyclerview

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.model.entity.OutfitCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.outfits.OutfitCategoryRepository
import ru.hse.termpaper.model.repository.outfits.OutfitSeasonRepository
import ru.hse.termpaper.model.repository.outfits.OutfitsRepository
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.OutfitCategoryCheckboxAdapter
import ru.hse.termpaper.view.adapters.OutfitsAdapter
import ru.hse.termpaper.view.adapters.OutfitsCheckboxAdapter
import ru.hse.termpaper.view.adapters.SeasonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonCheckboxAdapter
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.outfits.ChooseOutfitCategoryDialogFragment
import ru.hse.termpaper.view.outfits.OutfitCardFragment
import ru.hse.termpaper.view.outfits.OutfitsFragment
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class OutfitRecyclerViewService(
    private val outfitsCategoryRepository: OutfitCategoryRepository = OutfitCategoryRepository(),
    private val outfitSeasonRepository: OutfitSeasonRepository = OutfitSeasonRepository(),
    private val outfitsRepository: OutfitsRepository = OutfitsRepository(),
    private val outfitsModelService: OutfitsModelService = OutfitsModelService()

) {
    fun setupClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {}
        })
        clothesContainer.adapter = adapter
    }
    fun setupCategoryCheckboxRecyclerView(outfitCategories: MutableList<OutfitCategory>, view: View, context: Context) {
        outfitCategories.clear()
        outfitsCategoryRepository.getOutfitCategories { categories ->
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
    fun setupSeasonCheckboxRecyclerView(curSeasons: MutableList<Season>, view: View, context: Context) {
        curSeasons.clear()
        val seasons = outfitSeasonRepository.getSeasons()
        val seasonAdapter = SeasonCheckboxAdapter(seasons.distinct(), object: SeasonCheckboxAdapter.OnCheckboxClickListener{
            override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                val chosenSeason = seasons[position]
                if (isChecked) {
                    curSeasons.add(chosenSeason)
                } else {
                    curSeasons.remove(chosenSeason)
                }
            }
        })
        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonCheckboxRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        seasonRecyclerView.adapter = seasonAdapter
    }

    fun setupCategoryClickRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
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

    fun setupSeasonClickRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseOutfitCategoryDialogFragment?) {
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

    fun setupCategoryRecyclerView(outfit: Outfit, view: View, context: Context) {
        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        outfitsCategoryRepository.getCategoriesForOutfit(outfit) { _, categories ->
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

    fun setupClothesCardRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, fragment: Fragment) {
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

    fun setupOutfitCheckboxRecyclerView(chosenOutfits: MutableList<Outfit>, view: View?, context: Context) {
        chosenOutfits.clear()
        outfitsModelService.getOutfitsForCurrentUser { outfits ->
            val adapter = OutfitsCheckboxAdapter(
                outfits.distinct(),
                object : OutfitsCheckboxAdapter.OnCheckboxClickListener {
                    override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                        if (isChecked) {
                            chosenOutfits.add(outfits[position])
                        } else {
                            chosenOutfits.remove(outfits[position])
                        }
                    }
                })

            val recyclerView = view?.findViewById<RecyclerView>(R.id.outfitsCheckboxContainer)
            recyclerView?.adapter = adapter
        }
    }

    fun setupCalendarOutfitsRecyclerView(outfits: MutableList<Outfit>, view: View, activity: Activity, ) {
        val outfitsContainer: RecyclerView = view.findViewById(R.id.outfitsContainer)
        val mainScreenActivity = activity as MainScreenActivity

        val adapter = OutfitsAdapter(outfits.distinct(), object : OutfitsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenOutfit = outfits[position]
                outfitsRepository.getClothesForOutfit(chosenOutfit) {clothes ->
                    mainScreenActivity.replaceFragment(OutfitCardFragment(chosenOutfit, clothes, mainScreenActivity.calendarFragment, R.id.homePage), R.id.homePage)
                }

            }
        })
        outfitsContainer.adapter = adapter
    }
}