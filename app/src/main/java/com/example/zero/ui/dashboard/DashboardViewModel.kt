package com.example.zero.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.Const
import com.example.zero.data.FirebaseManager
import com.example.zero.data.MaterialListItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class DashboardViewModel : ViewModel() {
    private val _materialList = MutableLiveData<List<MaterialListItem>>()
    val materialList: LiveData<List<MaterialListItem>>
        get() = _materialList

    private val _username = MutableLiveData<String>()
    val username : LiveData<String> = _username


    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    init {
        _loading.value = true
        setAvatar()
        loadMaterials()
    }

    private fun setAvatar() {
        val currentUser = FirebaseManager.currentUser.currentUser
        val database = FirebaseManager.database

        // Check if the user is authenticated
        currentUser?.uid?.let { uid ->
            // Reference to the Firebase database
            val databaseReference = database.reference.child(Const.PATH_USERS)

            // Query to find the user with the matching UID
            val query: Query = databaseReference.orderByChild(Const.PATH_UID).equalTo(uid)

            // Add a ValueEventListener to retrieve the user data
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if dataSnapshot has children
                    if (dataSnapshot.exists()) {
                        // Iterate over each child (should be only one)
                        dataSnapshot.children.forEach { userSnapshot ->
                            // Retrieve the avatarId property
                            val avatarId = userSnapshot.child(Const.PATH_AVATAR_ID).getValue(Int::class.java)
                            val username = userSnapshot.child(Const.PATH_USERNAME).getValue(String::class.java)

                            // Use the retrieved avatarId
                            if (username != null) {
                                _username.value = username.toString()
                                _loading.value = false
                            } else {
                                // AvatarId not found or null
                                println("username not found for UID: $uid")
                                _loading.value = false
                            }
                        }
                    } else {
                        // User not found
                        _loading.value = false
                        println("User not found for UID: $uid")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Error occurred
                    _loading.value = false
                    println("Error: ${databaseError.message}")
                }
            })
        } ?: run {
            // User not authenticated
            _loading.value = false
            println("User not authenticated.")
        }
    }

    private fun loadMaterials() {
        val databaseReference = FirebaseManager.database.getReference("materials")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val materials = mutableListOf<MaterialListItem>()
                for (materialSnapshot in snapshot.children) {
                    val id = materialSnapshot.key?.toInt() ?: 0
                    val title = materialSnapshot.child("title").getValue(String::class.java) ?: ""
                    val img = materialSnapshot.child("imgurl").getValue(String::class.java) ?: ""
                    materials.add(MaterialListItem(id, title, img))
                }
                _materialList.value = materials
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error

                _loading.value = false
            }
        })
    }
}
