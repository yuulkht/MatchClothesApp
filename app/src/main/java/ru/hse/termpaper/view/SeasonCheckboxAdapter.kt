package ru.hse.termpaper.view

import android.widget.CheckBox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Season

class SeasonCheckboxAdapter (
    private val seasons: List<Season>,
    private val listener: SeasonCheckboxAdapter.OnCheckboxClickListener? = null
) : RecyclerView.Adapter<SeasonCheckboxAdapter.SeasonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_with_checkbox, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.bind(season)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener?.onCheckboxClicked(position, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

    interface OnCheckboxClickListener {
        fun onCheckboxClicked(position: Int, isChecked: Boolean)
    }

    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val seasonName: TextView = itemView.findViewById(R.id.categoryName)
        val checkbox: CheckBox = itemView.findViewById(R.id.categoryCheckbox)

        fun bind(season: Season) {
            seasonName.text = season.toString()
        }
    }
}
