package com.example.zero.ui.achievement.leaderboard

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.data.LeaderboardItem
import com.example.zero.databinding.FragmentLeaderboardListBinding
import com.example.zero.ui.LoaderOverlay
import com.example.zero.ui.achievement.AchievementFragment
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment
import com.example.zero.ui.utils.CircleDimmedPromptBackground
import com.example.zero.ui.utils.RectDimmedPromptBackground
import com.example.zero.ui.utils.TransparentPromptFocal
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

/**
 * A fragment representing a list of Items.
 */
class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var isFirstTime = true
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

        leaderboardViewModel.leaderboardList.observe(viewLifecycleOwner){
            val leaderboardAdapter = LeaderboardAdapter(it, requireContext(), currentUid)
            binding.leaderboardRecyclerView.adapter = leaderboardAdapter
            binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            val itemDecoration = DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )

            binding.leaderboardRecyclerView.addItemDecoration(itemDecoration)

            leaderboardAdapter.setOnItemClickCallback(object : LeaderboardAdapter.OnItemClickCallback{
                override fun onItemClicked(data: LeaderboardItem) {
//                Toast.makeText(requireContext(), "CLICKED ITEM ${data.uid}", Toast.LENGTH_LONG).show()
                    showBadgeDialog(data.uid, data.username)

                }
            })
        }

        val sharedPreferences = requireContext().getSharedPreferences(
            AchievementFragment.ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
        isFirstTime = sharedPreferences.getBoolean(AchievementFragment.ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL, true)

        if (isFirstTime) {
           showLeaderboardTapTargetPrompt()
        }
    }

    private fun showLeaderboardTapTargetPrompt() {
        val customFontPrimary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)
        val customFontSecondary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.leaderboardTable)
            .setPrimaryText("Ini adalah papan peringkat")
            .setSecondaryText("Kamu bisa melihat ranking semua pengguna, termasuk kamu.")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(customFontPrimary)
            .setSecondaryTextTypeface(customFontSecondary)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptFocal(TransparentPromptFocal()) // Optional: change the focal shape
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // Show the next prompt when the first one is dismissed
                    showRecyclerViewItemPrompt()
                }
            }
            .show()
    }

    private fun showRecyclerViewItemPrompt() {
        // Find the view for the first item in the RecyclerView
        val firstItemView = binding.leaderboardRecyclerView.layoutManager?.findViewByPosition(0)

        if (firstItemView != null) {
            val viewHolder = binding.leaderboardRecyclerView
                .getChildViewHolder(firstItemView) as LeaderboardAdapter.AchievementViewHolder

            val customFontPrimary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)
            val customFontSecondary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)

            // Create and show the Material Tap Target Prompt
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(viewHolder.itemView)  // Target the whole item view or a specific view inside the ViewHolder
                .setPrimaryText("Ini adalah pengguna")
                .setSecondaryText("Kamu bisa mengetuk item pengguna untuk melihat badges yang mereka punya")
                .setPrimaryTextColour(resources.getColor(R.color.black))
                .setSecondaryTextColour(resources.getColor(R.color.black))
                .setPrimaryTextTypeface(customFontPrimary)
                .setSecondaryTextTypeface(customFontSecondary)
                .setBackgroundColour(resources.getColor(R.color.green_1))
                .setPromptBackground(RectDimmedPromptBackground())
                .setPromptFocal(TransparentPromptFocal()) // Optional: change the focal shape
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                        // Show the next prompt when the first one is dismissed
                        showTabNavTargetPrompt()
                    }
                }
                .show()
        }
    }

    private fun showTabNavTargetPrompt() {
        val customFontPrimary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)
        val customFontSecondary: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)
        val tabLayout = requireActivity().findViewById<View>(R.id.tab_tablayout)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(tabLayout)
            .setPrimaryText("Ini adalah tab navigasi")
            .setSecondaryText("Kamu bisa melihat papan peringkat dan badge yang kamu miliki.")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(customFontPrimary)
            .setSecondaryTextTypeface(customFontSecondary)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptFocal(TransparentPromptFocal()) // Optional: change the focal shape
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // Show the next prompt when the first one is dismissed
                }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireContext().getSharedPreferences(
            AchievementFragment.ACHIEVEMENT_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
        isFirstTime = sharedPreferences.getBoolean(AchievementFragment.ACHIEVEMENT_KEY_FIRST_TIME_TUTORIAL, true)
    }


    private fun showBadgeDialog(uid : String?, username: String?) {
        val dialog = LeaderboardSelectOverlayFragment()
        val args = Bundle().apply {
            putString(UID_BADGES, uid)
            putString(USERNAME_BADGES, username)
        }
        dialog.arguments = args
        dialog.show(parentFragmentManager, TAG_DIALOG_FROM_LEADERBOARD_FRAGMENT)
    }

    companion object {
        const val UID_BADGES = "uid_badges"
        const val USERNAME_BADGES = "usn_badges"
        private const val TAG_DIALOG_FROM_LEADERBOARD_FRAGMENT = "TAG_DIALOG_FROM_LEADERBOARD_FRAGMENT"
    }

}