package com.example.zero.ui.achievement.badges

import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.FirebaseManager
import com.example.zero.data.Message
import com.example.zero.data.ResourceRepository
import com.example.zero.data.createBadgesList
import com.example.zero.databinding.FragmentBadgesBinding
import com.example.zero.ui.DefaultViewModelFactory
import com.example.zero.ui.LoaderOverlay
import com.example.zero.ui.achievement.leaderboard.LeaderboardAdapter
import com.example.zero.ui.achievement.leaderboard.LeaderboardSelectOverlayViewModel
import com.example.zero.ui.achievement.leaderboard.LeaderboardViewModel
import com.example.zero.ui.utils.RectDimmedPromptBackground
import com.example.zero.ui.utils.TransparentPromptFocal
import com.firebase.ui.database.FirebaseRecyclerOptions
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class BadgesFragment : Fragment() {

    private var _binding: FragmentBadgesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Initialize the ResourceRepository
    private val resourceRepository by lazy { ResourceRepository(requireContext()) }

    // Use the custom ViewModelFactory with viewModels delegate
    private val viewModel:  BadgesViewModel by viewModels {
        DefaultViewModelFactory(resourceRepository)
    }

    private val dialog = LoaderOverlay()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseManager.currentUser.uid.toString()

        viewModel.getUserDataWithBadges(uid)

        viewModel.badgesList.observe(viewLifecycleOwner){
            val badgesAdapter = BadgesAdapter(it)
            val layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rvBadges.addItemDecoration(GridSpacingItemDecoration(2, 64, false))

            binding.rvBadges.layoutManager = layoutManager
            binding.rvBadges.adapter = badgesAdapter
            badgesAdapter.setOnItemClickCallback(object : BadgesAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Badges) {
                    showBadgeDialog(data)
                }
            })

        }
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

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

