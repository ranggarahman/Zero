package com.example.zero.ui.dashboard

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.zero.databinding.FragmentChiroPopupBinding

class ChiroPopupDialogFragment : DialogFragment() {
    private var _binding: FragmentChiroPopupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChiroPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(CHIRO_POPUP_MSG)

        binding.chiroPopupMainText.text = title

        Toast.makeText(requireContext(), "TITLE $title", Toast.LENGTH_SHORT).show()

        //Glide.with(requireContext()).load(imgUrl).into(binding.iconImageView)

    }

    override fun onResume() {
        super.onResume()
        // Set the width and height of the dialog fragment to match_parent
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private var dismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    companion object {
        const val CHIRO_POPUP_MSG = "CHIRO_POPUP_MSG"
    }
}