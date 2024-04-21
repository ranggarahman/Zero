package com.example.zero.ui.achievement.badges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.zero.databinding.FragmentBadgesInfoOverlayBinding
import com.example.zero.ui.achievement.badges.BadgesFragment.Companion.BADGE_DESC
import com.example.zero.ui.achievement.badges.BadgesFragment.Companion.BADGE_IMG_URL
import com.example.zero.ui.achievement.badges.BadgesFragment.Companion.BADGE_TITLE

class BadgesOverlayFragment : DialogFragment() {

    private var _binding: FragmentBadgesInfoOverlayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBadgesInfoOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(BADGE_TITLE)
        val desc = arguments?.getString(BADGE_DESC)
        val imgUrl = arguments?.getString(BADGE_IMG_URL)

        binding.titleTextView.text = title
        binding.messageTextView.text = desc



        Glide.with(requireContext()).load(imgUrl).into(binding.iconImageView)

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