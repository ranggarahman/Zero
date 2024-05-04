package com.example.zero.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zero.R
import com.example.zero.data.Avatar
import com.example.zero.data.Badges
import com.example.zero.databinding.ItemAvatarSelectBinding

class AvatarSelectAdapter(private val avatarList: List<Avatar>) :
    RecyclerView.Adapter<AvatarSelectAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvatarSelectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val avatarImages = avatarList[position]
        holder.bind(avatarImages, position)
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }

    inner class ViewHolder(private val binding: ItemAvatarSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(avatarItem: Avatar, position: Int) {
            binding.apply {
                avatarImg.setImageResource(avatarItem.imgInt)
                avatarTitle.text = "Avatar ${position + 1}"
                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(avatarItem)
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