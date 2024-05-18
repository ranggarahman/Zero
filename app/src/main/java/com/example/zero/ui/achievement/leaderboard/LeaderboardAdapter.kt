package com.example.zero.ui.achievement.leaderboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.zero.data.LeaderboardItem
import com.example.zero.databinding.ItemLeaderboardBinding
import com.example.zero.ui.achievement.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class LeaderboardAdapter(
    private val leaderboardList: List<LeaderboardItem>,
    private val context: Context,
    private val currentUid: String?
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
            val drawableName = "a${leaderboard.avatarId}"

            // Get the resource identifier for the drawable
            val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            // Bind data achievement ke elemen-elemen dalam layout item_leaderboard.xml
            binding.apply {
                itemLeaderboardImage.load(resourceId) {
                    transformations(CircleCropTransformation())
                }
                rankTextView.text = position.plus(1).toString()
                nameTextView.text = leaderboard.username.toString()
                xpTextView.text = leaderboard.userpoint.toString()

                if (leaderboard.uid == currentUid){
                    root.setBackgroundColor(Color.parseColor("#B19CDA"))

                    // Set text style to bold
                    rankTextView.setTypeface(null, Typeface.BOLD)
                    nameTextView.setTypeface(null, Typeface.BOLD)
                    xpTextView.setTypeface(null, Typeface.BOLD)
                } else {
                    // Reset appearance
                    root.setBackgroundColor(Color.TRANSPARENT)
                    rankTextView.setTypeface(null, Typeface.NORMAL)
                    nameTextView.setTypeface(null, Typeface.NORMAL)
                    xpTextView.setTypeface(null, Typeface.NORMAL)
                }

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