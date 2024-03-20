import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.ClothesViewModel

class ClothesAdapter(
    private var clothesList: List<Cloth>,
    private var clothesViewModel: ClothesViewModel = ClothesViewModel()
) :
    RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>() {

    inner class ClothesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clothImageView: ImageView = itemView.findViewById(R.id.clothImage)
        val clothTitleTextView: TextView = itemView.findViewById(R.id.clothTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cloth, parent, false)
        return ClothesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val currentItem = clothesList[position]
        clothesViewModel.getImage(currentItem) { imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(holder.clothImageView)
            }
        }
        holder.clothTitleTextView.text = currentItem.title
    }

    fun updateItems(newClothesList: List<Cloth>) {
        clothesList = newClothesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = clothesList.size
}
