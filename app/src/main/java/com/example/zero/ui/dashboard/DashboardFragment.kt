package com.example.zero.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zero.R
import com.example.zero.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


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

        dashboardViewModel.materialList.observe(viewLifecycleOwner) { itemList ->
            val materialListAdapter = MaterialAdapter(itemList)
            binding.dashMaterialRv.adapter = materialListAdapter
            binding.dashMaterialRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            Log.d(TAG, "ITEMLIST : $itemList")

            materialListAdapter.setOnItemClickCallback(object :
                MaterialAdapter.OnItemClickCallback {
                override fun onItemClicked(id: Int) {
                    Toast.makeText(requireContext(), "ID IS : $id", Toast.LENGTH_LONG).show()
//                    val detailIntent = Intent(context, DetailActivity::class.java)
//                    detailIntent.putExtra(EXTRA_USERNAME, username)
//                    context.startActivity(detailIntent)
                }
            })
        }

        binding.btnChatroom.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_chatActivity)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "DASH_FRAG"
    }
}