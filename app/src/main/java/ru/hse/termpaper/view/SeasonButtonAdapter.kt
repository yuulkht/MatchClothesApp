package ru.hse.termpaper.view

import android.graphics.Color
import android.widget.CheckBox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.termpaper.R
import ru.hse.termpaper.model.entity.Season

class SeasonButtonAdapter (
    private val seasons: List<Season>,
    private val listener: OnItemClickListener? = null
) : RecyclerView.Adapter<SeasonButtonAdapter.SeasonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.bind(season)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(season)
            holder.changeColor()
        }
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

    interface OnItemClickListener {
        fun onItemClick(season: Season)
    }

    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val seasonName: TextView = itemView.findViewById(R.id.categoryName)
        private val seasonBackground: LinearLayout = itemView.findViewById(R.id.categoryBackground)

        fun bind(season: Season) {
            seasonName.text = season.toString()
        }

        fun changeColor() {
            seasonBackground.setBackgroundColor(Color.parseColor("#798E9C"))
            seasonName.setTextColor(Color.WHITE)
        }
    }
}
