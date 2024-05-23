package com.example.zero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zero.data.ResourceRepository
import com.example.zero.ui.achievement.badges.BadgesViewModel
import com.example.zero.ui.achievement.leaderboard.LeaderboardSelectOverlayViewModel

class DefaultViewModelFactory(private val resourceRepository: ResourceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardSelectOverlayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaderboardSelectOverlayViewModel(resourceRepository) as T
        }

        if (modelClass.isAssignableFrom(BadgesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BadgesViewModel(resourceRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
