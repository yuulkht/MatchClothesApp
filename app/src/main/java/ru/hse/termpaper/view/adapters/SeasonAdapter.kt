package ru.hse.termpaper.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Season

class SeasonAdapter (
    private val seasons: List<Season>,
) : RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.bind(season)
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val seasonName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(season: Season) {
            seasonName.text = season.toString()
        }
    }
}
