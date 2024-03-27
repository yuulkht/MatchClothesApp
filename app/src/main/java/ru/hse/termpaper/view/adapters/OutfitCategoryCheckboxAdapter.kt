package ru.hse.termpaper.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.OutfitCategory
import android.widget.CheckBox

class OutfitCategoryCheckboxAdapter(
    private val categories: List<OutfitCategory>,
    private val listener: OnCheckboxClickListener? = null
) : RecyclerView.Adapter<OutfitCategoryCheckboxAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_with_checkbox, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener?.onCheckboxClicked(position, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    interface OnCheckboxClickListener {
        fun onCheckboxClicked(position: Int, isChecked: Boolean)
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val checkbox: CheckBox = itemView.findViewById(R.id.categoryCheckbox)

        fun bind(category: OutfitCategory) {
            categoryName.text = category.title
        }
    }
}
