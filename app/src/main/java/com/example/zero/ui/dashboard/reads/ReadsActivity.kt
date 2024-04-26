package com.example.zero.ui.dashboard.reads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zero.R
import com.example.zero.databinding.ActivityReadsBinding

class ReadsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}