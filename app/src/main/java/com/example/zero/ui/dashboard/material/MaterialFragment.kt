package com.example.zero.ui.dashboard.material

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zero.R
import com.example.zero.databinding.FragmentMaterialBinding
import com.example.zero.ui.achievement.badges.BadgesFragment
import com.example.zero.ui.dashboard.ChiroPopupDialogFragment
import com.example.zero.ui.dashboard.ChiroPopupDialogFragment.Companion.CHIRO_POPUP_MSG
import com.example.zero.ui.dashboard.DashboardFragment
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID
import com.example.zero.ui.utils.CircleDimmedPromptBackground
import com.example.zero.ui.utils.RectDimmedPromptBackground
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

class MaterialFragment : Fragment() {

    private lateinit var viewModel: MaterialViewModel

    private var _binding: FragmentMaterialBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt(SELECTED_MATERIAL_ID) ?: 0

        buttonNavigator(id)

        val sharedPreferences = requireContext().getSharedPreferences(
            MTR_NAME_FIRST, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(MTR_KEY_FIRST_TIME, true)

        if (isFirstTime) {
            showTutorial()
            // Update the flag to indicate that the tutorial has been shown
            with(sharedPreferences.edit()) {
                putBoolean(MTR_KEY_FIRST_TIME, false)
                apply()
            }
        }

    }

    private fun showTutorial() {
        val dialog = ChiroPopupDialogFragment()
        val args = Bundle().apply {
            putString(CHIRO_POPUP_MSG, "Perhatikan beberapa menu di halaman ini ya")
        }
        dialog.arguments = args

        dialog.setOnDismissListener {
            showTapTargetPrompt()
        }

        dialog.show(parentFragmentManager, "chiro_popup_dialog")

    }

    private fun showTapTargetPrompt() {
        val customFont: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)
        val boldCustomFont : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.cvMtrReads)
            .setPrimaryText("Halaman Materi Bacaan")
            .setSecondaryText("Kamu bisa membaca bacaan tentang materi yang kamu pilih disini")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(boldCustomFont)
            .setSecondaryTextTypeface(customFont)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptFocal(RectanglePromptFocal()) // Optional: change the focal shape
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // Show the next prompt when the first one is dismissed
                    showSecondTapTargetPrompt()
                }
            }
            .show()
    }

    private fun showSecondTapTargetPrompt() {
        val customFont: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)
        val boldCustomFont : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.cvMtrFlash)
            .setPrimaryText("Halaman Flashcard")
            .setSecondaryText("Flashcard untuk menguji pengetahuanmu bisa diakses disini")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(boldCustomFont)
            .setSecondaryTextTypeface(customFont)
            .setTextSeparation(0.5f)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptFocal(RectanglePromptFocal()) // Optional: change the focal shape
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // The second prompt has been dismissed
                    showThirdTapTargetPrompt()
                }
            }
            .show()
    }

    private fun showThirdTapTargetPrompt() {
        val customFont: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)
        val boldCustomFont : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_bold)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.cvMtrQuiz)
            .setPrimaryText("Quiz")
            .setSecondaryText("Jika kamu merasa sudah paham, kamu bisa mengakses quiz")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(boldCustomFont)
            .setSecondaryTextTypeface(customFont)
            .setTextSeparation(0.5f)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptFocal(RectanglePromptFocal()) // Optional: change the focal shape
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // Show the next prompt when the first one is dismissed

                }
            }
            .show()
    }

    private fun buttonNavigator(id: Int) {
        val bundle = Bundle().apply {
            putInt(SELECTED_MATERIAL_ID, id) // "id" is the key used to pass the value
        }

        binding.materialBtnQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_materialFragment_to_quizActivity, bundle)
        }

        binding.materialBtnFlash.setOnClickListener {
            findNavController().navigate(R.id.action_materialFragment_to_flashcardActivity, bundle)
        }

        binding.materialBtnReads.setOnClickListener {
            findNavController().navigate(R.id.action_materialFragment_to_readsActivity, bundle)
        }
    }

    companion object {
        private const val MTR_KEY_FIRST_TIME = "MTR_KEY_FIRST_TIME"
        const val MTR_NAME_FIRST = "MTR_NAME_FIRST"
    }


}