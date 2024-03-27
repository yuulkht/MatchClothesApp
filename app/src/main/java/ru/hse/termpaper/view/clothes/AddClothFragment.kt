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
import ru.hse.termpaper.view.main.NotificationHelper
import ru.hse.termpaper.view.main.MainScreenActivity
import ru.hse.termpaper.viewmodel.clothes.AddClothService
import com.canhub.cropper.CropImageContract

class AddClothFragment (
    private val addClothViewModel: AddClothService = AddClothService(),
): Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_cloth, container, false)
        val uploadImage = view.findViewById<Button>(R.id.uploadImage)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val clothTitle = view.findViewById<EditText>(R.id.clothTitle)
        val clothInfo = view.findViewById<EditText>(R.id.clothInfo)
        val saveClothButton = view.findViewById<Button>(R.id.saveItem)

        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                addClothViewModel.setImage(result.uriContent, view, NotificationHelper(requireContext()))
            } else {
                addClothViewModel.setImage(null, view, NotificationHelper(requireContext()))
            }
        }

        val mainScreenActivity = requireActivity() as MainScreenActivity


        addClothViewModel.setupCategoryRecyclerView(view, requireContext())
        addClothViewModel.setupSeasonRecyclerView(view, requireContext())

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        uploadImage.setOnClickListener {
            addClothViewModel.startCrop(cropImage)
        }

        saveClothButton.setOnClickListener {
            val title = clothTitle.text.toString().trim()
            val info = clothInfo.text.toString().trim()
            addClothViewModel.saveCloth(title, info,  NotificationHelper(requireContext()))
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        return view
    }
}
