package com.example.quantorium.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.quantorium.R

class FragmentSettings : Fragment() {

    private lateinit var radioGroupTheme: android.widget.RadioGroup
    private lateinit var radioButtonLight: android.widget.RadioButton
    private lateinit var radioButtonDark: android.widget.RadioButton
    private lateinit var sharedPreferences: SharedPreferences
    private val prefName = "ThemePref"
    private val themeKey = "isDarkMode"
    private lateinit var versionTextView: android.widget.TextView
    private lateinit var url_vk: android.widget.LinearLayout
    private lateinit var url_website: android.widget.LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        versionTextView = view.findViewById(R.id.version_app)

        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionName = packageInfo.versionName
            versionTextView.text = "Версия приложения: $versionName"
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            versionTextView.text = "Версия приложения: Не удалось получить"
        }

        url_vk = view.findViewById(R.id.vk_url)
        url_vk.setOnClickListener {
            val url = "https://vk.com/g4_news"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            startActivity(intent)
        }

        url_website = view.findViewById(R.id.web_site_url)
        url_website.setOnClickListener {
            val url = "https://gimnazium4.gosuslugi.ru/"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            startActivity(intent)
        }

        radioGroupTheme = view.findViewById(R.id.radioGroupTheme)
        radioButtonLight = view.findViewById(R.id.radioButtonLight)
        radioButtonDark = view.findViewById(R.id.radioButtonDark)
        sharedPreferences = requireContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)

        // Load theme from SharedPreferences
        val isDarkMode = sharedPreferences.getBoolean(themeKey, false)
        if (isDarkMode) {
            radioButtonDark.isChecked = true
        } else {
            radioButtonLight.isChecked = true
        }
        applyTheme(isDarkMode)

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val isDarkModeSelected = checkedId == R.id.radioButtonDark
            saveThemePreference(isDarkModeSelected)
            applyTheme(isDarkModeSelected)
        }

        return view
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(themeKey, isDarkMode)
            apply()
        }
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            requireActivity().window.statusBarColor = Color.BLACK // или другой цвет для темного режима
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            requireActivity().window.decorView.systemUiVisibility = 0
            requireActivity().window.statusBarColor = Color.WHITE // или другой цвет для светлого режима
        }
    }
}