package com.example.zero.ui.chat.privatechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.FirebaseManager
import com.example.zero.data.Message
import com.example.zero.databinding.ActivityChatBinding
import com.example.zero.databinding.ActivityPrivateChatBinding
import com.example.zero.ui.chat.ChatActivity
import com.example.zero.ui.chat.ChatActivity.Companion.KEY_NAME_SENDER
import com.example.zero.ui.chat.ChatActivity.Companion.KEY_PHOTOURL_SENDER
import com.example.zero.ui.chat.ChatActivity.Companion.KEY_UID_SENDER
import com.example.zero.ui.chat.FirebaseMessageAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import java.util.Date

class PrivateChatActivity : AppCompatActivity() {

    private val currentUser = FirebaseManager.currentUser.currentUser
    private val database = FirebaseManager.database

    private lateinit var binding : ActivityPrivateChatBinding

    private val viewModel by viewModels<PrivateChatViewModel>()

    private lateinit var adapter: PrivateChatFirebaseMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uidSender = intent.getStringExtra(KEY_UID_SENDER)
        val name = intent.getStringExtra(KEY_NAME_SENDER)
        val photoUrl = intent.getIntExtra(KEY_PHOTOURL_SENDER, 0)

        Log.d(TAG, "DATA RETRIEVED : $name, $photoUrl")

        binding.imageviewTarget.setImageResource(photoUrl)
        binding.usernameTarget.text = name


        binding.btnPrivateBack.setOnClickListener {
            // Use the onBackPressedDispatcher to handle the back press
            onBackPressedDispatcher.onBackPressed()
        }


        val currentUserUid = currentUser?.uid

        val privatePath = generatePath(uidSender!!, currentUserUid!!)

        viewModel.setAvatar()

        val messagesRef = database.reference.child("${MESSAGES_CHILD}/$privatePath")

        buttonListener(messagesRef, name!!, photoUrl)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()
        adapter = PrivateChatFirebaseMessageAdapter(options, name)
        binding.privateChatRecyclerView.adapter = adapter

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.privateChatRecyclerView.layoutManager = manager
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
                }
            }
            binding.textMessageInput.setText("")
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

    private fun generatePath(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "$uid1$uid2" else "$uid2$uid1"
    }


    companion object {
        private const val TAG = "PCA_225"
        private const val MESSAGES_CHILD = "messages_private"
    }
}