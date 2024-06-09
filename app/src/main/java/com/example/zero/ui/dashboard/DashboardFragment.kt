package com.example.zero.ui.dashboard

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.data.Const.getCurrentStreak
import com.example.zero.databinding.FragmentDashboardBinding
import com.example.zero.ui.LoaderOverlay
import com.example.zero.ui.dashboard.ChiroPopupDialogFragment.Companion.CHIRO_POPUP_MSG
import com.example.zero.ui.utils.CircleDimmedPromptBackground
import com.example.zero.ui.utils.RectDimmedPromptBackground
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val dialog = LoaderOverlay()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        // Get the current streak count
        val streakCount = getCurrentStreak(requireContext())
        // Check if this is the first time opening the app
        val sharedPreferences = requireContext().getSharedPreferences(
            DASH_PREFS_NAME_FIRST_TIME_TUTORIAL, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(DASH_KEY_FIRST_TIME_TUTORIAL, true)

        if (isFirstTime) {
            showTutorial()
            // Update the flag to indicate that the tutorial has been shown
            with(sharedPreferences.edit()) {
                putBoolean(DASH_KEY_FIRST_TIME_TUTORIAL, false)
                apply()
            }
        }

        binding.dashCardOne.dashCardStreakText.text =
            getString(R.string.text_streaK_days, streakCount.toString())

        // Inside your observer
        dashboardViewModel.loading.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                binding.shimmerLayout.startShimmer()
                binding.dashMaterialRv.visibility = View.GONE

                // Constraint to bottom of shimmerLayout
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.dashParentConstriantConteiner) // assuming the parent layout is ConstraintLayout
                constraintSet.connect(
                    R.id.header_chatroom,
                    ConstraintSet.TOP,
                    R.id.shimmerLayout,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(binding.dashParentConstriantConteiner)
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.dashMaterialRv.visibility = View.VISIBLE

                // Constraint to bottom of dash_material_rv
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.dashParentConstriantConteiner) // assuming the parent layout is ConstraintLayout
                constraintSet.connect(
                    R.id.header_chatroom,
                    ConstraintSet.TOP,
                    R.id.dash_material_rv,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(binding.dashParentConstriantConteiner)
            }
        }

        dashboardViewModel.username.observe(viewLifecycleOwner){
            binding.headerUsername.text =
                getString(R.string.header_username, it)
        }

        dashboardViewModel.materialList.observe(viewLifecycleOwner) { itemList ->
            val materialListAdapter = MaterialAdapter(itemList, binding.dashMaterialRv)
            binding.dashMaterialRv.isNestedScrollingEnabled = true
            binding.dashMaterialRv.adapter = materialListAdapter
            binding.dashMaterialRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            Log.d(TAG, "ITEMLIST : $itemList")

            materialListAdapter.setOnItemClickCallback(object :
                MaterialAdapter.OnItemClickCallback {
                override fun onItemClicked(id: Int) {
                    val bundle = Bundle().apply {
                        putInt(SELECTED_MATERIAL_ID, id) // "id" is the key, and `id` is the value being passed
                    }

                    findNavController().navigate(R.id.action_navigation_dashboard_to_materialFragment, bundle)
                    //Toast.makeText(requireContext(), "ID IS : $id", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.btnChatroom.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_chatActivity)
        }

    }

    private fun showTutorial() {
        val dialog = ChiroPopupDialogFragment()
        val args = Bundle().apply {
            putString(CHIRO_POPUP_MSG, "Halo! Aku Chiro, aku juga sedang belajar CDSS seperti kamu. Aku bakal bantu kamu mengenal aplikasi ini yaa!")
        }
        dialog.arguments = args

        dialog.setOnDismissListener {
            showTapTargetPrompt()
        }

        dialog.show(parentFragmentManager, "chiro_popup_dialog_from_dash")

    }

    private fun showTapTargetPrompt() {
        val customFont: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.jktsans_regular)

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.dashCardOne.dashCardStreakText)
            .setPrimaryText("Ini adalah streak counter")
            .setSecondaryText("Kamu bisa menambah streak dengan membuka aplikasi ini setiap hari")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(customFont)
            .setSecondaryTextTypeface(customFont)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(CircleDimmedPromptBackground())
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

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.dashMaterialRv)
            .setPrimaryText("Ini adalah daftar materi yang tersedia")
            .setSecondaryText("Geser ke kanan dan kiri untuk melihat yang tersedia")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(customFont)
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

        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(binding.btnChatroom)
            .setPrimaryText("Mengakses Ruang Chat")
            .setSecondaryText("Kamu bisa berinteraksi dengan pengguna lain disini")
            .setPrimaryTextColour(resources.getColor(R.color.black))
            .setSecondaryTextColour(resources.getColor(R.color.black))
            .setPrimaryTextTypeface(customFont)
            .setSecondaryTextTypeface(customFont)
            .setBackgroundColour(resources.getColor(R.color.green_1))
            .setPromptBackground(RectDimmedPromptBackground())
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    // Show the next prompt when the first one is dismissed

                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DASH_PREFS_NAME_FIRST_TIME_TUTORIAL = "DASH_PREFS_NAME_FIRST_TIME_TUTORIAL"
        private const val DASH_KEY_FIRST_TIME_TUTORIAL = "DASH_KEY_FIRST_TIME_TUTORIAL"
        private const val TAG = "DASH_FRAG"
        const val SELECTED_MATERIAL_ID = "SELECTED_MATERIAL_ID"
    }
}