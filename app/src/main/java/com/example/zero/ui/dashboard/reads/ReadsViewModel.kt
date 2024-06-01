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
import com.google.firebase.database.getValue
import kotlin.random.Random

class ReadsViewModel : ViewModel() {

    private val _completionCount = MutableLiveData<Int>()
    val completionCount : LiveData<Int> = _completionCount

    private val _reads = MutableLiveData<List<ReadsItem>>()
    val reads : LiveData<List<ReadsItem>> = _reads

    private val _popup = MutableLiveData<Boolean>()
    val popup : LiveData<Boolean> = _popup

    private val imageUrls = listOf(
        "https://i.ibb.co.com/nPS6vf6/a1643f261fb77b437b234115ba7b2101.jpg",
        "https://i.ibb.co.com/Gd1tKK6/health-care-doctor-monitor.jpg",
        "https://i.ibb.co.com/frLQ3Nk/OIP.jpg",
        "https://i.ibb.co.com/DQyV3nf/a.jpg",
        "https://i.ibb.co.com/QnbC0dQ/machine-learning-in-hospitals-pic03-20210415-etkho-hospital-engineering.jpg",
        "https://i.ibb.co.com/QbdgP0r/Benefits-of-Medical-Animation-Services-735x349-1-min.jpg",
        "https://i.ibb.co.com/F3h8dcz/OIP.jpg",
        "https://i.ibb.co.com/BsRPprJ/Getty-Images-1164501571.jpg"
    )

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
                        val randomImageUrl = imageUrls[Random.nextInt(imageUrls.size)]

                        // Based on type, construct the Question object appropriately

                        val items = ReadsItem(
                            title = title ,
                            imgUrl = randomImageUrl,
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
                                    val currentReadsTaken = materialSnapshot.child("readsTaken").getValue(Int::class.java) ?: 0
                                    val quizTaken = materialSnapshot.child("quizTaken").getValue(Int::class.java) ?: 0
                                    val flashcardTaken = materialSnapshot.child("flashcardTaken").getValue(Int::class.java) ?: 0

                                    val newReadsTaken = currentReadsTaken + 1

                                    materialSnapshot.ref
                                        .child("readsTaken")
                                        .setValue(newReadsTaken).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Points updated successfully
                                                if (newReadsTaken >= 1 && flashcardTaken >= 1 && quizTaken >= 1) {
                                                    materialSnapshot.child("isCompleted").ref.setValue(true)
                                                }
                                                Log.d(TAG, "SUCCESS UPDATE")
                                            } else {
                                                // Error updating points
                                                Log.e(TAG, "FAIL")
                                            }
                                        }

                                } else {
                                    Log.e(TAG, "QUIZ ID NOT FOUND AT $quizId, MATERIAL ID $materialId")
                                }

                                val isCompleted = materialSnapshot.child("isCompleted").getValue(Boolean::class.java) ?: false

                                if (isCompleted) {
                                    _completionCount.value = (_completionCount.value ?: 0) + 1
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