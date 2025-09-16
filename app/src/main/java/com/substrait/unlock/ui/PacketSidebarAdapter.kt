
package com.substrait.unlock.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.substrait.unlock.data.PacketContent
import com.substrait.unlock.databinding.ItemPacketContentBinding

class PacketSidebarAdapter(
    private val onItemClicked: (PacketContent) -> Unit
) : ListAdapter<PacketContent, PacketSidebarAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPacketContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClicked(item) }
    }

    class ViewHolder(private val binding: ItemPacketContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PacketContent) {
            binding.title.text = item.title
            binding.context.text = item.context
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PacketContent>() {
        override fun areItemsTheSame(oldItem: PacketContent, newItem: PacketContent): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PacketContent, newItem: PacketContent): Boolean {
            return oldItem == newItem
        }
    }
}