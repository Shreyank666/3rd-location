package com.cscf.antitheftproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class LocationHistoryAdapter(private val locationHistory: List<LocationData>) :
    RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val locationText: TextView = view.findViewById(R.id.tvHistoryLocation)
        val timestampText: TextView = view.findViewById(R.id.tvHistoryTimestamp)
        val detailsText: TextView = view.findViewById(R.id.tvHistoryDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationData = locationHistory[position]
        
        holder.locationText.text = String.format(
            "📍 %.6f, %.6f",
            locationData.latitude,
            locationData.longitude
        )
        
        val date = locationData.timestamp.toDate()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        holder.timestampText.text = format.format(date)
        
        holder.detailsText.text = String.format(
            "Accuracy: %.1fm | Provider: %s | Speed: %.1f m/s | Altitude: %.1fm",
            locationData.accuracy,
            locationData.provider,
            locationData.speed,
            locationData.altitude
        )
    }

    override fun getItemCount() = locationHistory.size
}