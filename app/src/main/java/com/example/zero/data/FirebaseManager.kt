package com.example.zero.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    val database = FirebaseDatabase.getInstance("https://zero-91ff3-default-rtdb.asia-southeast1.firebasedatabase.app/").apply {
        setPersistenceEnabled(true)
    }

    val currentUser = FirebaseAuth.getInstance().apply {
        currentUser
    }
}