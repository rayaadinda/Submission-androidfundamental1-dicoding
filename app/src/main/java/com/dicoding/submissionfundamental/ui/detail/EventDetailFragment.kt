package com.dicoding.submissionfundamental.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dicoding.submissionfundamental.R
import com.dicoding.submissionfundamental.data.EventRepository
import com.dicoding.submissionfundamental.data.local.AppDatabase
import com.dicoding.submissionfundamental.data.response.ListEventsItem
import com.dicoding.submissionfundamental.data.retrofit.ApiConfig
import com.dicoding.submissionfundamental.databinding.FragmentEventDetailBinding


class EventDetailFragment : Fragment() {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels {
        ViewModelFactory(
            EventRepository(
                ApiConfig.getApiService(),
                AppDatabase.getDatabase(requireContext()).favoriteEventDao()
            )
        )
    }

    private var currentEvent: ListEventsItem? = null
    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt(ARG_EVENT_ID) ?: -1
        if (eventId != -1) {
            Log.d("EventDetailFragment", "Fetching event with ID: $eventId")
            viewModel.getEventDetail(eventId)
        } else {
            Log.e("EventDetailFragment", "No event ID provided")
            showError(getString(R.string.log_id))
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { event ->
            event?.let {
                Log.d("EventDetailFragment", "Received event: $it")
                currentEvent = it
                displayEventDetails(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { showError(it) }
        }
    }

    private fun showError(message: String) {
        with(binding) {
            textEventName.text = message
            textEventOrganizer.visibility = View.GONE
            textEventTime.visibility = View.GONE
            textEventQuota.visibility = View.GONE
            textEventDescription.visibility = View.GONE
            buttonRegister.visibility = View.GONE
            buttonAddToFavorite.visibility = View.GONE
        }
    }

    private fun parseHtmlDescription(htmlString: String): Spanned {
        return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: ListEventsItem) {
        with(binding) {
            Glide.with(this@EventDetailFragment)
                .load(event.imageLogo)
                .into(imageEvent)

            textEventName.text = event.name
            textEventOrganizer.text = event.ownerName
            textEventTime.text = event.beginTime
            textEventQuota.text = "Available: ${event.quota - event.registrants}"
            textEventDescription.text = parseHtmlDescription(event.description)

            buttonRegister.setOnClickListener {
                openRegistrationLink(event.link)
            }

            checkIfFavorite(event.id)

            buttonAddToFavorite.setOnClickListener {
                if (isFavorite) {
                    viewModel.removeFromFavorites(event.id)
                } else {
                    viewModel.addToFavorites(event)
                }
                checkIfFavorite(event.id)
            }
        }
    }

    private fun checkIfFavorite(eventId: Int) {
        viewModel.isFavorite(eventId).observe(viewLifecycleOwner) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteButton()
        }
    }

    private fun updateFavoriteButton() {
        binding.buttonAddToFavorite.text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
    }

    private fun openRegistrationLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        private const val ARG_EVENT_ID = "eventId"

        fun newInstance(eventId: Int): EventDetailFragment {
            return EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_EVENT_ID, eventId)
                }
            }
        }
    }
}