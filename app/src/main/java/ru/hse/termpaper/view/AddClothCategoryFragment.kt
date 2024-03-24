package ru.hse.termpaper.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.viewmodel.ClothesViewModel

class AddClothCategoryFragment(
    private val clothesViewModel: ClothesViewModel = ClothesViewModel(),
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository()
) : Fragment() {

    private lateinit var adapter: ClothesCheckboxAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clothesList: MutableList<Cloth>
    private var currentSearchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_cloth_category, container, false)
        val saveCategoryButton = view.findViewById<Button>(R.id.saveClothCategory)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val categoryTitle = view.findViewById<EditText>(R.id.clothCategoryTitle)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        val clothesInCategory: MutableList<Cloth> = mutableListOf()

        clothesViewModel.getClothesForCurrentUser {
            clothesList = it

            adapter = ClothesCheckboxAdapter(
                clothesList,
                object : ClothesCheckboxAdapter.OnCheckboxClickListener {
                    override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                        if (isChecked) {
                            clothesInCategory.add(clothesList[position])
                        } else {
                            clothesInCategory.remove(clothesList[position])
                        }
                    }
                })

            val clothesCheckboxContainer: RecyclerView =
                view.findViewById(R.id.clothesCheckboxContainer)

            clothesCheckboxContainer.adapter = adapter

        }

        saveCategoryButton.setOnClickListener {
            val title = categoryTitle.text.toString().trim()
            clothCategoryRepository.saveClothCategory(ClothCategory("", "", title)) {success, message, category ->
                if (success) {
                    for (cloth: Cloth in clothesInCategory){
                        clothCategoryRepository.addClothToCategory(cloth, category) {success, message ->
                            if(!success) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
                }

            }
        }
        return view
    }
}