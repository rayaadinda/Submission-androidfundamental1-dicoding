package com.dicoding.submissionfundamental.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submissionfundamental.data.local.FavoriteEvent
import com.dicoding.submissionfundamental.databinding.ItemFavoriteEventBinding
import java.text.SimpleDateFormat
import java.util.*

class FavoriteEventsAdapter(
    private val onItemClick: (FavoriteEvent) -> Unit,
    private val onRemoveClick: (FavoriteEvent) -> Unit
) : ListAdapter<FavoriteEvent, FavoriteEventsAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener { onItemClick(event) }
        holder.binding.buttonRemoveFavorite.setOnClickListener { onRemoveClick(event) }
    }

    class ViewHolder(val binding: ItemFavoriteEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEvent) {
            binding.textEventName.text = event.name
            binding.textEventDate.text = event.beginTime
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imageEvent)
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEvent>() {
            override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                return oldItem == newItem
            }
        }
    }
}