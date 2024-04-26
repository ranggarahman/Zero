package com.example.zero.ui.dashboard.quiz.result

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.zero.databinding.FragmentQuizResultDialogBinding
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.CORRECT_ANSWER_COUNT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.TIME_SPENT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.XP_ACQUIRED_COUNT

class QuizResultDialogFragment : DialogFragment() {

    private val binding by lazy {
        FragmentQuizResultDialogBinding.inflate(layoutInflater)
    }

    private val quizResultDialogViewModel by viewModels<QuizResultDialogViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        isCancelable = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = arguments?.getInt(CORRECT_ANSWER_COUNT, 0)
        val xp = arguments?.getInt(XP_ACQUIRED_COUNT, 0)
        val time = arguments?.getString(TIME_SPENT)

        binding.textviewScore.text = result.toString()
        binding.textviewXP.text = xp.toString()
        binding.textviewTime.text = time

/*        quizResultDialogViewModel.submitXP(xp ?: 0)

        quizResultDialogViewModel.isSubmitSuccess.observe(viewLifecycleOwner){
            binding.resultConfirm.isEnabled = it
        }*/

        binding.resultConfirm.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

}