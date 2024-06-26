package com.example.zero.ui.dashboard.quiz.result

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.zero.R
import com.example.zero.data.Badges
import com.example.zero.data.Const
import com.example.zero.data.Const.PATH_UID
import com.example.zero.data.Const.PATH_USERPOINTS
import com.example.zero.data.Const.PATH_USERS
import com.example.zero.data.FirebaseManager
import com.example.zero.databinding.FragmentAvatarNameChangeOverlayBinding
import com.example.zero.databinding.FragmentQuizResultDialogBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.dashboard.ChiroPopupDialogFragment
import com.example.zero.ui.dashboard.CongratsPopupDialogFragment
import com.example.zero.ui.dashboard.DashboardFragment
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

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.winner)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }

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

        quizResultDialogViewModel.completionCount.observe(viewLifecycleOwner) { completionCount ->
            Log.d(TAG, "COMPLETION COUNT : $completionCount")
            if (completionCount == 1){
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
            if (completionCount == 3){
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
        }

        // Check if this is the first time quiz
        val sharedPreferences = requireContext().getSharedPreferences(
            QRDF_NAME_FIST_QUIZ, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(QRDF_KEY_FIRST_QUIZ, true)

        if (isFirstTime) {
            showTutorial()
            // Update the flag to indicate that the tutorial has been shown
            with(sharedPreferences.edit()) {
                putBoolean(QRDF_KEY_FIRST_QUIZ, false)
                apply()
            }
        }

        binding.resultConfirm.setOnClickListener {
            activity?.finish()
        }

    }

    private fun showTutorial() {
        val dialog = ChiroPopupDialogFragment()
        val args = Bundle().apply {
            putString(ChiroPopupDialogFragment.CHIRO_POPUP_MSG, "Hore! Kamu menyelesaikan quiz pertamamu! Sekarang kamu sudah paham tentang Dasar dari CDSS. Yuk ke materi selanjutnya!")
        }
        dialog.arguments = args

        dialog.setOnDismissListener {
            //showTapTargetPrompt()
        }

        dialog.show(parentFragmentManager, "chiro_popup_dialog_from_quiz")

    }

    private fun showBadgeDialog(badgeItem : Badges) {
        val dialog = CongratsPopupDialogFragment()
        val args = Bundle().apply {
            putString(BadgesFragment.BADGE_TITLE, badgeItem.title)
            putString(BadgesFragment.BADGE_DESC, badgeItem.desc)
            putString(BadgesFragment.BADGE_IMG_URL, badgeItem.imgUrl)
        }
        dialog.arguments = args
        dialog.show(parentFragmentManager, "congrats_unlock_dialog_from_quiz")
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
        private const val QRDF_NAME_FIST_QUIZ = "QRDF_NAME_FIST_QUIZ"
        private const val QRDF_KEY_FIRST_QUIZ = "QRDF_KEY_FIRST_QUIZ"
    }

}