package ru.hse.termpaper.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ClothesFragment(private val clothesViewModel: ClothesViewModel = ClothesViewModel()) : Fragment() {

    private lateinit var adapter: ClothesAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clothesList: MutableList<Cloth>
    private var filteredList = mutableListOf<Cloth>()
    private var currentSearchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothes, container, false)
        val addButton = view.findViewById<LinearLayout>(R.id.addButton)

        addButton.setOnClickListener {
            val dialogFragment = AddItemDialogFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "AddItemDialog")
        }

        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)
        searchEditText = view.findViewById(R.id.searchEditText)

        val mainScreenActivity = requireActivity() as MainScreenActivity

        adapter = ClothesAdapter(filteredList, object : ClothesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chosenCloth = filteredList[position]
                mainScreenActivity.replaceFragment(ClothCardFragment(chosenCloth), R.id.clothesPage)
            }
        })

        clothesContainer.adapter = adapter

        clothesViewModel.getClothesForCurrentUser { clothes ->
            clothesList = clothes.toMutableList()
            updateFilteredList(currentSearchText)
            adapter.notifyDataSetChanged()
        }

        searchEditText.setText(currentSearchText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                updateFilteredList(searchText)
            }
        })

        return view
    }

    private fun updateFilteredList(searchText: String) {
        currentSearchText = searchText
        filteredList = clothesViewModel.filterClothesByTitle(clothesList, searchText).toMutableList()
        adapter.updateItems(filteredList)
    }
}





