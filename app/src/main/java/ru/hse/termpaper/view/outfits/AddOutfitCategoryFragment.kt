package ru.hse.termpaper.view.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.outfits.OutfitCategoryService
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class AddOutfitCategoryFragment(
    private val outfitsViewModel: OutfitsModelService = OutfitsModelService(),
    private val outfitCategoryViewModel: OutfitCategoryService = OutfitCategoryService()
) : Fragment() {

    private lateinit var categoryTitle: EditText
    private val outfitsInCategory: MutableList<Outfit> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_outfit_category, container, false)
        val saveCategoryButton = view.findViewById<Button>(R.id.saveOutfitCategory)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        categoryTitle = view.findViewById(R.id.outfitCategoryTitle)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        outfitsViewModel.setupOutfitRecyclerView(outfitsInCategory,view, requireContext())

        saveCategoryButton.setOnClickListener {
            outfitCategoryViewModel.saveCategoryWithOutfits(categoryTitle, outfitsInCategory.distinct(), NotificationHelper(requireContext()))
            val mainActivity = requireActivity() as MainScreenActivity
            mainActivity.replaceFragment(mainActivity.outfitsFragment, R.id.outfitsPage)
        }

        return view
    }

}