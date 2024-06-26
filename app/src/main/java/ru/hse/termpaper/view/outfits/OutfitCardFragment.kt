package ru.hse.termpaper.view.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.outfits.OutfitCardService
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class OutfitCardFragment (
    private val outfit: Outfit,
    private val clothes: MutableList<Cloth>,
    private val previousFragment: Fragment,
    private val menuSection: Int,
    private val outfitCardService: OutfitCardService = OutfitCardService(),
    private val outfitsModelService: OutfitsModelService = OutfitsModelService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_card, container, false)

        val outfitTitle = view.findViewById<TextView>(R.id.outfitTitle)
        val outfitImage = view.findViewById<ImageView>(R.id.outfitImage)
        val additionalInfo = view.findViewById<TextView>(R.id.additionalInfoText)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val deleteButton = view.findViewById<ImageView>(R.id.deleteButton)

        val notificationHelper = NotificationHelper(requireContext())

        val mainScreenActivity = requireActivity() as MainScreenActivity

        outfitCardService.setupClothesRecyclerView(clothes, view, requireActivity(), this)
        outfitCardService.setupCategoryRecyclerView(outfit, view, requireContext())
        outfitCardService.setupSeasonRecyclerView(outfit, view, requireContext())
        outfitCardService.fillOutfitInfo(outfit, outfitTitle, additionalInfo, outfitImage)

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(previousFragment, menuSection)
        }

        deleteButton.setOnClickListener {
            outfitsModelService.deleteOutfit(outfit) {_, message ->
                notificationHelper.showToast(message)
                mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
            }
        }

        return view
    }
}
