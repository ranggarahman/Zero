package com.example.zero

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zero.data.Const.updateStreak
import com.example.zero.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        setSupportActionBar(binding.maintoolbar)

        binding.maintoolbar.visibility = View.GONE

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        binding.maintoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        updateStreak(this)

        binding.maintoolbar.setTitleTextColor(getColor(R.color.white))
        binding.maintoolbar.navigationIcon?.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_achievement,  R.id.navigation_profile
            )
        )

        // Assuming you have already initialized navController and appBarConfiguration

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in appBarConfiguration.topLevelDestinations) {
                binding.maintoolbar.visibility = View.GONE
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.maintoolbar.visibility = View.VISIBLE
                binding.navView.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




}