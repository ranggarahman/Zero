package com.example.zero.ui.achievement.badges

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.databinding.ItemBadgesBinding

class BadgesAdapter(private val badgesList: List<Badges>) :
    RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Badges)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBadgesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val badgesData = badgesList[position]
        holder.bind(badgesData)
    }

    override fun getItemCount(): Int {
        return badgesList.size
    }

    inner class ViewHolder(private val binding: ItemBadgesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(badgesItem: Badges) {
            binding.apply {
                badgeTitle.text = badgesItem.title
                // Load the image using Coil
                badgeLogo.load(badgesItem.imgUrl) {
                    placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading (optional)
                    error(R.drawable.ic_launcher_background) // Image to show if loading fails (optional)
                    // You can add transformations if needed, e.g., CircleCropTransformation() for circular images
                    // transformations(CircleCropTransformation())
                }

                if (!badgesItem.isUnlocked) {
                    badgeLogo.alpha = 0.5f // Set the alpha value to 0.5 (or any other value you prefer)
                } else {
                    badgeLogo.alpha = 1.0f // Fully opaque
                }

                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(badgesItem)
                    animateItemView()
                }
            }

        }

        private fun animateItemView() {
            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_anim)
            itemView.startAnimation(animation)
        }
    }
}
