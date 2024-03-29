package ru.hse.termpaper.view.clothes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ru.hse.termpaper.R
import ru.hse.termpaper.view.outfits.ChooseClothCategoryDialogFragment
import ru.hse.termpaper.viewmodel.clothes.ClothesScreenService

class ClothesFragment(
    val clothesScreenService: ClothesScreenService = ClothesScreenService(),
    var searchEditText: EditText? = null,
    private var currentSearchText: String = "",
    private val dialogFragment: AddClothDialogFragment = AddClothDialogFragment()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothes, container, false)

        val addButton = view.findViewById<LinearLayout>(R.id.addButton)
        val chooseCategoryImageView = view.findViewById<ImageView>(R.id.chooseClothCategory)


        clothesScreenService.setupClothesRecyclerView(view, requireActivity())
        clothesScreenService.loadClothes("")

        searchEditText = view.findViewById(R.id.searchEditText)

        searchEditText?.setText(currentSearchText)
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                clothesScreenService.updateFilteredList(searchText)
            }
        })

        addButton.setOnClickListener {
            dialogFragment.show(requireActivity().supportFragmentManager, "AddItemDialog")
        }

        chooseCategoryImageView.setOnClickListener {
            val dialogFragment = ChooseClothCategoryDialogFragment()
            dialogFragment.show(getChildFragmentManager(), "ChooseClothCategoryDialog")
        }

        return view
    }
}