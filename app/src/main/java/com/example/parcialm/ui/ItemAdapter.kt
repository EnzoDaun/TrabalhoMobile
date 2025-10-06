// [RF004] Adapter de Itens
package com.example.parcialm.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialm.databinding.ItemCategoryHeaderBinding
import com.example.parcialm.databinding.ItemHeaderBinding
import com.example.parcialm.databinding.ItemShoppingItemBinding
import com.example.parcialm.model.Category
import com.example.parcialm.model.Item

class ItemAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onEditClick: (Item) -> Unit,
    private val onDeleteClick: (Item) -> Unit
) : ListAdapter<ItemAdapter.ItemDisplay, RecyclerView.ViewHolder>(DiffCallback()) {

    sealed class ItemDisplay {
        data class Header(val title: String) : ItemDisplay()
        data class CategoryHeader(val category: String) : ItemDisplay()
        data class ItemData(val item: Item) : ItemDisplay()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CATEGORY_HEADER = 1
        private const val TYPE_ITEM = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemDisplay.Header -> TYPE_HEADER
            is ItemDisplay.CategoryHeader -> TYPE_CATEGORY_HEADER
            is ItemDisplay.ItemData -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_CATEGORY_HEADER -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CategoryHeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ItemDisplay.Header -> (holder as HeaderViewHolder).bind(item.title)
            is ItemDisplay.CategoryHeader -> (holder as CategoryHeaderViewHolder).bind(item.category)
            is ItemDisplay.ItemData -> (holder as ItemViewHolder).bind(item.item)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvHeader.text = title
        }
    }

    inner class CategoryHeaderViewHolder(private val binding: ItemCategoryHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            val categoryEnum = Category.fromDisplayName(category)
            binding.tvCategory.text = "${categoryEnum.emoji} ${categoryEnum.displayName}"
        }
    }

    inner class ItemViewHolder(private val binding: ItemShoppingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.cbPurchased.isChecked = item.purchased
            binding.tvName.text = item.name
            binding.tvQuantity.text = "${item.quantity} ${item.unit}"

            val categoryEnum = Category.fromDisplayName(item.category)
            binding.tvCategoryEmoji.text = categoryEnum.emoji

            if (item.purchased) {
                binding.tvName.paintFlags = binding.tvName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvName.alpha = 0.6f
                binding.tvQuantity.alpha = 0.6f
                binding.tvCategoryEmoji.alpha = 0.6f
            } else {
                binding.tvName.paintFlags = binding.tvName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvName.alpha = 1.0f
                binding.tvQuantity.alpha = 1.0f
                binding.tvCategoryEmoji.alpha = 1.0f
            }

            binding.cbPurchased.setOnClickListener { onItemClick(item) }
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemDisplay>() {
        override fun areItemsTheSame(oldItem: ItemDisplay, newItem: ItemDisplay): Boolean {
            return when {
                oldItem is ItemDisplay.Header && newItem is ItemDisplay.Header -> oldItem.title == newItem.title
                oldItem is ItemDisplay.CategoryHeader && newItem is ItemDisplay.CategoryHeader -> oldItem.category == newItem.category
                oldItem is ItemDisplay.ItemData && newItem is ItemDisplay.ItemData -> oldItem.item.id == newItem.item.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ItemDisplay, newItem: ItemDisplay): Boolean {
            return oldItem == newItem
        }
    }
}
