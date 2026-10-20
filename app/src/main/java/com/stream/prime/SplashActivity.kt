package com.stream.prime

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.stream.prime.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.versionText.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        // Hide action bar for full-screen experience
        supportActionBar?.hide()

        // Quick Material 3 entrance animation
        startAnimations()

        // Navigate to MainActivity smoothly after 800ms
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 850)
    }

    private fun startAnimations() {
        val logoScaleX = ObjectAnimator.ofFloat(binding.logoContainer, "scaleX", 0.7f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(binding.logoContainer, "scaleY", 0.7f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(binding.logoContainer, "alpha", 0f, 1f)
        val titleAlpha = ObjectAnimator.ofFloat(binding.appTitle, "alpha", 0f, 1f)
        val titleTranslate = ObjectAnimator.ofFloat(binding.appTitle, "translationY", 24f, 0f)
        val subtitleAlpha = ObjectAnimator.ofFloat(binding.appSubtitle, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha, titleAlpha, titleTranslate, subtitleAlpha)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
} 
