package com.example.zero.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.Const.PATH_AVATAR_ID
import com.example.zero.data.Const.PATH_UID
import com.example.zero.data.Const.PATH_USERNAME
import com.example.zero.data.Const.PATH_USERS
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentProfileBinding
import com.example.zero.ui.achievement.AchievementFragment.Companion.ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment
import com.example.zero.ui.dashboard.DashboardFragment.Companion.DASH_PREFS_NAME_FIRST_TIME_TUTORIAL
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

        binding.btnEditUsername.setOnClickListener {
            showEditNameDialog()
        }

        binding.btnLogout.setOnClickListener {
            showExitConfirmationDialog()
        }

    }

    private fun showExitConfirmationDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.logout_confirmation_title))
            .setMessage(context.getString(R.string.logout_confirmation_message))
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                // User clicked "Yes" button, finish the activity
                // Clear the first shared preferences file
                val achievementSharedPreferences = context.getSharedPreferences(ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
                with(achievementSharedPreferences.edit()) {
                    clear()
                    apply()
                }

                // Clear the second shared preferences file
                val dashSharedPreferences = context.getSharedPreferences(DASH_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
                with(dashSharedPreferences.edit()) {
                    clear()
                    apply()
                }
                FirebaseManager.currentUser.signOut()
                findNavController().navigate(R.id.action_navigation_profile_to_loginActivity)
                activity?.finish()
            }
            .setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                // User clicked "No" button, dismiss the dialog
                dialog.dismiss()
            }
            .setCancelable(false) // Prevent dialog dismissal when clicking outside or pressing back button
            .setIcon(R.drawable.baseline_warning_24)

        val dialog = builder.create()
        dialog.show()
    }


    private fun showEditNameDialog() {
        val dialog = AvatarNameChangeOverlayFragment()
        dialog.show(parentFragmentManager, "avatar_name_edit_dialog")
    }

    private fun setAvatar() {
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val databaseReference = database.reference.child(PATH_USERS)

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild(PATH_UID).equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Retrieve the avatarId property
                            val avatarId = userSnapshot.child(PATH_AVATAR_ID).getValue(Int::class.java)
                            val username = userSnapshot.child(PATH_USERNAME).getValue(String::class.java)

                            // Use the retrieved avatarId
                            if (avatarId != null) {
                                // Construct the drawable resource name
                                val drawableName = "a$avatarId"

                                // Get the resource identifier for the drawable
                                val resourceId = activity?.resources?.getIdentifier(drawableName, "drawable", requireContext().packageName)

                                // Set the image resource
                                if (resourceId != null) {
                                    binding.profileImageViewFrag.setImageResource(resourceId)
                                    binding.textUsername.text = username
                                    binding.textEmail.text = currentUser.email
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