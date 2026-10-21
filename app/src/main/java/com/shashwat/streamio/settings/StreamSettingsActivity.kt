package com.shashwat.streamio.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shashwat.streamio.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.shashwat.streamio.accessibility.StreamAccessibilityService
import com.shashwat.streamio.screen.ScreenService

import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class StreamSettingsActivity : AppCompatActivity(), SettingsManager.SettingsChangeListener {
    
    companion object {
        private const val TAG = "StreamSettings"
    }
    
    // State Management for Compose
    private var isUpdatingUI = false
    private var isSavingSettings = false
    
    // Settings Data States
    private val currentStreamingMode = mutableStateOf("Landscape")
    private val currentStreamingService = mutableStateOf("YouTube Live")
    private val currentCustomUrl = mutableStateOf("")
    private val currentStreamKey = mutableStateOf("")
    private val currentLandscapeResolution = mutableStateOf("720p (1280x720)")
    private val currentVerticalResolution = mutableStateOf("720p (720x1280)")
    private val currentLandscapeFps = mutableStateOf(30)
    private val currentVerticalFps = mutableStateOf(30)
    private val currentLandscapeBitrate = mutableStateOf(2500)
    private val currentVerticalBitrate = mutableStateOf(2000)
    private val isVoiceChatEnabled = mutableStateOf(false)
    private val voiceChatStatusText = mutableStateOf("Ready to share mic")
    private val isAccessibilityGranted = mutableStateOf(false)
    
    // Configuration Data
    private val streamingServices = listOf("YouTube Live", "Facebook Live", "Twitch", "Instagram Live", "TikTok Live", "Custom RTMP")
    private val streamingModes = listOf("Landscape", "Vertical")
    
    private val resolutionOptions = mapOf(
        "360p (640x360)" to Pair(640, 360),
        "480p (854x480)" to Pair(854, 480),
        "720p (1280x720)" to Pair(1280, 720),
        "1080p (1920x1080)" to Pair(1920, 1080)
    )
    
    private val verticalResolutionOptions = mapOf(
        "360p (360x640)" to Pair(360, 640),
        "480p (480x854)" to Pair(480, 854),
        "720p (720x1280)" to Pair(720, 1280),
        "1080p (1080x1920)" to Pair(1080, 1920)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.statusBarColor = android.graphics.Color.parseColor("#000000")
        window.navigationBarColor = android.graphics.Color.parseColor("#000000")
        
        loadSettingsFromStorage()
        updateVoiceChatCompatibilityState()
        SettingsManager.addListener(this)

        setContent {
            CupertinoSettingsScreen(
                initialMode = currentStreamingMode.value,
                initialService = currentStreamingService.value,
                initialCustomUrl = currentCustomUrl.value,
                initialStreamKey = currentStreamKey.value,
                initialLandscapeResolution = currentLandscapeResolution.value,
                initialVerticalResolution = currentVerticalResolution.value,
                initialLandscapeFps = currentLandscapeFps.value,
                initialVerticalFps = currentVerticalFps.value,
                initialLandscapeBitrate = currentLandscapeBitrate.value,
                initialVerticalBitrate = currentVerticalBitrate.value,
                isVoiceChatEnabled = isVoiceChatEnabled.value,
                voiceChatStatusText = voiceChatStatusText.value,
                isAccessibilityGranted = isAccessibilityGranted.value,
                onVoiceChatToggle = { enabled ->
                    if (enabled) {
                        showVoiceChatCompatibilityDisclosure()
                    } else {
                        SettingsManager.setGameVoiceChatCompatibilityEnabled(this, false)
                        StreamAccessibilityService.setCaptureActive(this, false)
                        ScreenService.INSTANCE?.refreshGameVoiceChatCompatibilityMode()
                        updateVoiceChatCompatibilityState()
                    }
                },
                onOpenAccessibility = { openAccessibilitySettings() },
                onResetSettings = { resetToDefaults() },
                onSaveSettings = { mode, service, url, key, res, fps, bitrate ->
                    saveAllSettingsFromCompose(mode, service, url, key, res, fps, bitrate)
                },
                onBack = { finish() }
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        SettingsManager.removeListener(this)
    }

    override fun onResume() {
        super.onResume()
        val compatibilityEnabled = reconcileVoiceChatCompatibilityWithAccessibility()
        val captureActive = ScreenService.INSTANCE?.let {
            it.isStreaming() || it.isRecording()
        } == true
        if (captureActive && compatibilityEnabled) {
            val overlayWasVisible =
                StreamAccessibilityService.isCompatibilityOverlayVisible(this)
            val activated = StreamAccessibilityService.setCaptureActive(this, true)
            if (activated && !overlayWasVisible) {
                ScreenService.INSTANCE?.refreshGameVoiceChatCompatibilityMode()
            }
        }
        if (compatibilityEnabled) maybeOfferBackgroundProtection()
        updateVoiceChatCompatibilityState()
    }

    /**
     * Accessibility can be disabled outside Streamio while the saved compatibility switch is
     * still true. Make the switch truthful as soon as this screen resumes. The next user attempt
     * to turn it on follows the normal disclosure and opens Accessibility Settings again.
     */
    private fun reconcileVoiceChatCompatibilityWithAccessibility(): Boolean {
        val requestedEnabled = SettingsManager.isGameVoiceChatCompatibilityEnabled(this)
        val state = VoiceChatCompatibilityPolicy.resolve(
            requestedEnabled = requestedEnabled,
            accessibilityEnabled = StreamAccessibilityService.isEnabled(this),
            accessibilityConnected = StreamAccessibilityService.isConnected(this)
        )
        if (requestedEnabled && !state.enabled) {
            Log.w(TAG, "Mic Share is disconnected; automatically disabling compatibility mode")
            SettingsManager.setGameVoiceChatCompatibilityEnabled(this, false)
            StreamAccessibilityService.setCaptureActive(this, false)
            ScreenService.INSTANCE?.refreshGameVoiceChatCompatibilityMode()
            android.widget.Toast.makeText(
                this,
                R.string.game_voice_chat_compatibility_auto_disabled,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        return state.enabled
    }
    
    override fun onSettingsChanged() {
        if (!isSavingSettings) {
            runOnUiThread {
                loadSettingsFromStorage()
                updateVoiceChatCompatibilityState()
            }
        }
    }
    
    private fun showVoiceChatCompatibilityDisclosure() {
        AlertDialog.Builder(this)
            .setTitle(R.string.game_voice_chat_compatibility_title)
            .setMessage(R.string.game_voice_chat_compatibility_warning)
            .setPositiveButton(R.string.enable_compatibility_mode) { _, _ ->
                val state = VoiceChatCompatibilityPolicy.resolve(
                    requestedEnabled = true,
                    accessibilityEnabled = StreamAccessibilityService.isEnabled(this),
                    accessibilityConnected = StreamAccessibilityService.isConnected(this)
                )
                SettingsManager.setGameVoiceChatCompatibilityEnabled(this, true)
                updateVoiceChatCompatibilityState()
                if (state.shouldOpenAccessibilitySettings) {
                    openAccessibilitySettings()
                } else {
                    ScreenService.INSTANCE?.refreshGameVoiceChatCompatibilityMode()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                updateVoiceChatCompatibilityState()
            }
            .setOnCancelListener { updateVoiceChatCompatibilityState() }
            .show()
    }

    private fun openAccessibilitySettings() {
        runCatching {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }.onFailure {
            Log.e(TAG, "Unable to open Accessibility settings", it)
        }
    }

    private fun maybeOfferBackgroundProtection() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (!StreamAccessibilityService.isEnabled(this)) return
        if (SettingsManager.wasBackgroundProtectionPrompted(this)) return

        val powerManager = getSystemService(PowerManager::class.java)
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) return

        SettingsManager.markBackgroundProtectionPrompted(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.background_protection_title)
            .setMessage(R.string.background_protection_message)
            .setPositiveButton(R.string.allow_unrestricted_battery) { _, _ ->
                openBatteryProtectionSettings()
            }
            .setNegativeButton(R.string.not_now, null)
            .show()
    }

    private fun openBatteryProtectionSettings() {
        val packageUri = Uri.parse("package:$packageName")
        runCatching {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, packageUri)
            )
        }.recoverCatching {
            startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }.onFailure {
            Log.e(TAG, "Unable to open battery optimization settings", it)
        }
    }

    private fun updateVoiceChatCompatibilityState() {
        val enabled = SettingsManager.isGameVoiceChatCompatibilityEnabled(this)
        val accessibilityEnabled = StreamAccessibilityService.isEnabled(this)
        val overlayVisible = StreamAccessibilityService.isCompatibilityOverlayVisible(this)
        
        isVoiceChatEnabled.value = enabled
        isAccessibilityGranted.value = accessibilityEnabled
        
        voiceChatStatusText.value = getString(
            when {
                !enabled -> R.string.game_voice_chat_compatibility_disabled
                !accessibilityEnabled -> R.string.game_voice_chat_compatibility_permission_needed
                overlayVisible -> R.string.game_voice_chat_compatibility_active
                else -> R.string.game_voice_chat_compatibility_ready
            }
        )
    }
    
    private fun loadSettingsFromStorage() {
        val savedService = SettingsManager.getStreamingService(this)
        val savedUrl = SettingsManager.getStreamUrl(this)
        val savedKey = SettingsManager.getStreamKey(this)
        val mode = SettingsManager.getStreamingMode(this)
        
        currentStreamingMode.value = mode
        currentStreamingService.value = savedService
        currentCustomUrl.value = SettingsManager.getCustomStreamUrl(this)
        currentStreamKey.value = if (savedService == "Custom RTMP") {
            SettingsManager.getStreamKeyForService(this, savedService)
        } else {
            savedKey
        }
        
        val lWidth = SettingsManager.getLandscapeWidth(this)
        val lHeight = SettingsManager.getLandscapeHeight(this)
        currentLandscapeResolution.value = resolutionOptions.entries.find { it.value == Pair(lWidth, lHeight) }?.key ?: "720p (1280x720)"
        currentLandscapeFps.value = SettingsManager.getLandscapeFps(this)
        currentLandscapeBitrate.value = SettingsManager.getLandscapeBitrate(this)
        
        val vWidth = SettingsManager.getVerticalWidth(this)
        val vHeight = SettingsManager.getVerticalHeight(this)
        currentVerticalResolution.value = verticalResolutionOptions.entries.find { it.value == Pair(vWidth, vHeight) }?.key ?: "720p (720x1280)"
        currentVerticalFps.value = SettingsManager.getVerticalFps(this)
        currentVerticalBitrate.value = SettingsManager.getVerticalBitrate(this)
    }

    private fun saveAllSettingsFromCompose(
        mode: String,
        service: String,
        url: String,
        key: String,
        resolution: String,
        fps: Int,
        bitrate: Int
    ) {
        if (isSavingSettings) return
        isSavingSettings = true
        
        try {
            SettingsManager.startBatchUpdate()
            
            SettingsManager.setStreamingMode(this, mode)
            SettingsManager.setStreamingService(this, service)
            
            if (service == "Custom RTMP") {
                SettingsManager.setCustomStreamUrl(this, url)
                SettingsManager.setStreamKeyForService(this, service, key)
            } else {
                SettingsManager.setStreamKeyForService(this, service, key)
            }
            
            if (mode == "Landscape") {
                val (w, h) = resolutionOptions[resolution] ?: Pair(1280, 720)
                SettingsManager.setLandscapeWidth(this, w)
                SettingsManager.setLandscapeHeight(this, h)
                SettingsManager.setLandscapeFps(this, fps)
                SettingsManager.setLandscapeBitrate(this, bitrate)
            } else {
                val (w, h) = verticalResolutionOptions[resolution] ?: Pair(720, 1280)
                SettingsManager.setVerticalWidth(this, w)
                SettingsManager.setVerticalHeight(this, h)
                SettingsManager.setVerticalFps(this, fps)
                SettingsManager.setVerticalBitrate(this, bitrate)
            }
            
            android.widget.Toast.makeText(this, "Settings saved!", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving settings: ${e.message}")
            android.widget.Toast.makeText(this, "Error saving settings", android.widget.Toast.LENGTH_SHORT).show()
        } finally {
            SettingsManager.endBatchUpdate()
            isSavingSettings = false
        }
    }
    
    private fun resetToDefaults() {
        SettingsManager.resetToDefaults(this)
        StreamAccessibilityService.setCaptureActive(this, false)
        ScreenService.INSTANCE?.refreshGameVoiceChatCompatibilityMode()
        loadSettingsFromStorage()
        updateVoiceChatCompatibilityState()
        android.widget.Toast.makeText(this, "Settings reset to defaults", android.widget.Toast.LENGTH_SHORT).show()
    }
} 
