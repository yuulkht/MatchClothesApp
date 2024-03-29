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
import ru.hse.termpaper.model.entity.Journey

class JourneyAdapter(
    private val journeys: List<Journey>,
    private val listener: OnItemClickListener? = null
) : RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journey_trash, parent, false)
        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val journey = journeys[position]
        holder.bind(journey)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(journey)
        }
        holder.deleteButton.setOnClickListener {
            listener?.onDeleteClick(journey)
        }
    }

    override fun getItemCount(): Int {
        return journeys.size
    }

    interface OnItemClickListener {
        fun onItemClick(journey: Journey)
        fun onDeleteClick(journey: Journey)
    }

    class JourneyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journeyName: TextView = itemView.findViewById(R.id.journeyName)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteJourney)

        fun bind(journey: Journey) {
            journeyName.text = journey.title
        }

    }
}
