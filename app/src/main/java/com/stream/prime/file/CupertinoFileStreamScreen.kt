package com.stream.prime.file

import android.net.Uri
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
import androidx.compose.ui.text.style.TextOverflow
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
private val IosSystemRed = Color(0xFFFF453A)

data class VideoQueueItem(
    val uri: Uri,
    val name: String
)

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoFileStreamScreen(
    videos: List<VideoQueueItem>,
    isLoopEnabled: Boolean,
    onAddVideos: () -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemoveVideo: (Int) -> Unit,
    onClearAll: () -> Unit,
    onToggleLoop: (Boolean) -> Unit,
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
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                // Section 1: Video Sequence Queue
                if (videos.isEmpty()) {
                    CupertinoSection(
                        title = {
                            CupertinoText(
                                text = "VIDEO PLAYLIST QUEUE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = CupertinoTheme.colorScheme.secondaryLabel
                            )
                        },
                        caption = {
                            CupertinoText(
                                text = "Select multiple video files (MP4, MKV, etc.) to broadcast continuously in sequential order.",
                                color = CupertinoTheme.colorScheme.secondaryLabel,
                                fontSize = 12.sp
                            )
                        }
                    ) {
                        SectionItem(
                            title = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(IosSystemIndigo.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CupertinoIcon(
                                            imageVector = CupertinoIcons.Default.Video,
                                            contentDescription = null,
                                            tint = IosSystemIndigo,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CupertinoText(
                                        text = "No Videos Selected",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp,
                                        color = CupertinoTheme.colorScheme.label
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CupertinoText(
                                        text = "Tap below to choose multiple video files",
                                        fontSize = 13.sp,
                                        color = CupertinoTheme.colorScheme.secondaryLabel
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CupertinoButton(
                                        onClick = onAddVideos,
                                        colors = CupertinoButtonDefaults.filledButtonColors(
                                            containerColor = IosSystemBlue,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth(0.7f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.Plus,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            CupertinoText(
                                                text = "Select Videos",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                } else {
                    CupertinoSection(
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CupertinoText(
                                    text = "SEQUENCE QUEUE (${videos.size} VIDEOS)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CupertinoTheme.colorScheme.secondaryLabel
                                )
                                CupertinoButton(
                                    onClick = onClearAll,
                                    colors = CupertinoButtonDefaults.plainButtonColors(
                                        contentColor = IosSystemRed
                                    ),
                                    size = CupertinoButtonSize.Small
                                ) {
                                    CupertinoText("Clear", fontSize = 12.sp)
                                }
                            }
                        },
                        caption = {
                            CupertinoText(
                                text = "Videos stream one after another from top to bottom. Use arrows to change order.",
                                color = CupertinoTheme.colorScheme.secondaryLabel,
                                fontSize = 12.sp
                            )
                        }
                    ) {
                        videos.forEachIndexed { index, item ->
                            SectionItem(
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (index == 0) IosSystemBlue else Color(0xFF2C2C2E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CupertinoText(
                                            text = "${index + 1}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                },
                                title = {
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        CupertinoText(
                                            text = item.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            color = CupertinoTheme.colorScheme.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        CupertinoText(
                                            text = if (index == 0) "First (Starts stream)" else "Plays next in sequence",
                                            fontSize = 11.sp,
                                            color = if (index == 0) IosSystemBlue else CupertinoTheme.colorScheme.secondaryLabel
                                        )
                                    }
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Move Up
                                        CupertinoIconButton(
                                            onClick = { onMoveUp(index) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.ChevronUp,
                                                contentDescription = "Move Up",
                                                tint = if (index > 0) CupertinoTheme.colorScheme.accent else Color(0xFF48484A),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Move Down
                                        CupertinoIconButton(
                                            onClick = { onMoveDown(index) },
                                            enabled = index < videos.lastIndex,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.ChevronDown,
                                                contentDescription = "Move Down",
                                                tint = if (index < videos.lastIndex) CupertinoTheme.colorScheme.accent else Color(0xFF48484A),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Delete
                                        CupertinoIconButton(
                                            onClick = { onRemoveVideo(index) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.Trash,
                                                contentDescription = "Delete",
                                                tint = IosSystemRed,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        // Add More Videos row
                        SectionItem(
                            modifier = Modifier.clickable { onAddVideos() },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(IosSystemGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CupertinoIcon(
                                        imageVector = CupertinoIcons.Default.Plus,
                                        contentDescription = "Add",
                                        tint = IosSystemGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            title = {
                                CupertinoText(
                                    text = "Add More Videos",
                                    fontWeight = FontWeight.SemiBold,
                                    color = IosSystemGreen,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Sequence Options
                CupertinoSection(
                    title = {
                        CupertinoText(
                            text = "PLAYBACK SETTINGS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CupertinoTheme.colorScheme.secondaryLabel
                        )
                    },
                    caption = {
                        CupertinoText(
                            text = if (isLoopEnabled)
                                "When the final video completes, the queue will loop back to track #1 and repeat."
                            else
                                "Broadcast will automatically end once the final video finishes.",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 12.sp
                        )
                    }
                ) {
                    SectionItem(
                        title = {
                            CupertinoText(
                                text = "Loop Playlist Sequence",
                                fontWeight = FontWeight.Normal,
                                color = CupertinoTheme.colorScheme.label
                            )
                        },
                        trailingContent = {
                            CupertinoSwitch(
                                checked = isLoopEnabled,
                                onCheckedChange = onToggleLoop
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Start Stream Action
                CupertinoSection(
                    caption = {
                        CupertinoText(
                            text = "Video resolution and bitrates follow your configured Stream Settings.",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 12.sp
                        )
                    }
                ) {
                    SectionItem(
                        title = {
                            CupertinoButton(
                                onClick = onStartStream,
                                enabled = videos.isNotEmpty(),
                                colors = CupertinoButtonDefaults.filledButtonColors(
                                    containerColor = if (videos.isNotEmpty()) IosSystemGreen else Color(0xFF3A3A3C),
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
                                        text = if (videos.size > 1) "Stream ${videos.size} Videos in Sequence" else "Start File Stream",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
