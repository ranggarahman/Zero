package com.example.zero.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zero.R
import com.example.zero.data.MaterialListItem

class MaterialAdapter(
    private val materialList: List<MaterialListItem>,
    private val recyclerView: RecyclerView
)
    : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_material, parent, false)
        return MaterialViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materialList[position]
        holder.titleMaterial.text = material.title
        holder.descMaterial.text = material.desc
        Glide.with(holder.itemView.context)
            .load(material.img)
            .into(holder.imgMaterial)

        holder.btnMaterial.setOnClickListener {
            material.id.let { id ->
                onItemClickCallback.onItemClicked(id) }
        }

        // Set chevron visibility based on position
        when (position) {
            0 -> {
                // First item
                holder.leftChevron.visibility = View.GONE
                holder.rightChevron.visibility = View.VISIBLE
            }
            materialList.size - 1 -> {
                // Last item
                holder.leftChevron.visibility = View.VISIBLE
                holder.rightChevron.visibility = View.GONE
            }
            else -> {
                // Default case (any other item)
                holder.leftChevron.visibility = View.VISIBLE
                holder.rightChevron.visibility = View.VISIBLE
            }
        }

        // Chevron click listeners
        holder.leftChevron.setOnClickListener {
            if (position > 0) {
                recyclerView.smoothScrollToPosition(position - 1)
            }
        }

        holder.rightChevron.setOnClickListener {
            if (position < materialList.size - 1) {
                recyclerView.smoothScrollToPosition(position + 1)
            }
        }

    }

    override fun getItemCount() = materialList.size

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMaterial: ImageView = itemView.findViewById(R.id.iv_material_image)
        val titleMaterial: TextView = itemView.findViewById(R.id.tv_material_title)
        val descMaterial: TextView = itemView.findViewById(R.id.tv_material_desc)
        val btnMaterial: Button = itemView.findViewById(R.id.btn_material_navigate)

        val leftChevron: ImageView = itemView.findViewById(R.id.chevron_material_left)
        val rightChevron: ImageView = itemView.findViewById(R.id.chevron_material_right)

    }

    interface OnItemClickCallback{
        fun onItemClicked(id: Int)
    }

}