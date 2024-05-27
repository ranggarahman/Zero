package com.example.zero.ui.dashboard.reads

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        readsViewModel.getReads(id)

        readsViewModel.reads.observe(this@ReadsActivity){
            val adapter = ReadsAdapter(it)

            binding.rvReads.adapter = adapter
            binding.rvReads.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvReads.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ( recyclerView.canScrollVertically(-1) &&
                    !recyclerView.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE) {

                    Toast.makeText(this@ReadsActivity, "END REACHED", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        const val SELECTED_READS_ID = "selected_reads_id"
    }
}