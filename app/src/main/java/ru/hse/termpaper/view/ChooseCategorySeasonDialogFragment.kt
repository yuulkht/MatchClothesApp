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
import ru.hse.termpaper.view.CategoryButtonAdapter
import ru.hse.termpaper.view.ClothesFragment
import ru.hse.termpaper.view.SeasonButtonAdapter
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ChooseCategorySeasonDialogFragment : DialogFragment() {

    private lateinit var categoryAdapter: CategoryButtonAdapter
    private lateinit var seasonAdapter: SeasonButtonAdapter

    private val clothesViewModel = ClothesViewModel()
    private val clothesCategoryRepository = ClothCategoryRepository()
    private val clothSeasonRepository = ClothSeasonRepository()

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

        // Настройка RecyclerView для категорий
        clothesCategoryRepository.getClothCategories { categories ->
            val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryButtonRecyclerView)
            categoryRecyclerView.layoutManager = LinearLayoutManager(context)
            categoryAdapter = CategoryButtonAdapter(categories, object : CategoryButtonAdapter.OnItemClickListener {
                override fun onItemClick(category: ClothCategory) {
                    clothesCategoryRepository.getClothesFromCategory(category) { _, clothes ->
                        (parentFragment as? ClothesFragment)?.updateClothes(clothes)
                        dismiss()
                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter
        }

        // Настройка RecyclerView для сезонов
        val seasons = clothSeasonRepository.getSeasons()
        val seasonRecyclerView: RecyclerView = view.findViewById(R.id.seasonButtonRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        seasonAdapter = SeasonButtonAdapter(seasons, object : SeasonButtonAdapter.OnItemClickListener {
            override fun onItemClick(season: Season) {
                clothSeasonRepository.getClothesFromSeason(season) { _, clothes ->
                    (parentFragment as? ClothesFragment)?.updateClothes(clothes)
                    dismiss()
                }
            }
        })
        seasonRecyclerView.adapter = seasonAdapter

        resetButton.setOnClickListener {
            (parentFragment as? ClothesFragment)?.updateClothes(mutableListOf())
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
