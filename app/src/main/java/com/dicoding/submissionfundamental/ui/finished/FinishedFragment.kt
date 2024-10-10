package com.dicoding.submissionfundamental.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionfundamental.R
import com.dicoding.submissionfundamental.ui.home.FinishedEventsAdapter
import com.dicoding.submissionfundamental.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FinishedViewModel
    private lateinit var adapter: FinishedEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FinishedViewModel::class.java]
        setupRecyclerView()
        observeViewModel()

        viewModel.fetchFinishedEvents()

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.editTextSearch.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchEvents(query)
                } else {
                    viewModel.fetchFinishedEvents()
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = FinishedEventsAdapter { event ->
            navigateToEventDetail(event.id)
        }
        binding.recyclerViewFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FinishedFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_finishedFragment_to_eventDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}