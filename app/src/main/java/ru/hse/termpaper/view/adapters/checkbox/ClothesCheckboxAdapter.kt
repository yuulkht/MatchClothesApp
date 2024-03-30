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
import ru.hse.termpaper.model.entity.Cloth
import ru.hse.termpaper.viewmodel.clothes.ClothesModelService

class ClothesCheckboxAdapter(

    private var clothesList: List<Cloth>,
    private val listener: OnCheckboxClickListener? = null,
    private val clothesViewModel: ClothesModelService = ClothesModelService()
) : RecyclerView.Adapter<ClothesCheckboxAdapter.ClothesCheckboxViewHolder>() {

    inner class ClothesCheckboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clothImageView: ImageView = itemView.findViewById(R.id.itemImage)
        val clothTitleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val checkbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)

        fun bind(cloth: Cloth) {
            clothTitleTextView.text = cloth.title
            clothesViewModel.getImage(cloth) { imageURL ->
                if (imageURL != null) {
                    Picasso.get().load(imageURL).into(clothImageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesCheckboxViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wardrobe_with_checkbox, parent, false)
        return ClothesCheckboxViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClothesCheckboxViewHolder, position: Int) {
        val currentItem = clothesList[position]
        holder.bind(currentItem)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener?.onCheckboxClicked(position, isChecked)
        }
    }

    fun updateItems(newClothesList: List<Cloth>) {
        clothesList = newClothesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = clothesList.size

    interface OnCheckboxClickListener {
        fun onCheckboxClicked(position: Int, isChecked: Boolean)
    }
}