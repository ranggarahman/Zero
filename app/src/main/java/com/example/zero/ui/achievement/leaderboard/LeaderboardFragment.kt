package com.example.zero.ui.achievement.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.example.zero.databinding.FragmentLeaderboardListBinding

/**
 * A fragment representing a list of Items.
 */
class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val leaderboardViewModel by viewModels<LeaderboardViewModel>()

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

        leaderboardViewModel.getLeaderboard()

        val currentUid = FirebaseManager.currentUser.currentUser?.uid

        leaderboardViewModel.leaderboardList.observe(viewLifecycleOwner){ leaderboardList ->
            val leaderboardAdapter = LeaderboardAdapter(leaderboardList, requireContext(), currentUid)
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

}