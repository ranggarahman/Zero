package com.example.zero.ui.achievement.badges

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
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
                Glide.with(binding.root.context)
                    .load(badgesItem.imgUrl) // Assuming imageUrl is the field in your Badges data class containing the image URL
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading (optional)
                    .error(R.drawable.ic_launcher_background) // Image to show if loading fails (optional)
                    .into(binding.badgeLogo)
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
