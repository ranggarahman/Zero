package com.example.zero.data

import com.google.gson.annotations.SerializedName
import kotlin.random.Random

data class LeaderboardItem(

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("userpoint")
    val userpoint: Int? = null
)

fun generateLeaderboard(): List<LeaderboardItem> {
    // Generate 20 items with random points
    val leaderboardItems = List(20) {
        LeaderboardItem("user$it", Random.nextInt(0, 500)) // Adjust the range as needed
    }

    return leaderboardItems.sortedByDescending { it.userpoint ?: 0 }
}
