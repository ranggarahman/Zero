package com.example.zero.ui.dashboard.reads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zero.R
import com.example.zero.data.ReadsItem

class ReadsAdapter(private val readsList: List<ReadsItem>)
    : RecyclerView.Adapter<ReadsAdapter.MaterialViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_reads_material, parent, false)
        return MaterialViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = readsList[position]
        holder.subtitleReads.text = material.title
        holder.contentReads.text = material.content
    }

    override fun getItemCount() = readsList.size

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subtitleReads: TextView = itemView.findViewById(R.id.item_reads_subtitle)
        val contentReads: TextView = itemView.findViewById(R.id.item_reads_content)
    }

    interface OnItemClickCallback{
        fun onItemClicked(id: Int)
    }

}