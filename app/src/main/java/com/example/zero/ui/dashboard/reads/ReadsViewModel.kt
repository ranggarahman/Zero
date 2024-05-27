package com.example.zero.ui.dashboard.reads

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.Const
import com.example.zero.data.FirebaseManager
import com.example.zero.data.FlashcardItem
import com.example.zero.data.ReadsItem
import com.example.zero.ui.dashboard.flashcard.FlashcardViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ReadsViewModel : ViewModel() {

    private val _reads = MutableLiveData<List<ReadsItem>>()
    val reads : LiveData<List<ReadsItem>> = _reads

    fun getReads(materialId: Int) {

        val database = FirebaseManager.database.reference
        val reference = database.child("materials").child("$materialId").child("reads")

        try {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempReads = mutableListOf<ReadsItem>()
                    snapshot.children.forEachIndexed { _, flashcardSnapshot ->
                        val title = flashcardSnapshot.child("title").getValue(String::class.java) ?: ""
                        val content = flashcardSnapshot.child("content").getValue(String::class.java) ?: ""

                        // Based on type, construct the Question object appropriately

                        val items = ReadsItem(
                            title = title ,
                            content = content ,
                        )

                        tempReads.add(items)

                    }
                    _reads.postValue(tempReads)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.d(TAG, "EXCEPTION ${error.message}")
                }
            })
        } catch (e: Exception){
            Log.d(TAG, "EXCEPTION ${e.message}")
        }
    }

    fun setResult(quizId: Int) {
        // Get the current Firebase user
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        Log.d(TAG, "RESULT READS CALLED")

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
                            // Update quizTaken count in materialsCompletion
                            val materialsCompletionRef = userSnapshot.child("materialsCompletion")
                            materialsCompletionRef.children.forEach { materialSnapshot ->
                                val materialId = materialSnapshot.child("id").getValue(Int::class.java)
                                if (materialId == quizId) {
                                    val readsTaken = materialSnapshot.child("readsTaken").getValue(Int::class.java) ?: 0
                                    materialSnapshot.ref
                                        .child("readsTaken")
                                        .setValue(readsTaken + 1).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Points updated successfully
                                                Log.d(TAG, "SUCCESS UPDATE")
                                            } else {
                                                // Error updating points
                                                Log.e(TAG, "FAIL")
                                            }
                                        }
                                } else {
                                    Log.e(TAG, "QUIZ ID NOT FOUND AT $quizId, MATERIAL ID $materialId")
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
        private const val TAG = "RVM_VM"
    }
    
}