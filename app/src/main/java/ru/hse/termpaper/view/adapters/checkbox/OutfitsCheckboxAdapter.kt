package ru.hse.termpaper.view.adapters.checkbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Outfit
import ru.hse.termpaper.viewmodel.outfits.OutfitsModelService

class OutfitsCheckboxAdapter(

    private var outfitsList: List<Outfit>,
    private val listener: OnCheckboxClickListener? = null,
    private val outfitsViewModel: OutfitsModelService = OutfitsModelService()
) : RecyclerView.Adapter<OutfitsCheckboxAdapter.OutfitsCheckboxViewHolder>() {

    inner class OutfitsCheckboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val outfitImageView: ImageView = itemView.findViewById(R.id.itemImage)
        val outfitTitleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val checkbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)

        fun bind(outfit: Outfit) {
            outfitTitleTextView.text = outfit.title
            outfitsViewModel.getImage(outfit) { imageURL ->
                if (imageURL != null) {
                    Picasso.get().load(imageURL).into(outfitImageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitsCheckboxViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wardrobe_with_checkbox, parent, false)
        return OutfitsCheckboxViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutfitsCheckboxViewHolder, position: Int) {
        val currentItem = outfitsList[position]
        holder.bind(currentItem)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener?.onCheckboxClicked(position, isChecked)
        }
    }

    fun updateItems(newOutfitsList: List<Outfit>) {
        outfitsList = newOutfitsList
        notifyDataSetChanged()
    }

    override fun getItemCount() = outfitsList.size

    interface OnCheckboxClickListener {
        fun onCheckboxClicked(position: Int, isChecked: Boolean)
    }
}