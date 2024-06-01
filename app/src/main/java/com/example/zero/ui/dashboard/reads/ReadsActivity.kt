package com.example.zero.ui.dashboard.reads

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zero.data.Badges
import com.example.zero.databinding.ActivityReadsBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.achievement.badges.BadgesOverlayFragment
import com.example.zero.ui.dashboard.CongratsPopupDialogFragment
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID


class ReadsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadsBinding
    private var isEndReached = false
    private val readsViewModel by viewModels<ReadsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the id from the intent extras
        val id = intent.extras?.getInt(SELECTED_MATERIAL_ID) ?: 0

        readsViewModel.getReads(id)

        readsViewModel.completionCount.observe(this@ReadsActivity) { completionCount ->
            when (completionCount) {
                3 -> {
                    showBadgeDialog(
                        Badges(
                            id = 3,
                            title = "Congrats!",
                            desc = "You have unlocked this badge for completing 3 materials",
                            imgUrl = "https://i.ibb.co.com/Z80RGZZ/rank3.png",
                            isUnlocked = true
                        )
                    )
                }
                1 -> {
                    showBadgeDialog(
                        Badges(
                            id = 1,
                            title = "Congrats!",
                            desc = "You have unlocked this badge for completing 1 material",
                            imgUrl = "https://i.ibb.co.com/n1PQXHn/rank1.png",
                            isUnlocked = true
                        )
                    )
                }
            }
        }

        readsViewModel.reads.observe(this@ReadsActivity){
            val adapter = ReadsAdapter(it)

            binding.rvReads.adapter = adapter
            binding.rvReads.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvReads.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ( !isEndReached &&
                    recyclerView.canScrollVertically(-1) &&
                    !recyclerView.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE) {

                    isEndReached = true

                    readsViewModel.setResult(id)
                    Toast.makeText(this@ReadsActivity, "END REACHED", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showBadgeDialog(badgeItem : Badges) {
        val dialog = CongratsPopupDialogFragment()
        val args = Bundle().apply {
            putString(BadgesFragment.BADGE_TITLE, badgeItem.title)
            putString(BadgesFragment.BADGE_DESC, badgeItem.desc)
            putString(BadgesFragment.BADGE_IMG_URL, badgeItem.imgUrl)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "congrats_unlock_dialog")
    }

    companion object {
        const val SELECTED_READS_ID = "selected_reads_id"
    }
}