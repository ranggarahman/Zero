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

class MaterialAdapter(private val materialList: List<MaterialListItem>)
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
        Glide.with(holder.itemView.context)
            .load(material.img)
            .into(holder.imgMaterial)

        holder.btnMaterial.setOnClickListener {
            material.id.let { id ->
                onItemClickCallback.onItemClicked(id) }
        }

    }

    override fun getItemCount() = materialList.size

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMaterial: ImageView = itemView.findViewById(R.id.iv_material_image)
        val titleMaterial: TextView = itemView.findViewById(R.id.tv_material_title)
        val btnMaterial: Button = itemView.findViewById(R.id.btn_material_navigate)
    }

    interface OnItemClickCallback{
        fun onItemClicked(id: Int)
    }

}