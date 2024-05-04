package com.example.zero.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.data.Avatar
import com.example.zero.data.Badges
import com.example.zero.data.LeaderboardItem
import com.example.zero.databinding.FragmentAvatarSelectOverlayBinding
import com.example.zero.ui.achievement.leaderboard.LeaderboardAdapter

class AvatarSelectOverlayFragment : DialogFragment() {
    private var _binding: FragmentAvatarSelectOverlayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAvatarSelectOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listAvatar = generateAvatarList()

        val leaderboardAdapter = AvatarSelectAdapter(listAvatar)
        binding.rvSelectAvatar.adapter = leaderboardAdapter
        binding.rvSelectAvatar.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

        val itemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )

        binding.rvSelectAvatar.addItemDecoration(itemDecoration)

        leaderboardAdapter.setOnItemClickCallback(object : AvatarSelectAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Avatar) {
                Toast.makeText(requireContext(), "CLICKED ITEM ${data.id}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun generateAvatarList(): List<Avatar> {
        val avatarList = mutableListOf<Avatar>()
        val drawablePrefix = "a"
        val numAvatars = 20

        for (i in 1..numAvatars) {
            val imgInt = getDrawableResourceId(drawablePrefix + i)
            avatarList.add(Avatar(imgInt, i))
        }

        return avatarList
    }

    private fun getDrawableResourceId(name: String): Int {
        // Assuming you have a Context instance available
        // If not, you'll need to pass a Context as a parameter to this function
        return requireContext().resources.getIdentifier(name, "drawable", requireContext().packageName)
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
}