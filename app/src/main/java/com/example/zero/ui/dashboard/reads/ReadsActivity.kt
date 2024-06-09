package com.example.zero.ui.dashboard.reads

import android.content.Context
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
import com.example.zero.ui.dashboard.ChiroPopupDialogFragment
import com.example.zero.ui.dashboard.CongratsPopupDialogFragment
import com.example.zero.ui.dashboard.DashboardFragment
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID
import com.example.zero.ui.dashboard.quiz.result.QuizResultDialogFragment


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

        binding.readsHeaderText.text = "Bacaan ${id+1}"

        readsViewModel.getReads(id)

        readsViewModel.completionCount.observe(this@ReadsActivity) { completionCount ->
            when (completionCount) {
                3 -> {
                    showBadgeDialog(
                        Badges(
                            id = 3,
                            title = "Selamat!",
                            desc = "Kamu Mendapat Badge ini karena menyelesaikan 3 Materi",
                            imgUrl = "https://i.ibb.co.com/Z80RGZZ/rank3.png",
                            isUnlocked = true
                        )
                    )
                }
                1 -> {
                    showBadgeDialog(
                        Badges(
                            id = 1,
                            title = "Selamat!",
                            desc = "Kamu Mendapat Badge ini karena menyelesaikan 1 Materi",
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

        val sharedPreferences = this.getSharedPreferences(
            READS_NAME_FIRST, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(READS_KEY_FIRST, true)

        if (isFirstTime) {
            showTutorial()
            // Update the flag to indicate that the tutorial has been shown
            with(sharedPreferences.edit()) {
                putBoolean(READS_KEY_FIRST, false)
                apply()
            }
        }
    }

    private fun showTutorial() {
        val dialog = ChiroPopupDialogFragment()
        val args = Bundle().apply {
            putString(ChiroPopupDialogFragment.CHIRO_POPUP_MSG, "Perhatikan beberapa menu di halaman ini ya")
        }
        dialog.arguments = args

        dialog.setOnDismissListener {
        }

        dialog.show(supportFragmentManager, "chiro_popup_dialog_from_reads")

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
        const val READS_NAME_FIRST = "READS_NAME_FIST"
        private const val READS_KEY_FIRST = "READS_KEY_FIRST"
    }
}