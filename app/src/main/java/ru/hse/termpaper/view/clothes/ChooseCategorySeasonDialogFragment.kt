package ru.hse.termpaper.view.clothes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.model.repository.ClothSeasonRepository
import ru.hse.termpaper.view.adapters.CategoryButtonAdapter
import ru.hse.termpaper.view.adapters.SeasonButtonAdapter
import ru.hse.termpaper.viewmodel.ChooseCategorySeasonService

class ChooseCategorySeasonDialogFragment (
    private val chooseCategorySeasonService: ChooseCategorySeasonService = ChooseCategorySeasonService(),
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_choose_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resetButton = view.findViewById<Button>(R.id.resetButton)

        chooseCategorySeasonService.setupCategoryRecyclerView(view, requireContext(), parentFragment, this)
        chooseCategorySeasonService.setupSeasonRecyclerView(view, requireContext(), parentFragment, this)

        resetButton.setOnClickListener {
            val parent = parentFragment as? ClothesFragment
            parent?.clothesScreenService?.updateClothes(parent.searchEditText, mutableListOf(), true)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
