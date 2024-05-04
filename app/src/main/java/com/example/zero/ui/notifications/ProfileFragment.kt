package com.example.zero.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentProfileBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val userImg = FirebaseManager.currentUser.currentUser?.photoUrl
//        Glide.with(requireContext()).load(userImg).into(binding.profileImageViewFrag)

        binding.profileImageViewFrag.setOnClickListener {
            showSelectorDialog()
        }
    }

    private fun showSelectorDialog() {
        val dialog = AvatarSelectOverlayFragment()
//        val args = Bundle().apply {
//            putString(BadgesFragment.BADGE_TITLE, badgeItem.title)
//            putString(BadgesFragment.BADGE_DESC, badgeItem.desc)
//            putString(BadgesFragment.BADGE_IMG_URL, badgeItem.imgUrl)
//        }
//        dialog.arguments = args
        dialog.show(parentFragmentManager, "avatar_select_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}