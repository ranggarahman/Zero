//package com.example.zero.ui.chat
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewbinding.ViewBinding
//import com.example.zero.data.Message
//import com.example.zero.databinding.ItemMessageReceivedBinding
//import com.example.zero.databinding.ItemMessageSentBinding
//
//class MessageAdapter(private var messageList: List<Message>) :
//    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
//
//    interface OnSecondItemClickCallback {
//        fun onSecondItemClicked(message: String)
//    }
//
//    private var onSecondItemClickCallback: OnSecondItemClickCallback? = null
//
//    fun setCallback(onSecondItemClickCallback: OnSecondItemClickCallback) {
//        this.onSecondItemClickCallback = onSecondItemClickCallback
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun updateList(newList: List<Message>) {
//        messageList = newList
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = when (viewType) {
//            MessageType.RECEIVED_MESSAGE.ordinal -> ItemMessageReceivedBinding.inflate(inflater, parent, false)
//            MessageType.SENT_MESSAGE.ordinal -> ItemMessageSentBinding.inflate(inflater, parent, false)
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//        return ViewHolder(binding)
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return messageList[position].type.ordinal
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val message = messageList[position]
//        holder.bind(message)
//    }
//
//    override fun getItemCount(): Int {
//        return messageList.size
//    }
//
//    inner class ViewHolder(private val binding: ViewBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//
//        fun bind(message: Message) {
//            when (message.type) {
//                MessageType.RECEIVED_MESSAGE -> {
//                    val receivedMessageBinding = binding as ItemMessageReceivedBinding
//                    if (message.text!!.isNotEmpty()) {
//                        receivedMessageBinding.textMessageReceived.visibility = View.VISIBLE
//                        receivedMessageBinding.textMessageReceived.text = message.text
//                        receivedMessageBinding.chatLoadingPlaceholder.visibility = View.GONE
//                    } else {
//                        receivedMessageBinding.textMessageReceived.visibility = View.GONE
//                        receivedMessageBinding.chatLoadingPlaceholder.visibility = View.VISIBLE
//                    }
//                }
//                MessageType.SENT_MESSAGE -> {
//                    val sentMessageBinding = binding as ItemMessageSentBinding
//                    sentMessageBinding.textMessageSent.text = message.text
//                }
//            }
//        }
//    }
//
//}