package com.shashwat.streamio.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.*
import io.github.alexzhirkevich.cupertino.section.*
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import io.github.alexzhirkevich.cupertino.theme.darkColorScheme

private val IosSystemBlue = Color(0xFF007AFF)
private val IosSystemGreen = Color(0xFF34C759)
private val IosSystemOrange = Color(0xFFFF9500)
private val IosSystemIndigo = Color(0xFF5856D6)
private val IosSystemPurple = Color(0xFFAF52DE)
private val IosSystemTeal = Color(0xFF30B0C7)

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoHomeScreen(
    versionName: String,
    hasOverlayPermission: Boolean,
    onStartScreenStream: () -> Unit,
    onOpenFileStreaming: () -> Unit,
    onOpenOverlayStudio: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onRequestOverlayPermission: () -> Unit
) {
    CupertinoTheme(
        colorScheme = darkColorScheme(
            accent = IosSystemBlue
        )
    ) {
        CupertinoScaffold(
            topBar = {
                CupertinoTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CupertinoText(
                                text = "Streamio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(IosSystemBlue.copy(alpha = 0.2f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                CupertinoText(
                                    text = "STUDIO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = IosSystemBlue,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    },
                    actions = {
                        CupertinoIconButton(
                            onClick = onOpenSettings
                        ) {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.Gear,
                                contentDescription = "Settings",
                                tint = CupertinoTheme.colorScheme.accent
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CupertinoTheme.colorScheme.systemGroupedBackground)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 8.dp)
            ) {
                // Section 1: Hero Broadcast Card
                CupertinoSection {
                    SectionItem(
                        title = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                // Live Status Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(IosSystemGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CupertinoText(
                                        text = "READY TO BROADCAST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IosSystemGreen,
                                        letterSpacing = 0.6.sp
                                    )
                                }

                                // Main Hero Title
                                CupertinoText(
                                    text = "Live Screen Stream",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CupertinoTheme.colorScheme.label,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                CupertinoText(
                                    text = "Broadcast device screen, high-framerate gameplay, or camera with dual hardware audio mixer and customizable studio overlays.",
                                    fontSize = 14.sp,
                                    color = CupertinoTheme.colorScheme.secondaryLabel,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                // Cupertino Filled Action Button
                                CupertinoButton(
                                    onClick = onStartScreenStream,
                                    colors = CupertinoButtonDefaults.filledButtonColors(
                                        containerColor = IosSystemBlue,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CupertinoIcon(
                                            imageVector = CupertinoIcons.Default.Play,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CupertinoText(
                                            text = "Start Broadcast",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Studio Tools
                CupertinoSection(
                    title = {
                        CupertinoText(
                            text = "STUDIO TOOLS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            letterSpacing = 0.5.sp
                        )
                    }
                ) {
                    // Overlay Studio
                    SectionItem(
                        modifier = Modifier.clickable { onOpenOverlayStudio() },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IosSystemPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Video,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        title = {
                            CupertinoText(
                                text = "Overlay Studio & Layers",
                                fontWeight = FontWeight.SemiBold,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CupertinoText(
                                    text = "Scenes & Assets",
                                    color = CupertinoTheme.colorScheme.secondaryLabel,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.ChevronForward,
                                    contentDescription = null,
                                    tint = CupertinoTheme.colorScheme.secondaryLabel,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    )

                    // File Streaming
                    SectionItem(
                        modifier = Modifier.clickable { onOpenFileStreaming() },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IosSystemIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Video,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        title = {
                            CupertinoText(
                                text = "File Video Streaming",
                                fontWeight = FontWeight.SemiBold,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CupertinoText(
                                    text = "Loop & Playlist",
                                    color = CupertinoTheme.colorScheme.secondaryLabel,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.ChevronForward,
                                    contentDescription = null,
                                    tint = CupertinoTheme.colorScheme.secondaryLabel,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    )

                    // Stream Settings
                    SectionItem(
                        modifier = Modifier.clickable { onOpenSettings() },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IosSystemOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Gear,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        title = {
                            CupertinoText(
                                text = "Stream & Quality Settings",
                                fontWeight = FontWeight.SemiBold,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CupertinoText(
                                    text = "RTMP, SRT & Quality",
                                    color = CupertinoTheme.colorScheme.secondaryLabel,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.ChevronForward,
                                    contentDescription = null,
                                    tint = CupertinoTheme.colorScheme.secondaryLabel,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: System Access
                CupertinoSection(
                    title = {
                        CupertinoText(
                            text = "SYSTEM ACCESS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            letterSpacing = 0.5.sp
                        )
                    },
                    caption = {
                        CupertinoText(
                            text = "Floating overlay gives on-screen stream control while broadcasting gameplay or background apps without interrupting the session.",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                ) {
                    SectionItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IosSystemTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.ChevronForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        title = {
                            CupertinoText(
                                text = "Floating Controls Window",
                                fontWeight = FontWeight.Normal,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            if (hasOverlayPermission) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(IosSystemGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 9.dp, vertical = 4.dp)
                                ) {
                                    CupertinoText(
                                        text = "Active",
                                        color = IosSystemGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                CupertinoButton(
                                    onClick = onRequestOverlayPermission,
                                    colors = CupertinoButtonDefaults.tintedButtonColors(
                                        containerColor = IosSystemBlue.copy(alpha = 0.2f),
                                        contentColor = IosSystemBlue
                                    ),
                                    size = CupertinoButtonSize.Small
                                ) {
                                    CupertinoText(
                                        text = "Grant",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Footer
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CupertinoText(
                        text = "Streamio Studio • v$versionName",
                        fontSize = 13.sp,
                        color = CupertinoTheme.colorScheme.secondaryLabel,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CupertinoText(
                        text = "Ultra-Low Latency Mobile Broadcast Engine",
                        fontSize = 11.sp,
                        color = CupertinoTheme.colorScheme.tertiaryLabel
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

