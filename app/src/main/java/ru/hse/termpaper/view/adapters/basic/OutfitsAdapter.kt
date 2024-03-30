package ru.hse.termpaper.view.adapters.basic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class OutfitsAdapter(
    private var outfitsList: List<Outfit>,
    private val itemClickListener: OnItemClickListener? = null,
    private val outfitsViewModel: OutfitsModelService = OutfitsModelService()
) : RecyclerView.Adapter<OutfitsAdapter.OutfitsViewHolder>() {

    inner class OutfitsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val outfitImageView: ImageView = itemView.findViewById(R.id.itemImage)
        val outfitTitleTextView: TextView = itemView.findViewById(R.id.itemTitle)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wardrobe, parent, false)
        return OutfitsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutfitsViewHolder, position: Int) {
        val currentItem = outfitsList[position]
        outfitsViewModel.getImage(currentItem) { imageURL ->
            if (imageURL != null) {
                Picasso.get().load(imageURL).into(holder.outfitImageView)
            }
        }
        holder.outfitTitleTextView.text = currentItem.title
    }

    fun updateItems(newOutfitsList: List<Outfit>) {
        outfitsList = newOutfitsList
        notifyDataSetChanged()
    }

    override fun getItemCount() = outfitsList.size

    fun getItem(position: Int): Outfit? {
        if (position in outfitsList.indices) {
            return outfitsList[position]
        }
        return null
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}