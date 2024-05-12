package com.example.zero.ui.achievement.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardSelectOverlayViewModel: ViewModel() {

    private val database = FirebaseManager.database
    private val reference = database.reference.child("users")

    private val _badgesList = MutableLiveData<List<Badges>>()

    // Public getter for leaderboardList
    val badgesList: LiveData<List<Badges>> = _badgesList

    private val badgeImages = listOf(
        "https://i.ibb.co/n1PQXHn/rank1.png",
        "https://i.ibb.co/5rP7pch/rank2.png",
        "https://i.ibb.co/Z80RGZZ/rank3.png",
        "https://i.ibb.co/PFgHV2s/rank4.png",
        "https://i.ibb.co/hYj9V6L/rank5.png",
        "https://i.ibb.co/h2wr3cm/rank6.png"
    )

    fun getUserDataWithBadges(uid: String) {
        reference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val badgesSnapshot = snapshot.child("badges")
                val badgesList = mutableListOf<Badges>()

                if (badgesSnapshot.exists()) {
                    badgesSnapshot.children.forEach { badgeSnapshot ->
                        val id = badgeSnapshot.child("id").getValue(Int::class.java)
                        val isUnlocked = badgeSnapshot.child("isUnlocked").getValue(Boolean::class.java)
                        if (id != null && isUnlocked != null && id <= 6) {
                            val title = getGreekLetterTitle(id)
                            val description = getGreekLetterDescription(id)
                            val imgUrl = badgeImages[id - 1] // Adjusting index to match the id
                            badgesList.add(Badges(id, title, description, imgUrl, isUnlocked))
                        }
                    }
                    _badgesList.postValue(badgesList) // Post the fetched badges list to LiveData
                } else {
                    Log.d(TAG, "ERR ON CANCELLED MSD: ERR")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "ERR ON CANCELLED MSD: ${error.message}")
            }
        })
    }


    private fun getGreekLetterTitle(id: Int): String {
        val greekLetters = listOf(
            "alpha", "beta", "gamma", "delta", "epsilon", "zeta"
        )
        return greekLetters[id - 1] // Adjusting index to match the id
    }

    private fun getGreekLetterDescription(id: Int): String {
        val greekDescriptions = listOf(
            "Description for alpha",
            "Description for beta",
            "Description for gamma",
            "Description for delta",
            "Description for epsilon",
            "Description for zeta"
        )
        return greekDescriptions[id - 1] // Adjusting index to match the id
    }

    companion object {
        private const val TAG = "LSOF_VM"
    }
}