package ru.hse.termpaper.view.clothes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.clothes.ClothCardService
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ClothCardFragment (
    private val cloth: Cloth,
    private val previousFragment: Fragment,
    private val menuSection: Int,
    private val clothCardService: ClothCardService = ClothCardService(),
    private val clothesModelService: ClothesModelService = ClothesModelService()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cloth_card, container, false)

        val clothTitle = view.findViewById<TextView>(R.id.clothTitle)
        val clothImage = view.findViewById<ImageView>(R.id.clothImage)
        val additionalInfo = view.findViewById<TextView>(R.id.additionalInfoText)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val deleteButton = view.findViewById<ImageView>(R.id.deleteButton)

        val notificationHelper = NotificationHelper(requireContext())

        val mainScreenActivity = requireActivity() as MainScreenActivity

        clothCardService.setupCategoryRecyclerView(cloth, view, requireContext())
        clothCardService.setupSeasonRecyclerView(cloth, view, requireContext())
        clothCardService.fillClothInfo(cloth, clothTitle, additionalInfo, clothImage)

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(previousFragment, menuSection)
        }

        deleteButton.setOnClickListener {
            clothesModelService.deleteCloth(cloth) {_, message ->
                notificationHelper.showToast(message)
                mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
            }
        }

        return view
    }
}
