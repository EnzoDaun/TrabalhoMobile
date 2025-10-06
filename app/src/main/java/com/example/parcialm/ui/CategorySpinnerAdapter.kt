// [RF004] Adapter personalizado para Spinner de categorias com emojis
package com.example.parcialm.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.parcialm.R
import com.example.parcialm.model.Category

class CategorySpinnerAdapter(
    private val context: Context,
    private val categories: Array<Category>
) : BaseAdapter() {

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Category = categories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent, android.R.layout.simple_spinner_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup?, resource: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        val category = categories[position]
        textView.text = "${category.emoji} ${category.displayName}"

        return view
    }
}
