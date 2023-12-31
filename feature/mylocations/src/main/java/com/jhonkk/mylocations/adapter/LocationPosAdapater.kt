package com.jhonkk.mylocations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jhonkk.common.model.MyLocation
import com.jhonkk.mylocations.databinding.ItemPositionBinding

class LocationPosAdapater: ListAdapter<MyLocation, LocationPosAdapater.LocationPosHolder>(DiffUtilCallback()) {

    inner class LocationPosHolder(val binding: ItemPositionBinding): ViewHolder(binding.root) {
        fun bind(myLocation: MyLocation) {
            binding.tvDatetime.text = "${myLocation.datetime?.toDate()?.toLocaleString()}"
            binding.tvCoordinates.text = "Latitude: ${myLocation.latitude} - Longitude: ${myLocation.longitude}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationPosHolder {
        return LocationPosHolder(ItemPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LocationPosHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private class DiffUtilCallback: DiffUtil.ItemCallback<MyLocation?>() {
    override fun areItemsTheSame(oldItem: MyLocation, newItem: MyLocation): Boolean
            = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MyLocation, newItem: MyLocation): Boolean
            = oldItem == newItem

}