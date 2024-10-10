package com.dicoding.submissionfundamental.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submissionfundamental.databinding.ItemUpcomingEventBinding
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import java.text.SimpleDateFormat
import java.util.*

class UpcomingEventsAdapter(private val onItemClick: (ListEventsItem) -> Unit) : ListAdapter<ListEventsItem, UpcomingEventsAdapter.ViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    class ViewHolder(private val binding: ItemUpcomingEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.textEventName.text = event.name


            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

            try {
                val date = inputFormat.parse(event.beginTime)
                val formattedDate = date?.let { outputFormat.format(it) } ?: "Date not available"
                binding.textEventDate.text = formattedDate
                binding.textEventDate.visibility = View.VISIBLE
            } catch (e: Exception) {

                binding.textEventDate.visibility = View.VISIBLE
            }

            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imageEvent)
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<ListEventsItem>() {
        override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
        }
    }
}