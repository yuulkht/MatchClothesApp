package ru.hse.termpaper.view.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.outfits.OutfitCardService
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class OutfitCardFragment (
    private val outfit: Outfit,
    private val outfitCardService: OutfitCardService = OutfitCardService(),
    private val outfitsModelService: OutfitsModelService = OutfitsModelService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfit_card, container, false)

        val outfitTitle: TextView = view.findViewById(R.id.outfitTitle)
        val outfitImage: ImageView = view.findViewById(R.id.outfitImage)
        val additionalInfo: TextView = view.findViewById(R.id.additionalInfoText)
        val backLink: ImageView = view.findViewById(R.id.backButton)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)

        val notificationHelper = NotificationHelper(requireContext())

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        deleteButton.setOnClickListener {
            outfitsModelService.deleteOutfit(outfit) {_, message ->
                notificationHelper.showToast(message)
                mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
            }
        }

        outfitCardService.setupCategoryRecyclerView(outfit, view, requireContext())
        outfitCardService.setupSeasonRecyclerView(outfit, view, requireContext())
        outfitCardService.fillOutfitInfo(outfit, outfitTitle, additionalInfo, outfitImage)

        return view
    }
}
