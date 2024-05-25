package com.example.zero.ui.dashboard.reads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.zero.R
import com.example.zero.databinding.ActivityReadsBinding
import com.example.zero.ui.dashboard.DashboardFragment.Companion.SELECTED_MATERIAL_ID

class ReadsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadsBinding

    private val readsViewModel by viewModels<ReadsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the id from the intent extras
        val id = intent.extras?.getInt(SELECTED_MATERIAL_ID) ?: 0

        binding.scrollContainer.setOnBottomReachedListener {
            Toast.makeText(this@ReadsActivity, "YOU HAVE REACHED THE END", Toast.LENGTH_SHORT).show()
            readsViewModel.setResult(id)
        }
    }

    private fun ScrollView.setOnBottomReachedListener(onBottomReached: () -> Unit) {
        this.viewTreeObserver.addOnScrollChangedListener {
            val view = this.getChildAt(this.childCount - 1) as View
            val diff = view.bottom - (this.height + this.scrollY)

            if (diff <= 0) {
                onBottomReached()
            }
        }
    }


    companion object {
        const val SELECTED_READS_ID = "selected_reads_id"
    }
}