package ru.hse.termpaper.view.outfits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import ru.hse.termpaper.R
import ru.hse.termpaper.viewmodel.outfits.ChooseOutfitCategoryService

class ChooseOutfitCategoryDialogFragment (
    private val chooseCategorySeasonService: ChooseOutfitCategoryService = ChooseOutfitCategoryService(),
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_choose_outfit_category, container, false)

        val resetButton = view.findViewById<Button>(R.id.resetButton)

        chooseCategorySeasonService.setupCategoryRecyclerView(view, requireContext(), parentFragment, this)
        chooseCategorySeasonService.setupSeasonRecyclerView(view, requireContext(), parentFragment, this)

        resetButton.setOnClickListener {
            val parent = parentFragment as? OutfitsFragment
            parent?.outfitsScreenService?.updateOutfits(parent.searchEditText, mutableListOf(), true)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
