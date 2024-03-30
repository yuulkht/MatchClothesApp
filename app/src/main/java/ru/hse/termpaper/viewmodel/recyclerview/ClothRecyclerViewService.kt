package ru.hse.termpaper.viewmodel.recyclerview

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.clothes.ClothCategoryRepository
import ru.hse.termpaper.model.repository.clothes.ClothSeasonRepository
import ru.hse.termpaper.view.adapters.ClothCategoryAdapter
import ru.hse.termpaper.view.adapters.ClothCategoryButtonAdapter
import ru.hse.termpaper.view.adapters.ClothCategoryCheckboxAdapter
import ru.hse.termpaper.view.adapters.ClothesAdapter
import ru.hse.termpaper.view.adapters.ClothesCheckboxAdapter
import ru.hse.termpaper.view.adapters.SeasonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonCheckboxAdapter
import ru.hse.termpaper.view.clothes.ChooseClothCategoryDialogFragment
import ru.hse.termpaper.view.clothes.ClothCardFragment
import ru.hse.termpaper.view.clothes.ClothesFragment
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ClothRecyclerViewService(
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private val clothesModelService: ClothesModelService = ClothesModelService(),

) {
    fun setupCategoryCheckboxRecyclerView(clothCategories: MutableList<ClothCategory>, view: View, context: Context) {
        clothCategories.clear()
        clothCategoryRepository.getClothCategories { categories ->
            val categoryAdapter = ClothCategoryCheckboxAdapter(categories.distinct(), object : ClothCategoryCheckboxAdapter.OnCheckboxClickListener{
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

    fun setupSeasonCheckboxRecyclerView(curSeasons: MutableList<Season>, view: View, context: Context) {
        curSeasons.clear()
        val seasons = clothSeasonRepository.getSeasons()
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

    fun setupCategoryClickRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
        clothCategoryRepository.getClothCategories { categories ->
            val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryButtonRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            val categoryAdapter = ClothCategoryButtonAdapter(categories.distinct(), object : ClothCategoryButtonAdapter.OnItemClickListener {
                override fun onItemClick(category: ClothCategory) {
                    clothCategoryRepository.getClothesFromCategory(category) { _, clothes ->
                        val parent = parentFragment as? ClothesFragment
                        parent?.clothesScreenService?.updateClothes(parent.searchEditText, clothes, false)
                        curFragment?.dismiss()
                    }
                }

                override fun onDeleteClick(category: ClothCategory) {
                    clothCategoryRepository.deleteCategory(category) {message ->
                        NotificationHelper(context).showToast(message)
                        curFragment?.dismiss()

                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    fun setupSeasonClickRecyclerView(view: View, context: Context, parentFragment: Fragment?, curFragment: ChooseClothCategoryDialogFragment?) {
        val seasons = clothSeasonRepository.getSeasons()
        val seasonRecyclerView: RecyclerView = view.findViewById(R.id.seasonButtonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        val seasonAdapter = SeasonButtonAdapter(seasons.distinct(), object : SeasonButtonAdapter.OnItemClickListener {
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

    fun setupClothesCheckboxRecyclerView(clothesInCategory: MutableList<Cloth>, view: View?, context: Context) {
        clothesInCategory.clear()
        clothesModelService.getClothesForCurrentUser { clothes ->
            val adapter = ClothesCheckboxAdapter(
                clothes.distinct(),
                object : ClothesCheckboxAdapter.OnCheckboxClickListener {
                    override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                        if (isChecked) {
                            clothesInCategory.add(clothes[position])
                        } else {
                            clothesInCategory.remove(clothes[position])
                        }
                    }
                })

            val recyclerView = view?.findViewById<RecyclerView>(R.id.clothesCheckboxContainer)
            recyclerView?.adapter = adapter
        }
    }

    fun setupCalendarClothesRecyclerView(clothes: MutableList<Cloth>, view: View, activity: Activity, ) {
        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)
        val mainScreenActivity = activity as MainScreenActivity

        val adapter = ClothesAdapter(clothes.distinct(), object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = clothes[position]
                mainScreenActivity.replaceFragment(ClothCardFragment(chosenCloth, mainScreenActivity.calendarFragment, R.id.homePage), R.id.homePage)
            }
        })
        clothesContainer.adapter = adapter
    }

    fun setupJourneyClothesRecyclerView(clothes: List<Cloth>, view: View, activity: Activity, fragment: Fragment) {
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
}