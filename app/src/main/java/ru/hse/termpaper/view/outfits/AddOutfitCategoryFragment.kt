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
    private val outfitsService: OutfitsModelService = OutfitsModelService(),
    private val outfitCategoryService: OutfitCategoryService = OutfitCategoryService(),
    private val outfitsInCategory: MutableList<Outfit> = mutableListOf()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_outfit_category, container, false)

        val saveCategoryButton = view.findViewById<Button>(R.id.saveOutfitCategory)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val categoryTitle = view.findViewById<EditText>(R.id.outfitCategoryTitle)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        outfitsService.setupOutfitRecyclerView(outfitsInCategory,view, requireContext())

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        saveCategoryButton.setOnClickListener {
            outfitCategoryService.saveCategoryWithOutfits(categoryTitle, outfitsInCategory.distinct(), NotificationHelper(requireContext()))
            val mainActivity = requireActivity() as MainScreenActivity
            mainActivity.replaceFragment(mainActivity.outfitsFragment, R.id.outfitsPage)
        }

        return view
    }

}