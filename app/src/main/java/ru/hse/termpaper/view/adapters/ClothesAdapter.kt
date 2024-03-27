package ru.hse.termpaper.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ClothesAdapter(
    private var clothesList: List<Cloth>,
    private val itemClickListener: OnItemClickListener? = null,
    private val clothesViewModel: ClothesModelService = ClothesModelService()
) : RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>() {

    inner class ClothesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val clothImageView: ImageView = itemView.findViewById(R.id.itemImage)
        val clothTitleTextView: TextView = itemView.findViewById(R.id.itemTitle)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wardrobe, parent, false)
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

    fun getItem(position: Int): Cloth? {
        if (position in clothesList.indices) {
            return clothesList[position]
        }
        return null
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}