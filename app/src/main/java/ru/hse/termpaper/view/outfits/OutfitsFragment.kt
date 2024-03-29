package ru.hse.termpaper.view.outfits

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
import ru.hse.termpaper.view.clothes.AddClothDialogFragment
import ru.hse.termpaper.viewmodel.outfits.OutfitsScreenService

class OutfitsFragment(
    val outfitsScreenService: OutfitsScreenService = OutfitsScreenService(),
    var searchEditText: EditText? = null
) : Fragment() {

    private var currentSearchText: String = ""
    private val dialogFragment: AddOutfitDialogFragment = AddOutfitDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        val addButton = view.findViewById<LinearLayout>(R.id.addButton)
        val chooseCategoryImageView = view.findViewById<ImageView>(R.id.chooseOutfitCategory)

        outfitsScreenService.setupOutfitsRecyclerView(view, requireActivity())
        outfitsScreenService.loadOutfits("")

        searchEditText = view.findViewById(R.id.searchEditText)

        searchEditText?.setText(currentSearchText)
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                outfitsScreenService.updateFilteredList(searchText)
            }
        })

        addButton.setOnClickListener {
            dialogFragment.show(requireActivity().supportFragmentManager, "AddOutfitDialog")
        }

        chooseCategoryImageView.setOnClickListener {
            val dialogFragment = ChooseOutfitCategoryDialogFragment()
            dialogFragment.show(getChildFragmentManager(), "ChooseOutfitCategoryDialog")
        }


        return view
    }
}