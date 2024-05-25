package com.example.zero.ui.dashboard.material

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.zero.R
import com.example.zero.databinding.FragmentDashboardBinding
import com.example.zero.databinding.FragmentMaterialBinding
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID
import com.example.zero.ui.dashboard.quiz.QuizActivity
import com.example.zero.ui.dashboard.quiz.brief.QuizBriefDialogFragment

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


}