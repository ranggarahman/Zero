package com.example.zero.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zero.MainActivity
import com.example.zero.R
import com.example.zero.auth.LoginActivity
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val user = FirebaseManager.currentUser.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCurrentUser()

        binding.button.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkCurrentUser() {
        if (user != null) {
            // User is signed in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}