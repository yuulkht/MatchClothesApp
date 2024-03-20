import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ClothesFragment(private val clothesViewModel: ClothesViewModel = ClothesViewModel()) : Fragment() {

    private lateinit var adapter: ClothesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clothes, container, false)

        val clothesContainer: RecyclerView = view.findViewById(R.id.clothesContainer)


        adapter = ClothesAdapter(emptyList())

        clothesContainer.adapter = adapter

        clothesViewModel.getClothesForCurrentUser{listClothes: MutableList<Cloth> ->
            adapter.updateItems(listClothes)
        }

        return view
    }
}
