package com.example.zero.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    val database = FirebaseDatabase.getInstance("https://zero-91ff3-default-rtdb.asia-southeast1.firebasedatabase.app/").apply {
    }

    val currentUser = FirebaseAuth.getInstance().apply {
        currentUser
    }
}