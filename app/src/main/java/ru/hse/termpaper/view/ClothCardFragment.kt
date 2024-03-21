package ru.hse.termpaper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ClothCardFragment (
    private val cloth: Cloth,
    private val clothesViewModel: ClothesViewModel = ClothesViewModel()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cloth_card, container, false)

        // Находим все необходимые элементы интерфейса
        val clothTitleTextView: TextView = view.findViewById(R.id.clothTitle)
        val clothImageView: ImageView = view.findViewById(R.id.clothImage)
        val seasonTextView: TextView = view.findViewById(R.id.seasonText)
        val additionalInfoTextView: TextView = view.findViewById(R.id.additionalInfoText)
        val backLink = view.findViewById<ImageView>(R.id.backButton)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        clothTitleTextView.text = cloth.title
        seasonTextView.text = cloth.season.toString()
        additionalInfoTextView.text = cloth.information
        clothesViewModel.getImage(cloth) {imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(clothImageView)
            }
        }

        return view
    }
}
