package com.shashwat.streamio.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shashwat.streamio.R
import com.shashwat.streamio.home.segmentShape
import com.shashwat.streamio.ui.theme.LocalStreamioStatusColors
import com.shashwat.streamio.ui.theme.StreamioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayLibraryScreen(
    projects: List<OverlayProject>,
    activeId: String?,
    onCreateProject: () -> Unit,
    onEditProject: (OverlayProject) -> Unit,
    onToggleEnabled: (OverlayProject, Boolean) -> Unit,
    onDeleteProject: (OverlayProject) -> Unit,
    onBack: () -> Unit,
) {
    var projectToDelete by remember { mutableStateOf<OverlayProject?>(null) }

    StreamioTheme {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = { Text("Overlays") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onCreateProject,
                    icon = { Icon(painterResource(R.drawable.ic_auto_awesome), contentDescription = null) },
                    text = { Text("New overlay") },
                )
            },
        ) { padding ->
            if (projects.isEmpty()) {
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("No overlays yet", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Create one to add a logo, text or lower third on top of your broadcast.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                val status = LocalStreamioStatusColors.current
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        Text(
                            "Switch an overlay on to show it live. Tap to edit its layers.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                        )
                    }
                    itemsIndexed(projects, key = { _, p -> p.id }) { index, project ->
                        val isLive = project.config.enabled && project.id == activeId
                        val layers = project.config.layers.size
                        ListItem(
                            headlineContent = { Text(project.name) },
                            supportingContent = {
                                Text(
                                    (if (isLive) "On air · " else "") +
                                        "$layers ${if (layers == 1) "layer" else "layers"}",
                                    color = if (isLive) status.onAir else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(onClick = { projectToDelete = project }) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                    Switch(
                                        checked = project.config.enabled,
                                        onCheckedChange = { onToggleEnabled(project, it) },
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                            modifier = Modifier
                                .clip(segmentShape(index, projects.size))
                                .clickable(role = Role.Button, onClickLabel = "Edit") { onEditProject(project) },
                        )
                    }
                }
            }
        }

        projectToDelete?.let { project ->
            AlertDialog(
                onDismissRequest = { projectToDelete = null },
                title = { Text("Delete “${project.name}”?") },
                text = { Text("This overlay and its layers will be removed. This can’t be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        projectToDelete = null
                        onDeleteProject(project)
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { projectToDelete = null }) { Text("Cancel") }
                },
            )
        }
    }
}
