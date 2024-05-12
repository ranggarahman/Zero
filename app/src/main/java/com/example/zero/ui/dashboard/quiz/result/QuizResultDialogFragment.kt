package com.example.zero.ui.dashboard.quiz.result

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentQuizResultDialogBinding
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.CORRECT_ANSWER_COUNT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.TIME_SPENT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.XP_ACQUIRED_COUNT
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class QuizResultDialogFragment : DialogFragment() {

    private val binding by lazy {
        FragmentQuizResultDialogBinding.inflate(layoutInflater)
    }

    private val quizResultDialogViewModel by viewModels<QuizResultDialogViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        isCancelable = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = arguments?.getInt(CORRECT_ANSWER_COUNT, 0)
        val xp = arguments?.getInt(XP_ACQUIRED_COUNT, 0)
        val time = arguments?.getString(TIME_SPENT)

        binding.textviewScore.text = result.toString()
        binding.textviewXP.text = xp.toString()
        binding.textviewTime.text = time

        setUserpoint(xp!!)

        binding.resultConfirm.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setUserpoint(points: Int) {
        // Get the current Firebase user
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            //val databaseReference = database.getInstance().getReference("users")
            val databaseReference = database.reference.child("users")

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild("uid").equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Update the avatarId property
                            userSnapshot.child("userpoint").ref.setValue(points)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // AvatarId updated successfully
                                        Log.d(TAG, "SUCCESS UPDATE")
                                        dialog?.dismiss()
                                    } else {
                                        // Error updating avatarId
                                        Log.e(TAG, "FAIL")
                                    }
                                }
                        }
                    } else {
                        // User not found
                        Log.e(TAG, "FAIL User not found for UID: $uid ")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred
                    Log.e(TAG, "Error: ${databaseError.message}")
                }
            })
        } ?: run {
            // User not authenticated
            Log.e(TAG, "Error: unaothozired")
        }
    }

    override fun onResume() {
        super.onResume()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    companion object {
        private const val TAG = "QRDF"
    }

}