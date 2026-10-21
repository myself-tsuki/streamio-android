package com.stream.prime.file

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
private val IosSystemIndigo = Color(0xFF5856D6)
private val IosSystemGreen = Color(0xFF34C759)

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoFileStreamScreen(
    selectedUri: Uri?,
    onBrowseFiles: () -> Unit,
    onStartStream: () -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    CupertinoTheme(
        colorScheme = darkColorScheme(
            accent = IosSystemBlue
        )
    ) {
        CupertinoScaffold(
            topBar = {
                CupertinoTopAppBar(
                    navigationIcon = {
                        CupertinoIconButton(onClick = onBack) {
                            CupertinoIcon(
                                imageVector = CupertinoIcons.Default.ChevronBackward,
                                contentDescription = "Back",
                                tint = CupertinoTheme.colorScheme.accent
                            )
                        }
                    },
                    title = {
                        CupertinoText(
                            text = "File Streaming",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        CupertinoIconButton(onClick = onOpenSettings) {
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Section 1: Source File Selection
                CupertinoSection(
                    title = {
                        CupertinoText(
                            text = "VIDEO SOURCE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CupertinoTheme.colorScheme.secondaryLabel
                        )
                    },
                    caption = {
                        CupertinoText(
                            text = "Stream a pre-recorded MP4 or video file directly to your RTMP destination.",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 12.sp
                        )
                    }
                ) {
                    SectionItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IosSystemIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                CupertinoIcon(
                                    imageVector = CupertinoIcons.Default.Video,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        title = {
                            CupertinoText(
                                text = if (selectedUri != null) "Video Selected" else "No File Selected",
                                fontWeight = FontWeight.SemiBold,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            CupertinoButton(
                                onClick = onBrowseFiles,
                                colors = CupertinoButtonDefaults.filledButtonColors(
                                    containerColor = IosSystemBlue,
                                    contentColor = Color.White
                                ),
                                size = CupertinoButtonSize.Small
                            ) {
                                CupertinoText(
                                    text = if (selectedUri != null) "Change" else "Browse",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Stream Action
                CupertinoSection(
                    caption = {
                        CupertinoText(
                            text = "Make sure your RTMP server URL and Stream Key are configured in Stream Settings.",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 12.sp
                        )
                    }
                ) {
                    SectionItem(
                        title = {
                            CupertinoButton(
                                onClick = onStartStream,
                                enabled = selectedUri != null,
                                colors = CupertinoButtonDefaults.filledButtonColors(
                                    containerColor = if (selectedUri != null) IosSystemGreen else Color(0xFF3A3A3C),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.Play,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CupertinoText(
                                        text = "Start File Streaming",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
