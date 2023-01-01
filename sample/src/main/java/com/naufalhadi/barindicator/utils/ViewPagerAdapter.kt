package com.naufalhadi.barindicatorsample.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.naufalhadi.barindicatorsample.R

class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.ImageViewHolder>() {

    private val eventList = listOf("0", "1", "2")

    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder = ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_view_pager_demo, parent, false))

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        (holder.view as TextView).also {
            it.text = "Page " + eventList.get(position)

            val backgroundResId = if (position % 2 == 0) R.color.aqua else R.color.red
            it.setBackgroundColor(ContextCompat.getColor(it.context, backgroundResId))
        }
    }

    override fun getItemCount(): Int = eventList.size
}