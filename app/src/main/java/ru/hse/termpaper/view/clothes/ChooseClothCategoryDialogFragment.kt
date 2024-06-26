package ru.hse.termpaper.view.clothes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import ru.hse.termpaper.R
import ru.hse.termpaper.viewmodel.clothes.ChooseClothCategoryService

class ChooseClothCategoryDialogFragment (
    private val chooseCategorySeasonService: ChooseClothCategoryService = ChooseClothCategoryService(),
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_choose_cloth_category, container, false)

        val resetButton = view.findViewById<Button>(R.id.resetButton)

        chooseCategorySeasonService.setupCategoryRecyclerView(view, requireContext(), parentFragment, this)
        chooseCategorySeasonService.setupSeasonRecyclerView(view, requireContext(), parentFragment, this)

        resetButton.setOnClickListener {
            val parent = parentFragment as? ClothesFragment
            parent?.clothesScreenService?.updateClothes(parent.searchEditText, mutableListOf(), true)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
