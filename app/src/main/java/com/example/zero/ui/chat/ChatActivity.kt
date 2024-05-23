package com.example.zero.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Const
import com.example.zero.data.FirebaseManager
import com.example.zero.data.Message
import com.example.zero.databinding.ActivityChatBinding
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
            buttonListener(messagesRef, item.username!!, item.avatarId!!)

            val options = FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messagesRef, Message::class.java)
                .build()
            adapter = FirebaseMessageAdapter(options, item.username)
            binding.chatRecyclerView.adapter = adapter
            adapter.startListening()

            val manager = LinearLayoutManager(this)
            manager.stackFromEnd = true
            binding.chatRecyclerView.layoutManager = manager
        }
    }

    private fun buttonListener(messagesRef: DatabaseReference, username: String, avatarId: Int) {
        binding.buttonSendMessage.setOnClickListener {
            val uid = currentUser?.uid ?: return@setOnClickListener
            val friendlyMessage = Message(
                uidSender = uid,
                text = binding.textMessageInput.text.toString(),
                name = username,
                photoUrl = avatarId,
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
                                    // isChatSent updated successfully
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


    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    companion object {
        const val MESSAGES_CHILD = "messages"
        private const val TAG = "CHAT_ACTIVITY"
    }
}
