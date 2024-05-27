package com.example.zero.ui.dashboard.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.zero.R
import com.example.zero.databinding.ActivityQuizBinding
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID
import com.example.zero.ui.dashboard.misc.ButtonState
import com.example.zero.ui.dashboard.quiz.brief.QuizBriefDialogFragment
import com.example.zero.ui.dashboard.quiz.result.QuizResultDialogFragment
import com.example.zero.ui.dashboard.quiz.result.QuizResultDialogFragment.Companion.SELECTED_QUIZ_ID
import com.example.zero.ui.dashboard.reads.ReadsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.concurrent.TimeUnit

class QuizActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizBinding

    private var startTime: Long = 0

    private val quizViewModel by viewModels<QuizViewModel>()

    private var currentQuestionIndex = 0
    private var questionList: List<Question> = emptyList()
    private var buttonState = ButtonState.CLICK_TOAST
    private var selectedMaterialId = 0
    private val answerCheck = "Cek Jawaban"
    private val nextQuestion = "Pertanyaan Selanjutnya"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.extras?.getInt(SELECTED_MATERIAL_ID) ?: 0
        selectedMaterialId = id

        val briefDialog = QuizBriefDialogFragment()
        briefDialog.show(supportFragmentManager, "brief_dialog")

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTime = System.currentTimeMillis()

        quizViewModel.getQuiz()

        quizViewModel.questionList.observe(this){

            Log.d(TAG, "CALLED BZD QUIZLIST : ${it}")

            questionList = it
            showNextQuestion(questionList)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Keluar Quiz")
            .setMessage("Apa kamu yakin ingin keluar dari Quiz? Semua Progress akan hilang.")
            .setPositiveButton("Ya") { _, _ ->
                // User clicked "Yes" button, finish the activity
                finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                // User clicked "No" button, dismiss the dialog
                dialog.dismiss()
            }
            .setCancelable(false) // Prevent dialog dismissal when clicking outside or pressing back button
            .setIcon(R.drawable.baseline_warning_24)

        val dialog = builder.create()
        dialog.show()
    }


    private fun showNextQuestion(questionList: List<Question>) {
        if (currentQuestionIndex < questionList.size) {

            val currentQuestion = questionList[currentQuestionIndex]
            questionMapper(currentQuestion)
            currentQuestionIndex++

            // Update progress bar
            val progressBar = binding.quizProgressBar
            progressBar.setProgress(currentQuestionIndex, true)
            progressBar.max = questionList.size

        } else {
            showQuizResult()
        }

    }

    private fun showQuizResult() {
        val levelMultiplier = 5

        // Calculate the elapsed time in milliseconds
        val elapsedTime = System.currentTimeMillis() - startTime

        // Calculate minutes and seconds
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        // Format the minutes and seconds as MM:SS
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        Toast.makeText(
            this,
            "All Questions Displayed!",
            Toast.LENGTH_SHORT
        ).show()
        quizViewModel.correctAnswerCount.observe(this) { correctAnswerCount ->
            val dialog = QuizResultDialogFragment()

            val args = Bundle().apply {
                putInt(CORRECT_ANSWER_COUNT, correctAnswerCount)
                putInt(XP_ACQUIRED_COUNT, correctAnswerCount*levelMultiplier)
                putString(TIME_SPENT, formattedTime)
                putInt(SELECTED_QUIZ_ID, selectedMaterialId)
            }

            dialog.arguments = args
            dialog.show(supportFragmentManager, "QA_result_dialog")
        }
    }


    private fun questionMapper(question: Question) {
        val slide = Slide(Gravity.END)
        slide.duration = 450 // Set the duration of the animation (in milliseconds)

        TransitionManager.beginDelayedTransition(binding.root as ViewGroup, slide)

        // Hide all layout containers initially
        binding.multiplechoiceLayout.mcqContainer.visibility = View.GONE
        binding.fillblankLayout.fillblankContainer.visibility = View.GONE
        binding.trueorfalseLayout.trueorfalseContainer.visibility = View.GONE

        // Show the appropriate layout container based on the question type
        when (question.type) {
            "MCQ" -> {
                multipleChoiceSetup(question)
                binding.multiplechoiceLayout.mcqContainer.visibility = View.VISIBLE
            }
            "FIB" -> {
                fillBlankSetup(question)
                binding.fillblankLayout.fillblankContainer.visibility = View.VISIBLE
            }
            "TF" -> {
                trueOrFalseSetup(question)
                binding.trueorfalseLayout.trueorfalseContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun trueOrFalseSetup(question: Question): Int {
        val layout = binding.trueorfalseLayout

        val questionTextView = layout.questionTextView
        val trueButton = layout.trueButton
        val falseButton = layout.falseButton
        val imageView = layout.trueorfalseImageview

        imageView.setImageResource(question.imgInt)

        questionTextView.text = question.questionText

        val trueFalseButtons = listOf(trueButton, falseButton)

        trueFalseButtons.forEach { button ->
            button.backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, R.color.white)
            val strokeColor = when (button) {
                trueButton -> R.color.correct_answer_green
                falseButton -> R.color.wrong_answer_red
                else -> R.color.white
            }
            button.setStrokeColorResource(strokeColor)
        }

        var selectedOption: MaterialButton? = null
        var selectedOptionCategory: String? = null

        for (button in trueFalseButtons){
            button.setOnClickListener { view ->
                selectedOption?.apply {
                    //Reset Appearance
                    backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, R.color.white)
                    val strokeColor = when (this) {
                        trueButton -> R.color.correct_answer_green
                        falseButton -> R.color.wrong_answer_red
                        else -> R.color.white
                    }
                    setStrokeColorResource(strokeColor)
                }

                //Change selected option appearance
                selectedOption = view as MaterialButton
                selectedOption?.apply {
                    val backgroundColor = when (this) {
                        trueButton -> R.color.correct_answer_green
                        falseButton -> R.color.wrong_answer_red
                        else -> R.color.white
                    }
                    backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, backgroundColor)
                    setStrokeColorResource(R.color.black)
                }

                selectedOptionCategory = when (button) {
                    binding.trueorfalseLayout.trueButton -> "True"
                    binding.trueorfalseLayout.falseButton -> "False"
                    else -> null
                }
            }
        }

        binding.btnNext.setOnClickListener {
            val correctAnswer = question.correctAnswer

            Log.d(TAG, "CORRECT ANSWER TRUEFALSE : $correctAnswer")

            if (selectedOption != null) {
                when (buttonState) {
                    ButtonState.CLICK_TOAST -> {
                        binding.btnNext.text = answerCheck
                        trueFalseButtons.forEach { button ->
                            button.isEnabled = false
                        }

                        val isAnswerCorrect = selectedOptionCategory == correctAnswer

                        if (isAnswerCorrect) {
                            Toast.makeText(this, "Yey! Jawaban Benar", Toast.LENGTH_SHORT).show()
                            quizViewModel.correctAnswerIterator()
                        } else {
                            Toast.makeText(this, "Yah :( Jawabanmu masih salah.", Toast.LENGTH_SHORT).show()
                        }

                        buttonState = ButtonState.CLICK_NEXT_QUESTION
                        binding.btnNext.text = nextQuestion
                    }

                    ButtonState.CLICK_NEXT_QUESTION -> {
                        showNextQuestion(questionList)
                        trueFalseButtons.forEach { button ->
                            button.isEnabled = true
                        }
                        buttonState = ButtonState.CLICK_TOAST
                        binding.btnNext.text = answerCheck
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Oops! Kamu belum memilih jawaban",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return View.VISIBLE
    }


    private fun fillBlankSetup(question: Question): Int {
        binding.fillblankLayout.editAnswer.text = null
        binding.fillblankLayout.textQuestion.text = question.questionText

        binding.fillblankLayout.filblankImageview.setImageResource(question.imgInt)

        binding.btnNext.setOnClickListener {
            if (!binding.fillblankLayout.editAnswer.text.isNullOrBlank()) {
                when (buttonState) {
                    ButtonState.CLICK_TOAST -> {
                        binding.btnNext.text = answerCheck
                        // Show "CORRECT" or "FALSE" toast
                        if (binding.fillblankLayout.editAnswer.text.toString().trim().lowercase() == question.correctAnswer.lowercase()) {
                            Toast.makeText(this, "Yey! Jawaban Benar", Toast.LENGTH_SHORT).show()
                            quizViewModel.correctAnswerIterator()
                        } else {
                            Toast.makeText(this, "Yah :( Jawabanmu masih salah", Toast.LENGTH_SHORT).show()
                        }

                        buttonState = ButtonState.CLICK_NEXT_QUESTION // Update the state
                        binding.btnNext.text = nextQuestion
                    }

                    ButtonState.CLICK_NEXT_QUESTION -> {
                        // Show next question
                        showNextQuestion(questionList)

                        buttonState = ButtonState.CLICK_TOAST // Update state
                        binding.btnNext.text = answerCheck
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Oops!, Kamu belum ada Jawaban",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return View.VISIBLE
    }

    private fun multipleChoiceSetup(question: Question): Int {
        binding.multiplechoiceLayout.mcqImageView.setImageResource(question.imgInt)
        binding.multiplechoiceLayout.questionTextView.text = question.questionText

        val optionButtons = listOf(
            binding.multiplechoiceLayout.optionAButton,
            binding.multiplechoiceLayout.optionBButton,
            binding.multiplechoiceLayout.optionCButton,
            binding.multiplechoiceLayout.optionDButton
        )
        val optionTexts = listOf(
            binding.multiplechoiceLayout.optionAText,
            binding.multiplechoiceLayout.optionBText,
            binding.multiplechoiceLayout.optionCText,
            binding.multiplechoiceLayout.optionDText
        )

        val optionsMap = optionButtons.zip(optionTexts).toMap()

        // Reset appearance and selection of all option buttons
        for ((button, text) in optionsMap) {
            button.isEnabled = true
            button.backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, R.color.white)
            button.strokeColor = resources.getColor(R.color.primary_variant_purple)
            text.setTextColor(ContextCompat.getColorStateList(this@QuizActivity, R.color.black))
        }

        question.mcqOptions?.let { options ->
            optionTexts.forEachIndexed { index, textView ->
                when (index) {
                    0 -> textView.text = options.option1
                    1 -> textView.text = options.option2
                    2 -> textView.text = options.option3
                    3 -> textView.text = options.option4
                    // Add more cases
                }
            }
        }

        var selectedOption: MaterialCardView? = null
        var selectedTextView: TextView? = null
        var selectedOptionCategory: String? = null

        for ((button, text) in optionsMap) {
            button.setOnClickListener {
                // Reset appearance of previously selected option
                selectedOption?.apply {
                    backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, R.color.white)
                    strokeColor = resources.getColor(R.color.primary_variant_purple)
                }
                selectedTextView?.apply {
                    setTextColor(Color.BLACK)
                }

                // Set appearance of the selected option
                button.backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, R.color.primary_variant_purple)
                button.strokeColor = resources.getColor(R.color.black)

                // Set text color to white for the selected option
                text.setTextColor(Color.WHITE)

                selectedOption = button
                selectedTextView = text

                selectedOptionCategory = when (button) {
                    binding.multiplechoiceLayout.optionAButton -> "a"
                    binding.multiplechoiceLayout.optionBButton -> "b"
                    binding.multiplechoiceLayout.optionCButton -> "c"
                    binding.multiplechoiceLayout.optionDButton -> "d"

                    else -> null
                }

            }
        }

        binding.btnNext.setOnClickListener {
            if (selectedOption != null) {
                val correctAnswer = question.correctAnswer
                when (buttonState) {
                    ButtonState.CLICK_TOAST -> {
                        binding.btnNext.text = answerCheck
                        // Disable all option buttons
                        optionButtons.forEach { button ->
                            button.isEnabled = false
                        }
                        if (selectedOptionCategory == correctAnswer) {
                            Toast.makeText(this, "Yey! Jawabanmu Benar", Toast.LENGTH_SHORT).show()
                            quizViewModel.correctAnswerIterator()

                            // Turn the selected button green
                            selectedOption?.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    this@QuizActivity,
                                    R.color.correct_answer_green
                                )

                        } else {
                            Toast.makeText(this, "Yah, jawabanmu masih salah :(", Toast.LENGTH_SHORT).show()

                            // Turn the selected button red
                            selectedOption?.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    this@QuizActivity,
                                    R.color.wrong_answer_red
                                )

                            // Find the button associated with the correct answer
                            val correctOptionButton = optionButtons.find { button ->
                                optionTexts[optionButtons.indexOf(button)].text == correctAnswer
                            }

                            // Turn the button associated with the correct answer green
                            correctOptionButton?.backgroundTintList =
                                ContextCompat.getColorStateList(
                                    this@QuizActivity,
                                    R.color.correct_answer_green
                                )
                        }

                        buttonState = ButtonState.CLICK_NEXT_QUESTION
                        binding.btnNext.text = nextQuestion

                        Log.d(TAG, "QUESTION : ${question.correctAnswer}, SELECTED = $selectedOptionCategory")
                    }

                    ButtonState.CLICK_NEXT_QUESTION -> {
                        showNextQuestion(questionList)

                        optionButtons.forEach { button ->
                            button.isEnabled = true
                        }

                        buttonState = ButtonState.CLICK_TOAST
                        binding.btnNext.text = answerCheck
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Oops! Kamu belum memilih Jawaban",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterNetworkReceiver()
    }

    override fun onResume() {
        super.onResume()
        //registerNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        //unregisterNetworkReceiver()
    }

//    private fun registerNetworkReceiver() {
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(networkConnectivityListener, intentFilter)
//    }
//
//    private fun unregisterNetworkReceiver() {
//        try {
//            unregisterReceiver(networkConnectivityListener)
//        } catch (e: IllegalArgumentException) {
//            // Receiver was not registered, ignore the exception
//        }
//    }
//
//    override fun onNetworkConnectionChanged(isConnected: Boolean) {
//        if (!isConnected) {
//            showNetworkDialog()
//        } else {
//            // Connection is restored, perform necessary actions here
//        }
//    }
//
//    private fun showNetworkDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Maaf... 😔")
//            .setMessage("Nusabasa tidak bisa berjalan dengan optimal tanpa koneksi internet. Periksa kembali jaringan kamu")
//            .setPositiveButton("Coba Lagi") { dialog, _ ->
//                // Retry logic
//                dialog.dismiss()
//                retryConnection()
//            }
//            .setNegativeButton("Keluar Dari Quiz") { dialog, _ ->
//                // User clicked "No" button, dismiss the dialog and finish the activity
//                dialog.dismiss()
//                finish()
//            }
//            .setCancelable(false) // Prevent dialog dismissal when clicking outside or pressing back button
//            .setIcon(R.drawable.ic_wifi_low)
//
//        val dialog = builder.create()
//        dialog.show()
//    }
//
//    private fun retryConnection() {
//        // Perform the necessary actions to check the connection again
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
//        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
//
//        if (isConnected) {
//            // Connection is restored, perform necessary actions here
//        } else {
//            showNetworkDialog()
//        }
//    }

    companion object {
        private const val TAG = "QuizActivity"
        const val CORRECT_ANSWER_COUNT = "correct_answer_count"
        const val XP_ACQUIRED_COUNT = "xp_acquired_count"
        const val TIME_SPENT = "time_spent"
    }
}