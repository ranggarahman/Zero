package com.example.zero.ui.chat.privatechat

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.zero.data.Message
import com.example.zero.databinding.ItemMessageReceivedBinding
import com.example.zero.databinding.ItemMessageSentBinding
import com.example.zero.databinding.ItemPrivateMessageRecievedBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PrivateChatFirebaseMessageAdapter(
    options: FirebaseRecyclerOptions<Message>,
    private val currentUserName: String?
) : FirebaseRecyclerAdapter<Message, PrivateChatFirebaseMessageAdapter.MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            SENT_MESSAGE_VIEW_TYPE -> ItemMessageSentBinding.inflate(inflater, parent, false)
            RECEIVED_MESSAGE_VIEW_TYPE -> ItemPrivateMessageRecievedBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.bind(model, getItemViewType(position))
    }

    inner class MessageViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, viewType: Int) {
            when (viewType) {
                SENT_MESSAGE_VIEW_TYPE -> {
                    val sentMessageBinding = binding as ItemMessageSentBinding
                    sentMessageBinding.textMessageSent.text = message.text
                    sentMessageBinding.sentMessageTimestamp.text = message.timestamp?.let {
                        DateUtils.getRelativeTimeSpanString(
                            it
                        )
                    }
                }
                RECEIVED_MESSAGE_VIEW_TYPE -> {
                    val receivedMessageBinding = binding as ItemPrivateMessageRecievedBinding
                    if (message.text!!.isNotEmpty()) {
                        receivedMessageBinding.textMessageReceived.visibility = View.VISIBLE
                        //receivedMessageBinding.textChatUsernameItem.text = message.name
                        receivedMessageBinding.textMessageReceived.text = message.text
                        //receivedMessageBinding.imageviewAvatar.setImageResource(message.photoUrl!!)
                        if (message.timestamp != null) {
                            binding.textChatTimestamp.text = DateUtils.getRelativeTimeSpanString(message.timestamp)
                        }
                        receivedMessageBinding.chatLoadingPlaceholder.visibility = View.GONE
                    } else {
                        receivedMessageBinding.textMessageReceived.visibility = View.GONE
                        receivedMessageBinding.chatLoadingPlaceholder.visibility = View.VISIBLE
                    }
                }
                else -> throw IllegalArgumentException("Invalid view type")

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.name == currentUserName && message.name != null){
            SENT_MESSAGE_VIEW_TYPE
        } else {
            RECEIVED_MESSAGE_VIEW_TYPE
        }
    }

    companion object {
        private const val RECEIVED_MESSAGE_VIEW_TYPE = 0
        private const val SENT_MESSAGE_VIEW_TYPE = 1
    }
}