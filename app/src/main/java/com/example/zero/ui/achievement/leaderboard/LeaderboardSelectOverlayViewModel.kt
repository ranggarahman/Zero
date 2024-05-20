package com.example.zero.ui.achievement.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.google.android.gms.tasks.Tasks
import com.example.zero.data.LeaderboardItem
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardSelectOverlayViewModel: ViewModel() {

    private val database = FirebaseManager.database
    private val reference = database.reference.child("users")

    private val _badgesList = MutableLiveData<List<Badges>>()

    // Public getter for leaderboardList
    val badgesList: LiveData<List<Badges>> = _badgesList

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val badgeImages = listOf(
        "https://i.ibb.co/n1PQXHn/rank1.png",
        "https://i.ibb.co/5rP7pch/rank2.png",
        "https://i.ibb.co/Z80RGZZ/rank3.png",
        "https://i.ibb.co/PFgHV2s/rank4.png",
        "https://i.ibb.co/hYj9V6L/rank5.png",
        "https://i.ibb.co/h2wr3cm/rank6.png"
    )

    init {
        _loading.value = true
    }

    fun getUserDataWithBadges(uid: String) {
        reference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val badgesSnapshot = snapshot.child("badges")
                val userpointsSnapshot = snapshot.child("userpoints")
                val badgesList = mutableListOf<Badges>()

                // Retrieve the current user points
                val userPoints = userpointsSnapshot.getValue(Int::class.java) ?: 0

                if (badgesSnapshot.exists()) {
                    val badgeUpdates = mutableListOf<Task<Void>>()

                    badgesSnapshot.children.forEach { badgeSnapshot ->
                        val id = badgeSnapshot.child("id").getValue(Int::class.java)
                        val isUnlocked = badgeSnapshot.child("isUnlocked").getValue(Boolean::class.java)
                        if (id != null && isUnlocked != null && id <= 6) {
                            val title = getGreekLetterTitle(id)
                            val description = getGreekLetterDescription(id)
                            val imgUrl = badgeImages[id - 1] // Adjusting index to match the id
                            badgesList.add(Badges(id, title, description, imgUrl, isUnlocked))

                            // Check if the user points exceed or do not meet the thresholds and update the badges
                            if (id == 5) {
                                val task = badgeSnapshot.child("isUnlocked").ref.setValue(userPoints > 200)
                                badgeUpdates.add(task)
                            }
                            if (id == 6) {
                                val task = badgeSnapshot.child("isUnlocked").ref.setValue(userPoints > 300)
                                badgeUpdates.add(task)
                            }
                        }
                    }

                    // Ensure all badge updates are completed before posting the updated list
                    Tasks.whenAll(badgeUpdates).addOnCompleteListener {
                        _badgesList.postValue(badgesList) // Post the fetched badges list to LiveData
                        _loading.value = false
                    }
                } else {
                    Log.d(TAG, "No badges found for user.")
                    _loading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error on cancelled: ${error.message}")
                _loading.value = false
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