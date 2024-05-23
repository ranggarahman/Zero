package com.example.zero.ui.achievement.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardViewModel : ViewModel() {

    private val _leaderboardList = MutableLiveData<List<LeaderboardItem>>()

    // Public getter for leaderboardList
    val leaderboardList: LiveData<List<LeaderboardItem>> = _leaderboardList

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    init {
        _loading.value = true
    }

    fun getLeaderboard() {
        val database = FirebaseManager.database
        val reference = database.reference.child("users")

        try {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaderboardItems = mutableListOf<LeaderboardItem>()
                    snapshot.children.forEach { userSnapshot ->
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        val userpoint = userSnapshot.child("userpoints").getValue(Int::class.java)
                        val avatarId = userSnapshot.child("avatarId").getValue(Int::class.java)
                        val uid = userSnapshot.child("uid").getValue(String::class.java)
                        val leaderboardItem = LeaderboardItem(username, userpoint, avatarId, uid)
                        leaderboardItems.add(leaderboardItem)
                    }
                    // Sort the list based on userpoint in descending order
                    leaderboardItems.sortByDescending { it.userpoint }

                    // Update the top 5 users in the database
                    val top5Uids = leaderboardItems.take(5).map { it.uid }
                    top5Uids.forEach { uid ->
                        reference.child(uid.toString()).child("isTop5").setValue(true)
                    }

                    _leaderboardList.postValue(leaderboardItems)

                    _loading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "ERR ON CANCELLED MSD: ${error.message}")
                    _loading.value = false
                }
            })
        } catch (e: Exception) {
            Log.d(TAG, "ERR CATCH MSD: ${e.message}")
            _loading.value = false
        }
    }


    companion object {
        private const val TAG = "LDBRDVIEWMODEL"
    }
}

