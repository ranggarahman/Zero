package com.example.zero.ui.dashboard.flashcard

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.FlashcardItem
import com.example.zero.databinding.ActivityFlashcardBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.dashboard.CongratsPopupDialogFragment
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID
import com.example.zero.ui.dashboard.reads.ReadsActivity

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private var currentFlashcardIndex = 0
    private lateinit var flashcards : List<FlashcardItem>
    private var materialId = 0

    private val flashcardViewModel by viewModels<FlashcardViewModel>()
    private var isEndReached = false
    private lateinit var front_animation:AnimatorSet
    private lateinit var back_animation: AnimatorSet
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.extras?.getInt(SELECTED_MATERIAL_ID) ?: 0
        materialId = id

        flashcardViewModel.getFlashcards(id)
        flashcardViewModel.flashcardList.observe(this@FlashcardActivity){
            flashcards = it

            // Update UI with initial flashcard
            updateFlashcardUI()

            val scale = applicationContext.resources.displayMetrics.density
            val front = binding.contentContainer
            val back = binding.contentContainerBack

            front.cameraDistance = 8000 * scale
            back.cameraDistance = 8000 * scale

            // Now we will set the front animation
            front_animation = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
            back_animation = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

            // Set click listener for flashcard content container
            binding.contentContainer.setOnClickListener {
                flipFlashcard(front, back)
            }

            // Set click listeners for navigation buttons
            binding.previousButton.setOnClickListener {
                previousFlashcard()
            }
            binding.nextButton.setOnClickListener {
                nextFlashcard()
            }
        }

        flashcardViewModel.completionCount.observe(this@FlashcardActivity) { completionCount ->
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
                            title = "Congrats!",
                            desc = "Kamu Mendapat Badge ini karena menyelesaikan 1 Materi",
                            imgUrl = "https://i.ibb.co.com/n1PQXHn/rank1.png",
                            isUnlocked = true
                        )
                    )
                }
            }
        }
    }

    private fun showBadgeDialog(badgeItem : Badges) {
        val dialog = CongratsPopupDialogFragment()
        val args = Bundle().apply {
            putString(BadgesFragment.BADGE_TITLE, badgeItem.title)
            putString(BadgesFragment.BADGE_DESC, badgeItem.desc)
            putString(BadgesFragment.BADGE_IMG_URL, badgeItem.imgUrl)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "congrats_unlock_dialog_from_flash")
    }

    private fun updateButtonState(index: Int) {
        val isFirstPage = index == 0
        val isLastPage = index == flashcards.size - 1
        binding.previousButton.isEnabled = !isFirstPage
        binding.nextButton.isEnabled = !isLastPage

        if (isLastPage && !isEndReached) {
            flashcardViewModel.setAchievement(materialId)
            isEndReached = true
            Toast.makeText(this, "LAST FLASH", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateFlashcardUI() {
        updateButtonState(currentFlashcardIndex)
        val currentFlashcard = flashcards[currentFlashcardIndex]
        binding.materialTitle.text = currentFlashcard.title
        binding.materialContent.text = currentFlashcard.content

        binding.materialContentBack.text = currentFlashcard.reveal
    }

    private fun flipFlashcard(front: LinearLayout, back: LinearLayout) {
        if(isFront)
        {
            front_animation.setTarget(front)
            back_animation.setTarget(back)
            front_animation.start()
            back_animation.start()
            isFront = false


        }
        else
        {
            front_animation.setTarget(back)
            back_animation.setTarget(front)
            back_animation.start()
            front_animation.start()
            isFront =true

        }
    }

    private fun nextFlashcard() {
        if (currentFlashcardIndex < flashcards.lastIndex) {
            currentFlashcardIndex++
            updateFlashcardUI()
        }
    }

    private fun previousFlashcard() {
        if (currentFlashcardIndex > 0) {
            currentFlashcardIndex--
            updateFlashcardUI()
        }
    }
}
