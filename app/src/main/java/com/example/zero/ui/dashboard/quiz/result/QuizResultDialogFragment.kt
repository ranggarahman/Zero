package com.example.zero.ui.dashboard.quiz.result

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.zero.data.Const.PATH_UID
import com.example.zero.data.Const.PATH_USERPOINTS
import com.example.zero.data.Const.PATH_USERS
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentAvatarNameChangeOverlayBinding
import com.example.zero.databinding.FragmentQuizResultDialogBinding
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.CORRECT_ANSWER_COUNT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.TIME_SPENT
import com.example.zero.ui.dashboard.quiz.QuizActivity.Companion.XP_ACQUIRED_COUNT
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class QuizResultDialogFragment : DialogFragment() {

    private var _binding: FragmentQuizResultDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val quizResultDialogViewModel by viewModels<QuizResultDialogViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultDialogBinding.inflate(inflater, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = arguments?.getInt(CORRECT_ANSWER_COUNT, 0)
        val xp = arguments?.getInt(XP_ACQUIRED_COUNT, 0)
        val time = arguments?.getString(TIME_SPENT)
        val quizId = arguments?.getInt(SELECTED_QUIZ_ID, 0)


        binding.textviewScore.text = result.toString()
        binding.textviewXP.text = xp.toString()
        binding.textviewTime.text = time

        quizResultDialogViewModel.setResult(
            quizId = quizId!!,
            resultPoints = xp!!
        )

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

    companion object {
        private const val TAG = "QRDF"
        const val SELECTED_QUIZ_ID = "selected_quiz_id"
    }

}