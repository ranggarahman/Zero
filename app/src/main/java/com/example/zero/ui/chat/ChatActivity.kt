package com.example.zero.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.Const
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.example.zero.data.Message
import com.example.zero.databinding.ActivityChatBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment
import com.example.zero.ui.achievement.leaderboard.LeaderboardAdapter
import com.example.zero.ui.chat.privatechat.PrivateChatActivity
import com.example.zero.ui.dashboard.CongratsPopupDialogFragment
import com.example.zero.ui.notifications.AvatarSelectOverlayFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private val currentUser = FirebaseManager.currentUser.currentUser
    private val database = FirebaseManager.database

    private val viewModel by viewModels<ChatViewModel>()

    private lateinit var binding: ActivityChatBinding
    private var adapter: FirebaseMessageAdapter = FirebaseMessageAdapter(
        FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(database.reference.child(MESSAGES_CHILD), Message::class.java)
            .build(), ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.setAvatar()

        val messagesRef = database.reference.child(MESSAGES_CHILD)

        viewModel.retrievedObject.observe(this@ChatActivity){item ->

            Log.d(TAG, "$item")

            buttonListener(messagesRef, item.username ?: "", item.avatarId ?: 0)

            val options = FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messagesRef, Message::class.java)
                .build()
            adapter = FirebaseMessageAdapter(options, item.username)
            binding.chatRecyclerView.adapter = adapter
            adapter.startListening()

            val manager = LinearLayoutManager(this)
            manager.stackFromEnd = true
            binding.chatRecyclerView.layoutManager = manager

            adapter.setOnItemClickCallback(object : FirebaseMessageAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Message) {
                    Log.d(TAG, "DATA CLICKED TARGET : $data")
                    val intent = Intent(this@ChatActivity, PrivateChatActivity::class.java)
                    intent.putExtra(KEY_UID_SENDER, data.uidSender)
                    intent.putExtra(KEY_NAME_SENDER, data.name)
                    intent.putExtra(KEY_PHOTOURL_SENDER, data.photoUrl)
                    startActivity(intent)
                    finish()
                }
            })

        }

        binding.btnPrivateBack.setOnClickListener {
            // Use the onBackPressedDispatcher to handle the back press
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buttonListener(messagesRef: DatabaseReference, username: String, avatarId: Int) {

        val drawableName = "a$avatarId"
        val resourceId = resources?.getIdentifier(drawableName, "drawable", this.packageName)

        binding.buttonSendMessage.setOnClickListener {
            val uid = currentUser?.uid ?: return@setOnClickListener
            val friendlyMessage = Message(
                uidSender = uid,
                text = binding.textMessageInput.text.toString(),
                name = username,
                photoUrl = resourceId,
                timestamp = Date().time
            )
            messagesRef.push().setValue(friendlyMessage) { error, _ ->
                if (error != null) {
                    Toast.makeText(
                        this,
                        getString(R.string.send_error) + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "isChatSent WORKING")
                    setAchievement()
                }
            }
            binding.textMessageInput.setText("")
        }
    }

    private fun setAchievement() {
        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val userRef = database.reference.child("${Const.PATH_USERS}/$uid/isChatSent")

            // Add a SingleValueEventListener to retrieve the user data
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot exists
                    if (dataSnapshot.exists()) {
                        // Check the current value of isChatSent
                        val isChatSent = dataSnapshot.getValue(Boolean::class.java)
                        if (isChatSent == false) {
                            // Update the isChatSent property to true
                            userRef.setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showBadgeDialog(
                                        Badges(
                                            id = 2,
                                            title = "Congrats!",
                                            desc = "You have unlocked this badge for sending your first chat.",
                                            imgUrl = "https://i.ibb.co.com/5rP7pch/rank2.png",
                                            isUnlocked = true
                                        )
                                    )
                                    Log.d(TAG, "SUCCESS UPDATE")
                                } else {
                                    // Error updating isChatSent
                                    Log.e(TAG, "FAIL, ${task.exception}")
                                }
                            }
                        } else if (isChatSent == true) {
                            // isChatSent is already true
                            Log.d(TAG, "isChatSent is already true")
                        }
                    } else {
                        // isChatSent path does not exist, create it and set it to true
                        userRef.setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // isChatSent created and set to true successfully
                                Log.d(TAG, "SUCCESS CREATE AND UPDATE")
                            } else {
                                // Error creating and setting isChatSent
                                Log.e(TAG, "FAIL, ${task.exception}")
                            }
                        }
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

    private fun showBadgeDialog(badgeItem : Badges) {
        val dialog = CongratsPopupDialogFragment()
        val args = Bundle().apply {
            putString(BadgesFragment.BADGE_TITLE, badgeItem.title)
            putString(BadgesFragment.BADGE_DESC, badgeItem.desc)
            putString(BadgesFragment.BADGE_IMG_URL, badgeItem.imgUrl)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "congrats_unlock_dialog_from_chat")
    }


    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    companion object {
        const val KEY_UID_SENDER = "KEY_UID_SENDER"
        const val KEY_NAME_SENDER = "KEY_NAME_SENDER"
        const val KEY_PHOTOURL_SENDER = "KEY_PHOTOURL_SENDER"
        const val MESSAGES_CHILD = "messages"
        private const val TAG = "CHAT_ACTIVITY"
    }
}
