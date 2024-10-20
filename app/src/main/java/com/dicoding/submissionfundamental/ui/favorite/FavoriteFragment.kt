package com.dicoding.submissionfundamental.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionfundamental.data.EventRepository
import com.dicoding.submissionfundamental.data.local.AppDatabase
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import com.dicoding.submissionfundamental.databinding.FragmentFavoriteBinding
import com.dicoding.submissionfundamental.ui.detail.ViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory(
            EventRepository(
                ApiConfig.getApiService(),
                AppDatabase.getDatabase(requireContext()).favoriteEventDao()
            )
        )
    }

    private lateinit var adapter: FavoriteEventsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        adapter = FavoriteEventsAdapter(
            onItemClick = { favoriteEvent ->
                navigateToEventDetail(favoriteEvent.id)
            },
            onRemoveClick = { favoriteEvent ->
                viewModel.removeFromFavorites(favoriteEvent.id)
            }
        )
        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoriteFragment.adapter
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToEventDetailFragment(eventId)
        findNavController().navigate(action)
    }

    private fun observeFavorites() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites)
            binding.textNoFavorites.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}