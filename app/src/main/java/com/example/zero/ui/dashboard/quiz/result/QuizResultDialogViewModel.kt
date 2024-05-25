package com.example.zero.ui.dashboard.quiz.result

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.zero.data.Const
import com.example.zero.data.Const.PATH_USERPOINTS
import com.example.zero.data.FirebaseManager
import com.example.zero.ui.notifications.AvatarSelectOverlayFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class QuizResultDialogViewModel : ViewModel() {

    fun setResult(resultPoints: Int, quizId: Int) {
        // Get the current Firebase user
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val databaseReference = database.reference.child(Const.PATH_USERS)

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild(Const.PATH_UID).equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Get the current points from the snapshot
                            val currentPoints = userSnapshot.child(PATH_USERPOINTS).getValue(Int::class.java) ?: 0

                            // Add the new points to the current points
                            val newTotalPoints = currentPoints + resultPoints

                            // Update the userpoint property with the new total
                            userSnapshot.child(PATH_USERPOINTS).ref.setValue(newTotalPoints)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Points updated successfully
                                        Log.d(TAG, "SUCCESS UPDATE")
                                    } else {
                                        // Error updating points
                                        Log.e(TAG, "FAIL")
                                    }
                                }

                            // Update quizTaken count in materialsCompletion
                            val materialsCompletionRef = userSnapshot.child("materialsCompletion")
                            materialsCompletionRef.children.forEach { materialSnapshot ->
                                val materialId = materialSnapshot.child("id").getValue(Int::class.java)
                                if (materialId == quizId) {
                                    val quizTaken = materialSnapshot.child("quizTaken").getValue(Int::class.java) ?: 0
                                    materialSnapshot.ref
                                        .child("quizTaken")
                                        .setValue(quizTaken + 1).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Points updated successfully
                                            Log.d(TAG, "SUCCESS UPDATE")
                                        } else {
                                            // Error updating points
                                            Log.e(TAG, "FAIL")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // User not found
                        Log.e(TAG, "FAIL User not found for UID: $uid")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred
                    Log.e(TAG, "Error: ${databaseError.message}")
                }
            })
        } ?: run {
            // User not authenticated
            Log.e(TAG, "Error: unauthorized")
        }
    }

    
    companion object {
        private const val TAG = "QRDVM"
    }
    
}