package com.example.zero.ui.dashboard.reads

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
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
        holder.imageView.load(material.imgUrl)
        // Assuming `material` is your data object and `holder` is your ViewHolder
        val contentText = material.content
        // Set the formatted text to the TextView
        holder.contentReads.text =
            Html.fromHtml(contentText, Html.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount() = readsList.size

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subtitleReads: TextView = itemView.findViewById(R.id.item_reads_subtitle)
        val contentReads: TextView = itemView.findViewById(R.id.item_reads_content)
        val imageView: ImageView = itemView.findViewById(R.id.item_reads_image)
    }

    interface OnItemClickCallback{
        fun onItemClicked(id: Int)
    }

}