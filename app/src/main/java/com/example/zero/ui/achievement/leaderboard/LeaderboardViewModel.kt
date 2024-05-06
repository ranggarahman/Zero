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

class LeaderboardViewModel: ViewModel() {

    private val _leaderboardList = MutableLiveData<List<LeaderboardItem>>()
    val leaderboardList: LiveData<List<LeaderboardItem>> = _leaderboardList

    fun getLeaderboard() {
        val database = FirebaseManager.database
        val reference = database.reference.child("users")

        try {
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaderboardItems = mutableListOf<LeaderboardItem>()
                    snapshot.children.forEach { userSnapshot ->
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val userpoint = userSnapshot.child("userpoints").getValue(Int::class.java)
                        val avatarId = userSnapshot.child("avatarId").getValue(Int::class.java)
                        val uid = userSnapshot.child("uid").getValue(String::class.java)
                        val leaderboardItem = LeaderboardItem(email, userpoint, avatarId, uid)
                        leaderboardItems.add(leaderboardItem)
                    }
                    // Sort the list based on userpoint in descending order
                    leaderboardItems.sortByDescending { it.userpoint }
                    _leaderboardList.postValue(leaderboardItems)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "ERR ON CANCELLED MSD: ${error.message}")
                }
            })
        } catch (e: Exception){
            Log.d(TAG, "ERR CATCH MSD: ${e.message}")
        }

    }

    companion object {
        private const val TAG = "LDBRDVIEWMODEL"
    }
}