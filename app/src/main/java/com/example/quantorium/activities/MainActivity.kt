package com.example.quantorium.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.quantorium.data.AuthManager
import com.example.quantorium.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences
    private val prefName = "ThemePref"
    private val themeKey = "isDarkMode"

    private lateinit var authManager: AuthManager
    private val auth_token = "auth"


    override fun onCreate(savedInstanceState: Bundle?) {

        authManager = AuthManager(this)

        sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val isDarkModeSaved = sharedPreferences.contains(themeKey)

        if (!isDarkModeSaved) {
            // First launch: force light theme and save preference
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPreferences.edit().putBoolean(themeKey, false).apply() // Save "light theme" as the preference
        } else {
            // Subsequent launches: load from preferences
            val isDarkMode = sharedPreferences.getBoolean(themeKey, false)
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Изначально скрыть элементы
        binding.titleStart.visibility = View.INVISIBLE
        binding.addressStart.visibility = View.INVISIBLE
        binding.layout.visibility = View.INVISIBLE
        binding.logoIcon.visibility = View.INVISIBLE
        binding.helloStart.visibility = View.INVISIBLE
        binding.descStart.visibility = View.INVISIBLE
        binding.noAccStart.visibility = View.INVISIBLE
        binding.btnStart.visibility = View.INVISIBLE

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)

        val token_auth = getSharedPreferences(auth_token, Context.MODE_PRIVATE)
        val token = token_auth.getString("token", null)

        if (isFirstLaunch && token_auth.toString().isEmpty()) {
            // Первый запуск — показываем всю анимацию
            startLogoAnimation()
            // Сохраняем, что пользователь запустил приложение впервые
            prefs.edit().putBoolean("is_first_launch", false).apply()
        } else {
            // Не первый запуск — запускаем анимацию до hideTextViewsAndShrinkLogo,
            //если будет токен, то переход на новую страницу, иначе на авторизацию
            startLogoAnimationForReturningUser()
        }

        binding.btnStart.setOnClickListener {
            val int = Intent(this@MainActivity, Auth::class.java)
            startActivity(int)
            overridePendingTransition(0,0)
        }
    }

    private fun startLogoAnimationForReturningUser() {
        binding.logoIcon.visibility = View.VISIBLE

        binding.logoIcon.post {
            val startX = -binding.logoIcon.width.toFloat() - binding.logoIcon.left.toFloat() - 100f
            binding.logoIcon.translationX = startX
            binding.logoIcon.scaleX = 0.5f
            binding.logoIcon.scaleY = 0.5f

            val moveIn = ObjectAnimator.ofFloat(binding.logoIcon, "translationX", startX, 0f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val rotate = ObjectAnimator.ofFloat(binding.logoIcon, "rotation", 0f, 360f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleUpX = ObjectAnimator.ofFloat(binding.logoIcon, "scaleX", 0.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val scaleUpY = ObjectAnimator.ofFloat(binding.logoIcon, "scaleY", 0.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(moveIn, rotate, scaleUpX, scaleUpY)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.logoIcon.requestLayout()

                        Handler(Looper.getMainLooper()).postDelayed({
                            showTextViewsForReturningUser()
                        }, 1000)
                    }
                })
                start()
            }
        }
    }

    private fun showTextViewsForReturningUser() {
        binding.titleStart.visibility = View.VISIBLE
        binding.addressStart.visibility = View.VISIBLE

        val fadeIn1 = ObjectAnimator.ofFloat(binding.titleStart, "alpha", 0f, 1f).apply { duration = 500 }
        val fadeIn2 = ObjectAnimator.ofFloat(binding.addressStart, "alpha", 0f, 1f).apply { duration = 500 }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        hideTextViewsAndGoToActivityFragment()
                    }, 2000)
                }
            })
            start()
        }
    }

    private fun hideTextViewsAndGoToActivityFragment() {
        val fadeOut1 = ObjectAnimator.ofFloat(binding.titleStart, "alpha", 1f, 0f).apply { duration = 500 }
        val fadeOut2 = ObjectAnimator.ofFloat(binding.addressStart, "alpha", 1f, 0f).apply { duration = 500 }

        AnimatorSet().apply {
            playTogether(fadeOut1, fadeOut2)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.titleStart.visibility = View.INVISIBLE
                    binding.addressStart.visibility = View.INVISIBLE
                    val prefs = getSharedPreferences(auth_token, Context.MODE_PRIVATE)
                    val token = prefs.getString("token", null)
                    if (token != null && token.isNotEmpty()) {
                        // Токен есть, переходим к ActivityFragment
                        val intent = Intent(this@MainActivity, ActivityFragment::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Токена нет, переходим к AuthActivity
                        val intent = Intent(this@MainActivity, Auth::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })
            start()
        }
    }

    // --- Далее идут ваши уже реализованные методы анимации для первого запуска ---

    private fun startLogoAnimation() {
        binding.logoIcon.visibility = View.VISIBLE

        // Логотип изначально в позиции слева (сдвинут) и с масштабом 0.5
        binding.logoIcon.post {
            val startX = -binding.logoIcon.width.toFloat() - binding.logoIcon.left.toFloat() - 100f
            binding.logoIcon.translationX = startX
            binding.logoIcon.scaleX = 0.5f
            binding.logoIcon.scaleY = 0.5f

            val moveIn = ObjectAnimator.ofFloat(binding.logoIcon, "translationX", startX, 0f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val rotate = ObjectAnimator.ofFloat(binding.logoIcon, "rotation", 0f, 360f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleUpX = ObjectAnimator.ofFloat(binding.logoIcon, "scaleX", 0.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val scaleUpY = ObjectAnimator.ofFloat(binding.logoIcon, "scaleY", 0.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(moveIn, rotate, scaleUpX, scaleUpY)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Не меняем layoutParams! Размер фиксируем в layout
                        Handler(Looper.getMainLooper()).postDelayed({
                            showTextViews()
                        }, 1000)
                    }
                })
                start()
            }
        }
    }


    private fun showTextViews() {
        binding.titleStart.visibility = View.VISIBLE
        binding.addressStart.visibility = View.VISIBLE

        val fadeIn1 = ObjectAnimator.ofFloat(binding.titleStart, "alpha", 0f, 1f).apply { duration = 500 }
        val fadeIn2 = ObjectAnimator.ofFloat(binding.addressStart, "alpha", 0f, 1f).apply { duration = 500 }

        AnimatorSet().apply {
            playTogether(fadeIn1, fadeIn2)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        hideTextViewsAndShrinkLogo()
                    }, 2000)
                }
            })
            start()
        }
    }

    private fun hideTextViewsAndShrinkLogo() {
        val fadeOut1 = ObjectAnimator.ofFloat(binding.titleStart, "alpha", 1f, 0f).apply { duration = 500 }
        val fadeOut2 = ObjectAnimator.ofFloat(binding.addressStart, "alpha", 1f, 0f).apply { duration = 500 }

        val scaleDownX = ObjectAnimator.ofFloat(binding.logoIcon, "scaleX", 1f, 0.5f).apply { duration = 500 }
        val scaleDownY = ObjectAnimator.ofFloat(binding.logoIcon, "scaleY", 1f, 0.5f).apply { duration = 500 }

        // Анимация поднятия логотипа
        val moveUp = ObjectAnimator.ofFloat(binding.logoIcon, "translationY", 0f, -370f).apply { duration = 500 }

        AnimatorSet().apply {
            playTogether(fadeOut1, fadeOut2, scaleDownX, scaleDownY, moveUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.titleStart.visibility = View.INVISIBLE
                    binding.addressStart.visibility = View.INVISIBLE

                    // layoutParams не меняем, размер остается фиксированным
                    showLinearLayout()
                }
            })
            start()
        }
    }


    private fun showLinearLayout() {
        binding.layout.visibility = View.VISIBLE

        val viewsToAnimate = listOf(
            binding.helloStart,
            binding.descStart,
            binding.btnStart,
            binding.noAccStart
        )

        viewsToAnimate.forEach { view ->
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.translationX = -100f
        }

        playSequentiallyWithDelay(
            viewsToAnimate.map { view ->
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(view, "translationX", view.translationX, 0f).apply {
                            duration = 400
                            interpolator = AccelerateDecelerateInterpolator()
                        },
                        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                            duration = 400
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    )
                }
            },
            150L
        )
    }

    private fun playSequentiallyWithDelay(animators: List<AnimatorSet>, delayBetween: Long) {
        if (animators.isEmpty()) return

        fun playNext(index: Int) {
            if (index >= animators.size) return
            animators[index].start()
            animators[index].addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        playNext(index + 1)
                    }, delayBetween)
                }
            })
        }

        playNext(0)
    }
}
