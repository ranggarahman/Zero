package com.example.zero.ui.achievement.leaderboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.LeaderboardItem
import com.example.zero.data.generateLeaderboard
import com.example.zero.databinding.FragmentBadgesBinding
import com.example.zero.databinding.FragmentLeaderboardListBinding
import com.example.zero.ui.achievement.badges.BadgesAdapter
import com.example.zero.ui.achievement.badges.BadgesViewModel
import com.example.zero.ui.achievement.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leaderboardAdapter = LeaderboardAdapter(generateLeaderboard())
        binding.leaderboardRecyclerView.adapter = leaderboardAdapter
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )

        binding.leaderboardRecyclerView.addItemDecoration(itemDecoration)

        leaderboardAdapter.setOnItemClickCallback(object : LeaderboardAdapter.OnItemClickCallback{
            override fun onItemClicked(data: LeaderboardItem) {
                Toast.makeText(requireContext(), "CLICKED ITEM ${data.username}", Toast.LENGTH_LONG).show()
            }
        })
    }

}