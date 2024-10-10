package com.dicoding.submissionfundamental.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionfundamental.R
import com.dicoding.submissionfundamental.databinding.FragmentUpcomingBinding
import com.dicoding.submissionfundamental.ui.home.UpcomingEventsAdapter

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UpcomingViewModel
    private lateinit var adapter: UpcomingEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[UpcomingViewModel::class.java]
        setupRecyclerView()
        observeViewModel()

        viewModel.fetchActiveEvents()
    }

    private fun setupRecyclerView() {
        adapter = UpcomingEventsAdapter { event ->
            navigateToEventDetail(event.id)
        }
        binding.recyclerViewUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@UpcomingFragment.adapter
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_upcomingFragment_to_eventDetailFragment, bundle)
    }

    private fun observeViewModel() {
        viewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}