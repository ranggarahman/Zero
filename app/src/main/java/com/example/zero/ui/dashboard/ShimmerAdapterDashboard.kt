package com.example.zero.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zero.R


class ShimmerAdapter(private val itemCount: Int) :
    RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shimmer_dash_material_list, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        // No binding necessary for shimmer effect
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    class ShimmerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}
