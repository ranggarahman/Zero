package com.example.zero.data

import com.example.zero.R

data class Badges(
    val id: Int,
    val title: String,
    val desc: String,
    val imgUrl: String,
    var isUnlocked: Boolean,
)

fun createBadgesList(): List<Badges> {
    val badgeImages = listOf(
        "https://i.ibb.co/n1PQXHn/rank1.png",
        "https://i.ibb.co/5rP7pch/rank2.png",
        "https://i.ibb.co/Z80RGZZ/rank3.png",
        "https://i.ibb.co/PFgHV2s/rank4.png",
        "https://i.ibb.co/hYj9V6L/rank5.png",
        "https://i.ibb.co/h2wr3cm/rank6.png"
    )

    val badgesList = mutableListOf<Badges>()
    for (i in 1..4) {
        badgesList.add(
            Badges(
                id = i,
                title = "title$i",
                desc = "desc$i",
                imgUrl = badgeImages[i - 1],
                isUnlocked = true
            )
        )
    }

    badgesList.add(
        Badges(
            id = 5,
            title = "The Gayyer",
            desc = "Mendapatkan lebih dari 300 XP",
            imgUrl = "https://i.ibb.co/hYj9V6L/rank5.png",
            isUnlocked = false
        )
    )

    badgesList.add(
        Badges(
            id = 6,
            title = "The Gayyer",
            desc = "Mendapatkan lebih dari 400 XP",
            imgUrl = "https://i.ibb.co/h2wr3cm/rank6.png",
            isUnlocked = false
        )
    )

    return badgesList
}
