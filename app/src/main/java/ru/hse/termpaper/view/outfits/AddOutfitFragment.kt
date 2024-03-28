package ru.hse.termpaper.view.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.outfits.AddOutfitService
import com.canhub.cropper.CropImageContract
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class AddOutfitFragment (
    private val clothes: MutableList<Cloth>,
    private val addOutfitService: AddOutfitService = AddOutfitService(),
): Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_outfit, container, false)
        val uploadImage = view.findViewById<Button>(R.id.uploadImage)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val outfitTitle = view.findViewById<EditText>(R.id.outfitTitle)
        val outfitInfo = view.findViewById<EditText>(R.id.outfitInfo)
        val saveOutfitButton = view.findViewById<Button>(R.id.saveOutfit)
        val clothesContainer = view.findViewById<RecyclerView>(R.id.clothesContainer)

        addOutfitService.setupClothesRecyclerView(clothes, view, requireActivity())

        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                addOutfitService.setImage(result.uriContent, view, NotificationHelper(requireContext()))
            } else {
                addOutfitService.setImage(null, view, NotificationHelper(requireContext()))
            }
        }

        val mainScreenActivity = requireActivity() as MainScreenActivity


        addOutfitService.setupCategoryRecyclerView(view, requireContext())
        addOutfitService.setupSeasonRecyclerView(view, requireContext())

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        uploadImage.setOnClickListener {
            addOutfitService.startCrop(cropImage)
        }

        saveOutfitButton.setOnClickListener {
            val title = outfitTitle.text.toString().trim()
            val info = outfitInfo.text.toString().trim()
            addOutfitService.saveOutfit(title, info, clothes,  NotificationHelper(requireContext()))
            mainScreenActivity.replaceFragment(mainScreenActivity.outfitsFragment, R.id.outfitsPage)
        }

        return view
    }
}
