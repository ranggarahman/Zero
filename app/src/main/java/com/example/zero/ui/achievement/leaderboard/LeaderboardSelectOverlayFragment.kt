package com.example.zero.ui.achievement.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.data.createBadgesList
import com.example.zero.databinding.FragmentLeaderboardPopupOverlayBinding
import com.example.zero.ui.achievement.badges.BadgesAdapter
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.leaderboard.LeaderboardFragment.Companion.UID_BADGES
import com.example.zero.ui.achievement.leaderboard.LeaderboardFragment.Companion.USERNAME_BADGES

class LeaderboardSelectOverlayFragment : DialogFragment() {
    private var _binding: FragmentLeaderboardPopupOverlayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val leaderboardSelectOverlayViewModel by viewModels<LeaderboardSelectOverlayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLeaderboardPopupOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getString(UID_BADGES)
        val username = arguments?.getString(USERNAME_BADGES)

        if (uid != null) {
            leaderboardSelectOverlayViewModel.getUserDataWithBadges(uid)
            leaderboardSelectOverlayViewModel.badgesList.observe(viewLifecycleOwner){list ->
                val badgesAdapter = BadgesAdapter(list)
                binding.frgLeaderboardPopupRvbadges.adapter = badgesAdapter
                binding.frgLeaderboardPopupRvbadges.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

                Log.d(TAG, "RETRIEVED LIST $list")
                binding.frgLeaderboardPopupUsername.text = username
            }
        } else {
            Toast.makeText(requireContext(), "LIST NOT RETRIVED", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Set the width and height of the dialog fragment to match_parent
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "LSOF"
    }
}