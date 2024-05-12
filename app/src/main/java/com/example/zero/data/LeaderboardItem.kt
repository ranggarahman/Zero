package com.example.zero.data

import com.google.gson.annotations.SerializedName
import kotlin.random.Random

data class LeaderboardItem(

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("userpoint")
    val userpoint: Int? = null,

    @field:SerializedName("avatarId")
    val avatarId: Int? = null,

    @field:SerializedName("uid")
    val uid: String? = null,
)

