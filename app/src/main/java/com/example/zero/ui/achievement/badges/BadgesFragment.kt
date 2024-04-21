package com.example.zero.ui.achievement.badges

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.createBadgesList
import com.example.zero.databinding.FragmentAchievementBinding
import com.example.zero.databinding.FragmentBadgesBinding
import com.example.zero.ui.achievement.AchievementViewModel

class BadgesFragment : Fragment() {

    private var _binding: FragmentBadgesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val badgesViewModel =
            ViewModelProvider(this)[BadgesViewModel::class.java]

        _binding = FragmentBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val badgesAdapter = BadgesAdapter(createBadgesList())
        binding.rvBadges.adapter = badgesAdapter
        binding.rvBadges.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

        badgesAdapter.setOnItemClickCallback(object : BadgesAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Badges) {
                showBadgeDialog(data)
            }
        })

    }

    private fun showBadgeDialog(badgeItem : Badges) {
        val dialog = BadgesOverlayFragment()
        val args = Bundle().apply {
            putString(BADGE_TITLE, badgeItem.title)
            putString(BADGE_DESC, badgeItem.desc)
            putString(BADGE_IMG_URL, badgeItem.imgUrl)
        }
        dialog.arguments = args
        dialog.show(parentFragmentManager, "popup_dialog")
    }

    companion object {
        const val BADGE_TITLE = "badge_title"
        const val BADGE_DESC = "badge_desc"
        const val BADGE_IMG_URL = "badge_img_url"
    }

}