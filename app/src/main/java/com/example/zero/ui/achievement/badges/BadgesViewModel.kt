package com.example.zero.ui.achievement.badges

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.data.ResourceRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BadgesViewModel(private val resourceRepository: ResourceRepository): ViewModel() {

    private val database = FirebaseManager.database
    private val reference = database.reference.child("users")

    private val _badgesList = MutableLiveData<List<Badges>>()

    // Public getter for leaderboardList
    val badgesList: LiveData<List<Badges>> = _badgesList

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val badgeImages = listOf(
        "https://i.ibb.co.com/n1PQXHn/rank1.png",
        "https://i.ibb.co.com/5rP7pch/rank2.png",
        "https://i.ibb.co.com/Z80RGZZ/rank3.png",
        "https://i.ibb.co.com/PFgHV2s/rank4.png",
        "https://i.ibb.co.com/hYj9V6L/rank5.png",
        "https://i.ibb.co.com/h2wr3cm/rank6.png"
    )

    init {
        _loading.value = true
    }

    fun getUserDataWithBadges(uid: String) {
        reference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val badgesSnapshot = snapshot.child("badges")
                val userpointsSnapshot = snapshot.child("userpoints")
                val materialsCompletionSnapshot = snapshot.child("materialsCompletion")
                val isChatSentSnapshot = snapshot.child("isChatSent")
                val isTop5 = snapshot.child("isTop5")

                val badgesList = mutableListOf<Badges>()

                // Retrieve the current user points
                val userPoints = userpointsSnapshot.getValue(Int::class.java) ?: 0

                if (badgesSnapshot.exists()) {
                    val badgeUpdates = mutableListOf<Task<Void>>()

                    badgesSnapshot.children.forEach { badgeSnapshot ->
                        val id = badgeSnapshot.child("id").getValue(Int::class.java)
                        val isUnlocked = badgeSnapshot.child("isUnlocked").getValue(Boolean::class.java)
                        if (id != null && isUnlocked != null && id <= 6) {
                            val title = getTitles(id)
                            val description = getDescriptions(id)
                            val imgUrl = badgeImages[id - 1] // Adjusting index to match the id
                            badgesList.add(Badges(id, title, description, imgUrl, isUnlocked))

                            // Check if the user fulfills the conditions for each badge and update the status if necessary
                            checkerBadge(
                                id,
                                materialsCompletionSnapshot,
                                isUnlocked,
                                badgeUpdates,
                                badgeSnapshot,
                                userPoints,
                                isChatSentSnapshot,
                                isTop5
                            )
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

    private fun checkerBadge(
        id: Int,
        materialsCompletionSnapshot: DataSnapshot,
        isUnlocked: Boolean,
        badgeUpdates: MutableList<Task<Void>>,
        badgeSnapshot: DataSnapshot,
        userPoints: Int,
        isChatSent: DataSnapshot,
        isTop5: DataSnapshot
    ) {
        when (id) {
            1 -> {

                var quizzesCompleted = 0
                var readingMaterialsCompleted = 0
                var flashcardsCompleted = 0

                materialsCompletionSnapshot.children.forEach { materialSnapshot ->
                    val quizTaken =
                        materialSnapshot.child("quizTaken").getValue(Int::class.java) ?: 0
                    val flashcardTaken =
                        materialSnapshot.child("flashcardTaken").getValue(Int::class.java) ?: 0
                    val readsTaken =
                        materialSnapshot.child("readsTaken").getValue(Int::class.java) ?: 0

                    if (quizTaken >= 1) {
                        quizzesCompleted++
                    }
                    if (flashcardTaken >= 1) {
                        flashcardsCompleted++
                    }
                    if (readsTaken >= 1) {
                        readingMaterialsCompleted++
                    }
                }

                if (quizzesCompleted >= 1 && flashcardsCompleted >= 1 && readingMaterialsCompleted >= 1 && !isUnlocked) {
                    badgeUpdates.add(
                        badgeSnapshot.child("isUnlocked").ref.setValue(
                            true
                        )
                    )
                }

            }

            2 -> {

                val messagesSent = isChatSent.getValue(Boolean::class.java) ?: false

                if (messagesSent && !isUnlocked) {
                    badgeUpdates.add(
                        badgeSnapshot.child("isUnlocked").ref.setValue(
                            true
                        )
                    )
                }
            }
            3 -> {

                var completionCount = 0

                materialsCompletionSnapshot.children.forEach { materialSnapshot ->
                    val isCompleted =
                        materialSnapshot.child("isCompleted").getValue(Boolean::class.java) ?: false

                    if (isCompleted) {
                        completionCount++
                    }
                }

                if (completionCount >= 3 && !isUnlocked) {
                    badgeUpdates.add(
                        badgeSnapshot.child("isUnlocked").ref.setValue(
                            true
                        )
                    )
                }
            }

            4 -> {
                if (userPoints > 200 && !isUnlocked) {
                    badgeUpdates.add(
                        badgeSnapshot.child("isUnlocked").ref.setValue(
                            true
                        )
                    )
                }
            }

            5 -> {
                if (userPoints > 300 && !isUnlocked) {
                    badgeUpdates.add(
                        badgeSnapshot.child("isUnlocked").ref.setValue(
                            true
                        )
                    )
                }
            }

            6 -> {
                val topStatus = isTop5.getValue(Boolean::class.java) ?: false
                Log.d(TAG, "ID 6 OF $topStatus")
                badgeUpdates.add(
                    badgeSnapshot.child("isUnlocked").ref.setValue(
                        topStatus
                    )
                )
            }
        }
    }


    private fun getTitles(id: Int): String {
        val titles = listOf(
            R.string.title_badge_1,
            R.string.title_badge_2,
            R.string.title_badge_3,
            R.string.title_badge_4,
            R.string.title_badge_5,
            R.string.title_badge_6
        )
        return resourceRepository.getString(titles[id - 1])// Adjusting index to match the id
    }

    fun getDescriptions(id: Int): String {
        val descriptions = listOf(
            R.string.desc_badge_1,
            R.string.desc_badge_2,
            R.string.desc_badge_3,
            R.string.desc_badge_4,
            R.string.desc_badge_5,
            R.string.desc_badge_6
        )

        return resourceRepository.getString(descriptions[id - 1])
    }

    companion object {
        private const val TAG = "LSOF_VM"
    }
}