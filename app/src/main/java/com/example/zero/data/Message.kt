package com.example.zero.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    val uidSender: String? = null,
    val text: String? = null,
    val name: String? = null,
    val photoUrl: Int? = null,
    val timestamp: Long? = null
){
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
