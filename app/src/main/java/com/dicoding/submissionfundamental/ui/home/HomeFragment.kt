package com.dicoding.submissionfundamental.ui.home

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
import com.dicoding.submissionfundamental.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var upcomingEventsAdapter: UpcomingEventsAdapter
    private lateinit var finishedEventsAdapter: FinishedEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()
        observeViewModel()

        viewModel.fetchEvents()
    }

    private fun setupRecyclerViews() {
        upcomingEventsAdapter = UpcomingEventsAdapter { event ->
            navigateToEventDetail(event.id)
        }
        binding.carouselUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingEventsAdapter
        }

        finishedEventsAdapter = FinishedEventsAdapter { event ->
            navigateToEventDetail(event.id)
        }
        binding.listFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedEventsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            upcomingEventsAdapter.submitList(events)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventsAdapter.submitList(events)
        }

        viewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarUpcoming.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarFinished.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_homeFragment_to_eventDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}