package ru.hse.termpaper.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory

class ClothCategoryAdapter(
    private val categories: List<ClothCategory>,
) : RecyclerView.Adapter<ClothCategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }


    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: ClothCategory) {
            categoryName.text = category.title
        }
    }
}
