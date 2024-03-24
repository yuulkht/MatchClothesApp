package ru.hse.termpaper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.model.repository.ClothSeasonRepository
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ClothCardFragment (
    private val cloth: Cloth,
    private val clothesViewModel: ClothesViewModel = ClothesViewModel(),
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
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
        val additionalInfoTextView: TextView = view.findViewById(R.id.additionalInfoText)
        val backLink = view.findViewById<ImageView>(R.id.backButton)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        clothCategoryRepository.getCategoriesForCloth(cloth) { _, categories ->
            val categoryAdapter = CategoryAdapter(categories)
            categoryRecyclerView.adapter = categoryAdapter
        }

        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)

        clothSeasonRepository.getSeasonsForCloth(cloth) { _, seasons ->
            val seasonAdapter = SeasonAdapter(seasons)
            seasonRecyclerView.adapter = seasonAdapter
        }

        clothTitleTextView.text = cloth.title
        additionalInfoTextView.text = cloth.information
        clothesViewModel.getImage(cloth) {imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(clothImageView)
            }
        }

        return view
    }
}
