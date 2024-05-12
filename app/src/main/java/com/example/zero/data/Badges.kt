package com.example.zero.data

data class Badges(
    val id: Int,
    val title: String,
    val desc: String,
    val imgUrl: String,
    val isUnlocked: Boolean,
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
    for (i in 1..6) {
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
    return badgesList
}
