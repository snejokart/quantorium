package com.example.quantorium.activities

import FragmentSchedule
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.quantorium.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityFragment : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar // Declare Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private val prefName = "ThemePref"
    private val themeKey = "isDarkMode"

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(themeKey, false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        toolbar = findViewById(R.id.toolbar) // Find Toolbar
        setSupportActionBar(toolbar) // Set toolbar as action bar
        supportActionBar?.setDisplayShowTitleEnabled(true) // Enable title display

        bottomNavigationView = findViewById(R.id.bottom_nav)

        val news = FragmentNews()
        val schedule = FragmentSchedule()
        val settings = FragmentSettings()
        val profile = FragmentProfileStudent()

        setCurrentFragment(news, "Новости") // Set initial fragment with title

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.btn_news -> setCurrentFragment(news, "Новости")
                R.id.btn_schedule -> setCurrentFragment(schedule, "Расписание")
                R.id.btn_settings -> setCurrentFragment(settings, "Настройки")
                R.id.btn_profile -> setCurrentFragment(profile, "Профиль")
                else -> false // Handle unexpected item IDs
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragment)
            commit()
        }
        supportActionBar?.title = title // Update the title in the toolbar
    }
}