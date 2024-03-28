package ru.hse.termpaper.view.clothes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.clothes.ClothCategoryService
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class AddClothCategoryFragment(
    private val clothesViewModel: ClothesModelService = ClothesModelService(),
    private val clothCategoryViewModel: ClothCategoryService = ClothCategoryService()
) : Fragment() {

    private lateinit var categoryTitle: EditText
    private val clothesInCategory: MutableList<Cloth> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_cloth_category, container, false)
        val saveCategoryButton = view.findViewById<Button>(R.id.saveClothCategory)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        categoryTitle = view.findViewById(R.id.clothCategoryTitle)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        clothesViewModel.setupClothRecyclerView(clothesInCategory,view, requireContext())

        saveCategoryButton.setOnClickListener {
            clothCategoryViewModel.saveCategoryWithClothes(categoryTitle, clothesInCategory.distinct(), NotificationHelper(requireContext()))
            val mainActivity = requireActivity() as MainScreenActivity
            mainActivity.replaceFragment(mainActivity.clothesFragment, R.id.clothesPage)
        }

        return view
    }

}