package com.example.zero.ui.achievement.leaderboard

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
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

    inner class AchievementViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderboard: LeaderboardItem, position: Int) {
            // Bind data achievement ke elemen-elemen dalam layout item_leaderboard.xml
            binding.rankTextView.text = position.plus(1).toString()
            binding.nameTextView.text = leaderboard.username.toString()
            binding.xpTextView.text = leaderboard.userpoint.toString()
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