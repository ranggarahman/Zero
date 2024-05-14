package com.example.zero.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentProfileBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setAvatar()

        binding.btnEditProfile.setOnClickListener {
            showSelectorDialog()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseManager.currentUser.signOut()
            findNavController().navigate(R.id.action_navigation_profile_to_loginActivity)
            activity?.finish()
        }

    }

    private fun setAvatar() {
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val databaseReference = database.reference.child("users")

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild("uid").equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Retrieve the avatarId property
                            val avatarId = userSnapshot.child("avatarId").getValue(Int::class.java)

                            // Use the retrieved avatarId
                            if (avatarId != null) {
                                // Construct the drawable resource name
                                val drawableName = "a$avatarId"

                                // Get the resource identifier for the drawable
                                val resourceId = requireContext().resources.getIdentifier(drawableName, "drawable", requireContext().packageName)

                                // Set the image resource
                                if (resourceId != 0) {
                                    binding.profileImageViewFrag.setImageResource(resourceId)
                                } else {
                                    // Drawable resource not found
                                    println("Drawable resource not found for avatarId: $avatarId")
                                }
                            } else {
                                // AvatarId not found or null
                                println("AvatarId not found for UID: $uid")
                            }
                        }
                    } else {
                        // User not found
                        println("User not found for UID: $uid")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred
                    println("Error: ${databaseError.message}")
                }
            })
        } ?: run {
            // User not authenticated
            println("User not authenticated.")
        }

    }

    private fun showSelectorDialog() {
        val dialog = AvatarSelectOverlayFragment()
        dialog.show(parentFragmentManager, "avatar_select_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}