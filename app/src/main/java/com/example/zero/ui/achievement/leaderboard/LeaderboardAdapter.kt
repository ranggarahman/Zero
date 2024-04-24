package com.example.zero.ui.achievement.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zero.data.LeaderboardItem
import com.example.zero.databinding.ItemLeaderboardBinding
import com.example.zero.ui.achievement.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class LeaderboardAdapter(
    private val leaderboardList: List<LeaderboardItem>
) : RecyclerView.Adapter<LeaderboardAdapter.AchievementViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: LeaderboardItem)
    }

    inner class AchievementViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderboard: LeaderboardItem, position: Int) {
            // Bind data achievement ke elemen-elemen dalam layout item_leaderboard.xml

            binding.apply {
                rankTextView.text = position.plus(1).toString()
                nameTextView.text = leaderboard.username.toString()
                xpTextView.text = leaderboard.userpoint.toString()
                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(leaderboard)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLeaderboardBinding.inflate(inflater, parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = leaderboardList[position]
        holder.bind(achievement, position)
    }

    override fun getItemCount(): Int = leaderboardList.size
}