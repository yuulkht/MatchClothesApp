package ru.hse.termpaper.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.ClothCategory

class ClothCategoryButtonAdapter(
    private val categories: List<ClothCategory>,
    private val listener: OnItemClickListener? = null
) : RecyclerView.Adapter<ClothCategoryButtonAdapter.CategoryButtonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_trash, parent, false)
        return CategoryButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryButtonViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(category)
            holder.changeColor()
        }
        holder.deleteButton.setOnClickListener {
            listener?.onDeleteClick(category)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    interface OnItemClickListener {
        fun onItemClick(category: ClothCategory)
        fun onDeleteClick(category: ClothCategory)
    }

    class CategoryButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val categoryBackground: LinearLayout = itemView.findViewById(R.id.categoryBackground)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteCategory)

        fun bind(category: ClothCategory) {
            categoryName.text = category.title
        }

        fun changeColor() {
            categoryBackground.setBackgroundColor(Color.parseColor("#798E9C"))
            categoryName.setTextColor(Color.WHITE)
        }

    }
}
