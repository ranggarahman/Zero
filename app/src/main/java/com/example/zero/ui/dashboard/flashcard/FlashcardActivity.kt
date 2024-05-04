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
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.example.zero.R
import com.example.zero.data.FlashcardItem
import com.example.zero.databinding.ActivityFlashcardBinding

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private var currentFlashcardIndex = 0
    private val flashcards = generateDummyFlashcards() // Call the dummy data generator

    private val flashcardViewModel by viewModels<FlashcardViewModel>()

    private lateinit var front_animation:AnimatorSet
    private lateinit var back_animation: AnimatorSet
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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



//        when (currentFlashcardIndex) {
//            0 -> {
//                binding.previousButton.isEnabled = false
//                binding.nextButton.isEnabled = true // Enable next button when not at last index
//            }
//            flashcards.lastIndex -> {
//                binding.previousButton.isEnabled = true // Enable previous button when not at first index
//                binding.nextButton.isEnabled = false
//            }
//            else -> {
//                binding.previousButton.isEnabled = true
//                binding.nextButton.isEnabled = true
//            }
//        }


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

    private fun generateDummyFlashcards(): List<FlashcardItem> {
        // Replace this with your actual data generation logic
        return listOf(
            FlashcardItem(1, "Title 1", "Content 1", "Reveal Content 1"),
            FlashcardItem(2, "Title 2", "Content 2", "Reveal Content 2"),
            FlashcardItem(3, "Title 3", "Content 3", "Reveal Content 3")
        )
    }

    private fun updateButtonState(index: Int) {
        val isFirstPage = index == 0
        val isLastPage = index == flashcards.size - 1
        binding.previousButton.isEnabled = !isFirstPage
        binding.nextButton.isEnabled = !isLastPage
    }

    private fun updateFlashcardUI() {
        updateButtonState(currentFlashcardIndex)
        val currentFlashcard = flashcards[currentFlashcardIndex]
        binding.materialTitle.text = currentFlashcard.title
        binding.materialContent.text = currentFlashcard.content
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
