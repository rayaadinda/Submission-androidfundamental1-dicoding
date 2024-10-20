package com.dicoding.submissionfundamental.ui.settings

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submissionfundamental.R
import com.dicoding.submissionfundamental.databinding.FragmentSettingsBinding
import com.dicoding.submissionfundamental.ui.theme.ThemeViewModel
import com.dicoding.submissionfundamental.ui.theme.ThemeViewModelFactory
import com.dicoding.submissionfundamental.utils.ReminderManager
import com.dicoding.submissionfundamental.utils.ThemePreferences
import com.dicoding.submissionfundamental.utils.dataStore

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeViewModel: ThemeViewModel
    private lateinit var reminderManager: ReminderManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("SettingsFragment", "Notification permission granted")
            scheduleReminder()
        } else {
            Log.d("SettingsFragment", "Notification permission denied")
            binding.switchReminder.isChecked = false
            Toast.makeText(requireContext(), "Permission denied. Cannot set reminder.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = ThemePreferences.getInstance(requireContext().dataStore)
        themeViewModel = ViewModelProvider(this, ThemeViewModelFactory(pref))[ThemeViewModel::class.java]
        reminderManager = ReminderManager(requireContext())

        themeViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchTheme.isChecked = isDarkModeActive
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            themeViewModel.saveThemeSetting(isChecked)
        }

        binding.switchReminder.isChecked = reminderManager.getReminderState()
        Log.d("SettingsFragment", "Initial reminder state: ${binding.switchReminder.isChecked}")

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsFragment", "Reminder switch changed to: $isChecked")
            if (isChecked) {
                handleReminderToggle()
            } else {
                cancelReminder()
            }
        }

        binding.buttonTestNotification.setOnClickListener {
            showTestNotification()
        }
    }

    private fun handleReminderToggle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("SettingsFragment", "Notification permission already granted")
                    scheduleReminder()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d("SettingsFragment", "Should show permission rationale")
                    showNotificationPermissionRationale()
                }
                else -> {
                    Log.d("SettingsFragment", "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d("SettingsFragment", "Android version < 13, scheduling reminder without permission check")
            scheduleReminder()
        }
    }

    private fun showNotificationPermissionRationale() {
        Toast.makeText(requireContext(), "Notification permission is required for reminders", Toast.LENGTH_LONG).show()
    }


    private fun scheduleReminder() {
        Log.d("SettingsFragment", "Scheduling reminder")
        reminderManager.scheduleReminder()
        Toast.makeText(requireContext(), "Daily reminder scheduled", Toast.LENGTH_SHORT).show()
    }

    private fun cancelReminder() {
        Log.d("SettingsFragment", "Cancelling reminder")
        reminderManager.cancelReminder()
        Toast.makeText(requireContext(), "Daily reminder cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun showTestNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Test Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Upcoming Event")
            .setContentText("This is a test notification from your app")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CHANNEL_ID = "TestNotificationChannel"
        private const val NOTIFICATION_ID = 1
    }
}