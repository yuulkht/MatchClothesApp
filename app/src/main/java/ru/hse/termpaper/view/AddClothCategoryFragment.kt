package ru.hse.termpaper.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.ClothesViewModel

class AddClothCategoryFragment(
    private val clothesViewModel: ClothesViewModel = ClothesViewModel()
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
        return view
    }
}