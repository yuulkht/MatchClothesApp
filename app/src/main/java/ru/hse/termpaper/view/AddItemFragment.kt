import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.model.entity.ClothCategory
import ru.hse.termpaper.model.entity.Season
import ru.hse.termpaper.model.repository.ClothCategoryRepository
import ru.hse.termpaper.model.repository.ClothSeasonRepository
import ru.hse.termpaper.model.repository.ClothesRepository
import ru.hse.termpaper.view.CategoryCheckboxAdapter
import ru.hse.termpaper.view.MainScreenActivity
import ru.hse.termpaper.view.SeasonCheckboxAdapter

class AddItemFragment (
    private var selectedImageUri: Uri? = null,
    // потом использовать модель
    private val clothesRepository: ClothesRepository = ClothesRepository(),
    private val clothCategoryRepository: ClothCategoryRepository = ClothCategoryRepository(),
    private val clothSeasonRepository: ClothSeasonRepository = ClothSeasonRepository(),
    private var categories:MutableList<ClothCategory> = mutableListOf(),
    private var seasons:MutableList<Season> = mutableListOf(),
): Fragment(){

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                view?.findViewById<ImageView>(R.id.clothImage)?.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)
        val uploadImage = view.findViewById<Button>(R.id.uploadImage)
        val backLink = view.findViewById<ImageView>(R.id.backButton)
        val clothTitle = view.findViewById<EditText>(R.id.clothTitle)
        val clothInfo = view.findViewById<EditText>(R.id.clothInfo)
        val saveClothButton = view.findViewById<Button>(R.id.saveItem)

        val clothSeasons:MutableList<Season> = mutableListOf()
        val clothCategories:MutableList<ClothCategory> = mutableListOf()

        // Настройка RecyclerView для категорий
        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.categoryCheckboxRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        clothCategoryRepository.getClothCategories { categories ->
            this.categories = categories // сохраняем полученные категории
            val categoryAdapter = CategoryCheckboxAdapter(categories, object : CategoryCheckboxAdapter.OnCheckboxClickListener{
                override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                    val chosenCategory = categories[position]
                    if (isChecked) {
                        clothCategories.add(chosenCategory)
                    } else {
                        clothCategories.remove(chosenCategory)
                    }
                }
            })
            categoryRecyclerView.adapter = categoryAdapter // устанавливаем адаптер для RecyclerView
        }

        val seasonRecyclerView = view.findViewById<RecyclerView>(R.id.seasonCheckboxRecyclerView)
        seasonRecyclerView.layoutManager = LinearLayoutManager(context)
        seasons = clothSeasonRepository.getSeasons()
        val seasonAdapter = SeasonCheckboxAdapter(seasons, object: SeasonCheckboxAdapter.OnCheckboxClickListener{
            override fun onCheckboxClicked(position: Int, isChecked: Boolean) {
                val chosenSeason= seasons[position]
                if (isChecked) {
                    clothSeasons.add(chosenSeason)
                } else {
                    clothSeasons.remove(chosenSeason)
                }
            }
        })
        seasonRecyclerView.adapter = seasonAdapter

        val mainScreenActivity = requireActivity() as MainScreenActivity

        backLink.setOnClickListener {
            mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
        }

        uploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        saveClothButton.setOnClickListener {
            val title = clothTitle.text.toString().trim()
            val info = clothInfo.text.toString().trim()
            val imageUri = selectedImageUri

            imageUri?.let{
                clothesRepository.saveCloth(Cloth("", "", title,"", info), imageUri) { success, message, cloth ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    for (category: ClothCategory in clothCategories) {
                        clothCategoryRepository.addClothToCategory(cloth, category) {success,message ->
                            if (!success) {
                                Toast.makeText(requireContext(), "не удалось добавить вещь в категорию", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    for (season: Season in clothSeasons) {
                        clothSeasonRepository.addClothToSeason(cloth, season) {success,message ->
                            if (!success) {
                                Toast.makeText(requireContext(), "не удалось добавить вещь в сезон", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    mainScreenActivity.replaceFragment(mainScreenActivity.clothesFragment, R.id.clothesPage)
                }
            } ?: run {
                Toast.makeText(requireContext(), "фотография не загружена", Toast.LENGTH_LONG).show()
            }


        }

        return view
    }
}
