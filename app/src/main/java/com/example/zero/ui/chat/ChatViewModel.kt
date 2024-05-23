package com.example.zero.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.Const
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ChatViewModel: ViewModel() {

    private val currentUser = FirebaseManager.currentUser.currentUser
    private val database = FirebaseManager.database

    private val _retrievedObject = MutableLiveData<LeaderboardItem>()
    val retrievedObject : LiveData<LeaderboardItem> = _retrievedObject

    fun setAvatar() {
        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val databaseReference = database.reference.child(Const.PATH_USERS)

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild(Const.PATH_UID).equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Retrieve the avatarId and username properties
                            val retrievedUsername = userSnapshot.child(Const.PATH_USERNAME).getValue(String::class.java)
                            val retrievedAvatarId = userSnapshot.child(Const.PATH_AVATAR_ID).getValue(Int::class.java)
                            val retrievedUserpoint = userSnapshot.child(Const.PATH_USERPOINTS).getValue(Int::class.java)

                            _retrievedObject.value = LeaderboardItem(
                                username = retrievedUsername,
                                avatarId = retrievedAvatarId,
                                userpoint = retrievedUserpoint,
                                uid = currentUser.uid
                            )
                        }
                    } else {
                        // User not found
                        Log.d(TAG, "User not found for UID: $uid")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred
                    Log.e(TAG, "Error: ${databaseError.message}")
                }
            })
        } ?: run {
            // User not authenticated
            Log.e(TAG, "User not authenticated.")
        }
    }

    companion object {
        private const val TAG = "CHATVIEWMODEL"
    }
}