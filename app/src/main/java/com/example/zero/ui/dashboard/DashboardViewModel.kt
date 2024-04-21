package com.example.zero.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.FirebaseManager
import com.example.zero.data.MaterialListItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DashboardViewModel : ViewModel() {
    private val _materialList = MutableLiveData<List<MaterialListItem>>()
    val materialList: LiveData<List<MaterialListItem>>
        get() = _materialList

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    init {
        _loading.value = true
        loadMaterials()
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
