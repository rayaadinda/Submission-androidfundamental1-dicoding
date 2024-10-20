package com.dicoding.submissionfundamental

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.submissionfundamental.databinding.ActivityMainBinding
import com.dicoding.submissionfundamental.ui.theme.ThemeViewModel
import com.dicoding.submissionfundamental.ui.theme.ThemeViewModelFactory
import com.dicoding.submissionfundamental.utils.ThemePreferences
import com.dicoding.submissionfundamental.utils.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = ThemePreferences.getInstance(dataStore)
        themeViewModel =
            ViewModelProvider(this, ThemeViewModelFactory(pref))[ThemeViewModel::class.java]

        themeViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)


        
    }
    
    

}