package com.example.zero.ui.notifications

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Avatar
import com.example.zero.data.Const
import com.example.zero.data.Const.PATH_UID
import com.example.zero.data.Const.PATH_USERNAME
import com.example.zero.data.Const.PATH_USERS
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentAvatarNameChangeOverlayBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class AvatarNameChangeOverlayFragment: DialogFragment() {
    private var _binding: FragmentAvatarNameChangeOverlayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAvatarNameChangeOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmUsername.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyleCustom)
            alertDialogBuilder.apply {
                setTitle("Mengubah Username")
                setMessage("Kamu yakin ingin mengganti username kamu menjadi $username?")
                setPositiveButton("Ya") { dialog, which ->
                    changeUsername(username)
                    dialog.dismiss()
                }
                setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }

    private fun changeUsername(name: String) {
        // Get the current Firebase user
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            //val databaseReference = database.getInstance().getReference("users")
            val databaseReference = database.reference.child(PATH_USERS)

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild(PATH_UID).equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Update the avatarId property
                            userSnapshot.child(PATH_USERNAME).ref.setValue(name)
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
        // Set the width and height of the dialog fragment to match_parent
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ANCOF"
    }
}