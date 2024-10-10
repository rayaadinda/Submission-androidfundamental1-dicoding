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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.submissionfundamental.databinding.FragmentEventDetailBinding
import com.dicoding.submissionfundamental.data.response.ListEventsItem


class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EventDetailViewModel

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

        viewModel = ViewModelProvider(this)[EventDetailViewModel::class.java]

        val eventId = arguments?.getInt("eventId") ?: -1
        if (eventId != -1) {
            Log.d("EventDetailFragment", "Fetching event with ID: $eventId")
            viewModel.getEventDetail(eventId)
        } else {
            Log.e("EventDetailFragment", "No event ID provided")
            showError("@string/log_id")
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                Log.d("EventDetailFragment", "Received event: $event")
                displayEventDetails(event)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                showError(errorMessage)
            }
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
        }
    }


    private fun parseHtmlDescription(htmlString: String): Spanned {
        return(
                Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
                )

    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: ListEventsItem) {
        with(binding) {
            val imageUrl = event.imageLogo
            Glide.with(this@EventDetailFragment)
                .load(imageUrl)
                .into(imageEvent)

            textEventName.text = event.name
            textEventOrganizer.text = event.ownerName
            textEventTime.text = event.beginTime
            textEventQuota.text = "Available: ${event.quota - event.registrants}"
            val parsedDescription = parseHtmlDescription(event.description )
            textEventDescription.text = parsedDescription

            buttonRegister.setOnClickListener {
                openRegistrationLink(event.link)
            }
        }
    }

    private fun openRegistrationLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}