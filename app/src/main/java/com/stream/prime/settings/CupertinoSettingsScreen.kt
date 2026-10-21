package com.stream.prime.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.*
import io.github.alexzhirkevich.cupertino.section.*
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import io.github.alexzhirkevich.cupertino.theme.CupertinoColors

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoSettingsScreen(
    initialMode: String,
    initialService: String,
    initialCustomUrl: String,
    initialStreamKey: String,
    initialLandscapeResolution: String,
    initialVerticalResolution: String,
    initialLandscapeFps: Int,
    initialVerticalFps: Int,
    initialLandscapeBitrate: Int,
    initialVerticalBitrate: Int,
    isVoiceChatEnabled: Boolean,
    voiceChatStatusText: String,
    isAccessibilityGranted: Boolean,
    onVoiceChatToggle: (Boolean) -> Unit,
    onOpenAccessibility: () -> Unit,
    onResetSettings: () -> Unit,
    onSaveSettings: (
        mode: String,
        service: String,
        url: String,
        key: String,
        resolution: String,
        fps: Int,
        bitrate: Int
    ) -> Unit,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf(initialMode) }
    var service by remember { mutableStateOf(initialService) }
    var customUrl by remember { mutableStateOf(initialCustomUrl) }
    var streamKey by remember { mutableStateOf(initialStreamKey) }

    val landscapeResolutions = listOf("360p (640x360)", "480p (854x480)", "720p (1280x720)", "1080p (1920x1080)")
    val verticalResolutions = listOf("360p (360x640)", "480p (480x854)", "720p (720x1280)", "1080p (1080x1920)")

    var resolution by remember(mode) {
        mutableStateOf(if (mode == "Landscape") initialLandscapeResolution else initialVerticalResolution)
    }

    var fps by remember(mode) {
        mutableIntStateOf(if (mode == "Landscape") initialLandscapeFps else initialVerticalFps)
    }

    var bitrateText by remember(mode) {
        mutableStateOf(if (mode == "Landscape") initialLandscapeBitrate.toString() else initialVerticalBitrate.toString())
    }

    var showPlatformSheet by remember { mutableStateOf(false) }
    var showResolutionSheet by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val platforms = listOf("YouTube Live", "Facebook Live", "Twitch", "Instagram Live", "TikTok Live", "Custom RTMP")

    CupertinoTheme(
        colorScheme = io.github.alexzhirkevich.cupertino.theme.darkColorScheme()
    ) {
        CupertinoScaffold(
            topBar = {
                CupertinoTopAppBar(
                    title = {
                        CupertinoText(
                            text = "Settings",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        CupertinoButton(
                            onClick = onBack,
                            colors = CupertinoButtonDefaults.plainButtonColors()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.ChevronBackward,
                                    contentDescription = "Back",
                                    tint = CupertinoTheme.colorScheme.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                CupertinoText(
                                    text = "Studio",
                                    color = CupertinoTheme.colorScheme.accent,
                                    fontSize = 17.sp
                                )
                            }
                        }
                    },
                    actions = {
                        CupertinoButton(
                            onClick = {
                                val bitrateInt = bitrateText.toIntOrNull() ?: 2500
                                onSaveSettings(mode, service, customUrl, streamKey, resolution, fps, bitrateInt)
                            },
                            colors = CupertinoButtonDefaults.plainButtonColors()
                        ) {
                            CupertinoText(
                                text = "Done",
                                fontWeight = FontWeight.Bold,
                                color = CupertinoTheme.colorScheme.accent,
                                fontSize = 17.sp
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .padding(bottom = 30.dp)
            ) {
                // Section 1: Orientation
                CupertinoSection(
                    title = { CupertinoText("STREAM ORIENTATION") },
                    caption = {
                        CupertinoText(
                            if (mode == "Landscape") "Standard 16:9 widescreen broadcast for YouTube, Twitch, etc."
                            else "Vertical 9:16 portrait broadcast for TikTok, Instagram Reels, Shorts."
                        )
                    }
                ) {
                    SectionItem(
                        title = {
                            CupertinoSegmentedControl(
                                selectedTabIndex = if (mode == "Landscape") 0 else 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                CupertinoSegmentedControlTab(
                                    isSelected = mode == "Landscape",
                                    onClick = { mode = "Landscape" }
                                ) {
                                    CupertinoText("Landscape (16:9)")
                                }
                                CupertinoSegmentedControlTab(
                                    isSelected = mode == "Vertical",
                                    onClick = { mode = "Vertical" }
                                ) {
                                    CupertinoText("Vertical (9:16)")
                                }
                            }
                        }
                    )
                }

                // Section 2: Platform & Ingestion
                CupertinoSection(
                    title = { CupertinoText("DESTINATION & SERVICE") },
                    caption = {
                        if (service != "Custom RTMP") {
                            CupertinoText("Paste your RTMP stream key from $service.")
                        } else {
                            CupertinoText("Enter custom RTMP server URL and ingestion key.")
                        }
                    }
                ) {
                    SectionLink(
                        onClick = { showPlatformSheet = true },
                        title = { CupertinoText("Platform") },
                        caption = { CupertinoText(service) }
                    )

                    if (service == "Custom RTMP") {
                        SectionTextField(
                            value = customUrl,
                            onValueChange = { customUrl = it },
                            placeholder = { CupertinoText("rtmp://...") },
                            trailingIcon = {
                                CupertinoText("Server URL", color = CupertinoTheme.colorScheme.secondaryLabel, fontSize = 13.sp)
                            }
                        )
                    }

                    SectionTextField(
                        value = streamKey,
                        onValueChange = { streamKey = it },
                        placeholder = { CupertinoText("Stream Key") },
                        trailingIcon = {
                            CupertinoText("Key", color = CupertinoTheme.colorScheme.secondaryLabel, fontSize = 13.sp)
                        }
                    )
                }

                // Section 3: Video Encoder Settings
                CupertinoSection(
                    title = { CupertinoText("ENCODER CONFIGURATION") },
                    caption = {
                        CupertinoText("Recommended: 720p @ 30/60 FPS with 2500-4000 Kbps for smooth streaming.")
                    }
                ) {
                    SectionLink(
                        onClick = { showResolutionSheet = true },
                        title = { CupertinoText("Resolution") },
                        caption = { CupertinoText(resolution) }
                    )

                    SectionItem(
                        title = { CupertinoText("Frame Rate") },
                        trailingContent = {
                            CupertinoSegmentedControl(
                                selectedTabIndex = if (fps == 30) 0 else 1,
                                modifier = Modifier.width(160.dp)
                            ) {
                                CupertinoSegmentedControlTab(
                                    isSelected = fps == 30,
                                    onClick = { fps = 30 }
                                ) {
                                    CupertinoText("30 FPS")
                                }
                                CupertinoSegmentedControlTab(
                                    isSelected = fps == 60,
                                    onClick = { fps = 60 }
                                ) {
                                    CupertinoText("60 FPS")
                                }
                            }
                        }
                    )

                    SectionTextField(
                        value = bitrateText,
                        onValueChange = { bitrateText = it.filter { ch -> ch.isDigit() } },
                        placeholder = { CupertinoText("2500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            CupertinoText("Kbps", color = CupertinoTheme.colorScheme.secondaryLabel, fontSize = 14.sp)
                        }
                    )
                }

                // Section 4: Voice Chat Compatibility
                CupertinoSection(
                    title = { CupertinoText("AUDIO & VOICE CHAT MIXER") },
                    caption = {
                        CupertinoText(voiceChatStatusText)
                    }
                ) {
                    SectionItem(
                        title = { CupertinoText("Mic Share Compatibility") },
                        trailingContent = {
                            CupertinoSwitch(
                                checked = isVoiceChatEnabled,
                                onCheckedChange = onVoiceChatToggle
                            )
                        }
                    )

                    if (isVoiceChatEnabled && !isAccessibilityGranted) {
                        SectionLink(
                            onClick = onOpenAccessibility,
                            title = {
                                CupertinoText(
                                    text = "Grant Accessibility Permission",
                                    color = Color(0xFFFF9500)
                                )
                            },
                            caption = { CupertinoText("Required") }
                        )
                    }
                }

                // Section 5: Reset
                CupertinoSection(
                    caption = {
                        CupertinoText("Restores default RTMP bitrate, resolution, and platform selections.")
                    }
                ) {
                    SectionItem(
                        modifier = Modifier.clickable { showResetDialog = true },
                        title = {
                            CupertinoText(
                                text = "Reset All Settings",
                                color = Color(0xFFFF3B30),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        // Platform Action Sheet
        CupertinoActionSheet(
            visible = showPlatformSheet,
            onDismissRequest = { showPlatformSheet = false },
            title = { CupertinoText("Select Streaming Platform") },
            message = { CupertinoText("Choose your preferred destination") },
            buttons = {
                platforms.forEach { platform ->
                    action(
                        onClick = {
                            service = platform
                            showPlatformSheet = false
                        },
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CupertinoText(
                                    text = platform,
                                    fontWeight = if (service == platform) FontWeight.Bold else FontWeight.Normal,
                                    color = if (service == platform) CupertinoTheme.colorScheme.accent else CupertinoTheme.colorScheme.label
                                )
                                if (service == platform) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.Checkmark,
                                        contentDescription = "Selected",
                                        tint = CupertinoTheme.colorScheme.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )
                }
                action(
                    onClick = { showPlatformSheet = false },
                    style = AlertActionStyle.Cancel,
                    title = { CupertinoText("Cancel") }
                )
            }
        )

        // Resolution Action Sheet
        val resolutions = if (mode == "Landscape") landscapeResolutions else verticalResolutions
        CupertinoActionSheet(
            visible = showResolutionSheet,
            onDismissRequest = { showResolutionSheet = false },
            title = { CupertinoText("Select Resolution") },
            message = { CupertinoText("Choose video output resolution") },
            buttons = {
                resolutions.forEach { resOption ->
                    action(
                        onClick = {
                            resolution = resOption
                            showResolutionSheet = false
                        },
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CupertinoText(
                                    text = resOption,
                                    fontWeight = if (resolution == resOption) FontWeight.Bold else FontWeight.Normal,
                                    color = if (resolution == resOption) CupertinoTheme.colorScheme.accent else CupertinoTheme.colorScheme.label
                                )
                                if (resolution == resOption) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.Checkmark,
                                        contentDescription = "Selected",
                                        tint = CupertinoTheme.colorScheme.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )
                }
                action(
                    onClick = { showResolutionSheet = false },
                    style = AlertActionStyle.Cancel,
                    title = { CupertinoText("Cancel") }
                )
            }
        )

        // Reset Confirmation Dialog
        if (showResetDialog) {
            CupertinoAlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { CupertinoText("Reset Settings?") },
                message = { CupertinoText("Are you sure you want to reset all streaming settings to default values?") },
                buttons = {
                    action(
                        onClick = {
                            showResetDialog = false
                            onResetSettings()
                        },
                        style = AlertActionStyle.Destructive,
                        title = { CupertinoText("Reset to Defaults") }
                    )
                    action(
                        onClick = { showResetDialog = false },
                        style = AlertActionStyle.Cancel,
                        title = { CupertinoText("Cancel") }
                    )
                }
            )
        }
    }
}
