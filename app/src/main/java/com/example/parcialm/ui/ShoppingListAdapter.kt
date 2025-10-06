// [RF003] Adapter de Listas
package com.example.parcialm.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialm.R
import com.example.parcialm.databinding.ItemShoppingListBinding
import com.example.parcialm.model.ShoppingList

class ShoppingListAdapter(
    private val onItemClick: (ShoppingList) -> Unit,
    private val onEditClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemShoppingListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ShoppingList) {
            binding.tvTitle.text = list.title

            if (list.imageUri != null) {
                try {
                    binding.ivImage.setImageURI(Uri.parse(list.imageUri))
                } catch (e: Exception) {
                    binding.ivImage.setImageResource(R.drawable.ic_placeholder)
                }
            } else {
                binding.ivImage.setImageResource(R.drawable.ic_placeholder)
            }

            binding.root.setOnClickListener { onItemClick(list) }
            binding.btnEdit.setOnClickListener { onEditClick(list) }
            binding.btnDelete.setOnClickListener { onDeleteClick(list) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem == newItem
        }
    }
}

