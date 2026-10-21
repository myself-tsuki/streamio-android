package com.stream.prime.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
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
private val IosSystemRed = Color(0xFFFF3B30)

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CupertinoLayerManagerScreen(
    projects: List<OverlayProject>,
    activeId: String?,
    onCreateProject: () -> Unit,
    onEditProject: (OverlayProject) -> Unit,
    onToggleEnabled: (OverlayProject, Boolean) -> Unit,
    onDeleteProject: (OverlayProject) -> Unit,
    onBack: () -> Unit
) {
    var projectToDelete by remember { mutableStateOf<OverlayProject?>(null) }

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
                                contentDescription = "Close",
                                tint = CupertinoTheme.colorScheme.accent
                            )
                        }
                    },
                    title = {
                        CupertinoText(
                            text = "Overlay Library",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        CupertinoButton(
                            onClick = onCreateProject,
                            colors = CupertinoButtonDefaults.plainButtonColors(
                                contentColor = CupertinoTheme.colorScheme.accent
                            )
                        ) {
                            CupertinoText(
                                text = "New",
                                fontWeight = FontWeight.SemiBold
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
                if (projects.isEmpty()) {
                    CupertinoSection(
                        caption = {
                            CupertinoText(
                                text = "Tap 'New' at the top right to design camera frames, logos, and lower thirds.",
                                color = CupertinoTheme.colorScheme.secondaryLabel
                            )
                        }
                    ) {
                        SectionItem(
                            title = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CupertinoText(
                                        text = "No Overlay Compositions",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp,
                                        color = CupertinoTheme.colorScheme.label
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CupertinoText(
                                        text = "Add graphics and text overlays to enhance your stream.",
                                        fontSize = 13.sp,
                                        color = CupertinoTheme.colorScheme.secondaryLabel
                                    )
                                }
                            }
                        )
                    }
                } else {
                    CupertinoSection(
                        title = {
                            CupertinoText(
                                text = "SAVED COMPOSITIONS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = CupertinoTheme.colorScheme.secondaryLabel
                            )
                        },
                        caption = {
                            CupertinoText(
                                text = "Toggle the switch to make an overlay live on the broadcast.",
                                color = CupertinoTheme.colorScheme.secondaryLabel
                            )
                        }
                    ) {
                        projects.forEach { project ->
                            val isLive = project.config.enabled && (project.id == activeId)
                            SectionItem(
                                title = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onEditProject(project) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CupertinoText(
                                                text = project.name,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = CupertinoTheme.colorScheme.label
                                            )
                                            if (isLive) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(IosSystemGreen.copy(alpha = 0.2f))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    CupertinoText(
                                                        text = "LIVE",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = IosSystemGreen
                                                    )
                                                }
                                            }
                                        }
                                        CupertinoText(
                                            text = "${project.config.layers.size} layers • Tap to edit",
                                            fontSize = 12.sp,
                                            color = CupertinoTheme.colorScheme.secondaryLabel
                                        )
                                    }
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CupertinoSwitch(
                                            checked = project.config.enabled,
                                            onCheckedChange = { onToggleEnabled(project, it) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CupertinoIconButton(
                                            onClick = { projectToDelete = project }
                                        ) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.Trash,
                                                contentDescription = "Delete",
                                                tint = IosSystemRed,
                                                modifier = Modifier.size(18.dp)
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

        projectToDelete?.let { project ->
            CupertinoAlertDialog(
                onDismissRequest = { projectToDelete = null },
                title = { CupertinoText("Delete ${project.name}?") },
                message = { CupertinoText("This overlay composition cannot be restored.") },
                buttons = {
                    action(
                        onClick = {
                            val toDelete = projectToDelete
                            projectToDelete = null
                            toDelete?.let { onDeleteProject(it) }
                        },
                        style = AlertActionStyle.Destructive,
                        title = { CupertinoText("Delete") }
                    )
                    action(
                        onClick = { projectToDelete = null },
                        style = AlertActionStyle.Cancel,
                        title = { CupertinoText("Cancel") }
                    )
                }
            )
        }
    }
}
