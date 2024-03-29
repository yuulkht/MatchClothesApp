package ru.hse.termpaper.view.outfits

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ChooseClothesForOutfit(
    private val chooseClothesForService: ClothesModelService = ClothesModelService(),
    private val clothesInOutfit: MutableList<Cloth> = mutableListOf()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_clothes, container, false)

        val nextButton = view.findViewById<Button>(R.id.next)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val addingText = view.findViewById<TextView>(R.id.adding)

        addingText.text = "Добавление образа"

        val mainScreenActivity = requireActivity() as MainScreenActivity

        chooseClothesForService.setupClothRecyclerView(clothesInOutfit,view, requireContext())

        nextButton.setOnClickListener {
            val mainActivity = requireActivity() as MainScreenActivity
            if (clothesInOutfit.isEmpty()) {
                NotificationHelper( requireContext()).showToast( "Вы не выбрали вещи для образа")
                mainActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
            } else {
                mainActivity.replaceFragment(AddOutfitFragment(clothesInOutfit), R.id.outfitsPage)
            }
        }

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        return view
    }
}