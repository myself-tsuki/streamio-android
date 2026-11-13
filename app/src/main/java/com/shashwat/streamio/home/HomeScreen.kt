package com.shashwat.streamio.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shashwat.streamio.R
import com.shashwat.streamio.ui.theme.LocalStreamioStatusColors
import com.shashwat.streamio.ui.theme.StreamioTheme

/** Shape for item [index] of [count] in an expressive segmented list group. */
internal fun segmentShape(index: Int, count: Int): Shape {
    val big = 20.dp
    val small = 4.dp
    return RoundedCornerShape(
        topStart = if (index == 0) big else small,
        topEnd = if (index == 0) big else small,
        bottomStart = if (index == count - 1) big else small,
        bottomEnd = if (index == count - 1) big else small,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    versionName: String,
    destinationSummary: String,
    hasOverlayPermission: Boolean,
    onStartScreenStream: () -> Unit,
    onOpenFileStreaming: () -> Unit,
    onOpenOverlayStudio: () -> Unit,
    onOpenSettings: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
) {
    StreamioTheme {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text("Streamio") },
                    actions = {
                        IconButton(onClick = onOpenSettings) {
                            Icon(painterResource(R.drawable.ic_settings), contentDescription = "Stream settings")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                GoLiveHero(destinationSummary, onStartScreenStream)

                SectionHeader("Studio tools")
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ToolRow("File playlist", "Stream saved videos in sequence", R.drawable.ic_repeat, segmentShape(0, 3), onOpenFileStreaming)
                    ToolRow("Overlays", "Logos, text and lower thirds", R.drawable.ic_auto_awesome, segmentShape(1, 3), onOpenOverlayStudio)
                    ToolRow("Stream settings", "Destination, resolution, bitrate", R.drawable.ic_settings, segmentShape(2, 3), onOpenSettings)
                }

                SectionHeader("Permissions")
                PermissionRow(hasOverlayPermission, onRequestOverlayPermission)

                Text(
                    text = "Streamio $versionName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp, bottom = 24.dp).align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

@Composable
private fun GoLiveHero(destinationSummary: String, onStart: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(
                "Broadcast your screen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(8.dp))
            Text("Game, app or camera with mic and device audio mixed live.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_screen_share), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(destinationSummary, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(24.dp))
            // Large expressive primary action: the one thing this screen is for.
            Button(
                onClick = onStart,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            ) {
                Icon(painterResource(R.drawable.ic_stream_start), contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Go live", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 32.dp, bottom = 12.dp).semantics { heading() },
    )
}

@Composable
private fun ToolRow(title: String, subtitle: String, @DrawableRes icon: Int, shape: Shape, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(icon), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.clip(shape).clickable(role = Role.Button, onClick = onClick),
    )
}

@Composable
private fun PermissionRow(granted: Boolean, onGrant: () -> Unit) {
    val status = LocalStreamioStatusColors.current
    ListItem(
        headlineContent = { Text("Floating controls") },
        supportingContent = {
            Text(
                if (granted) "Allowed: control the stream from any app"
                else "Needs “Display over other apps” to control the stream from games",
            )
        },
        trailingContent = {
            if (granted) {
                Text("Allowed", color = status.ready, style = MaterialTheme.typography.labelLarge)
            } else {
                FilledTonalButton(onClick = onGrant) { Text("Allow") }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.clip(MaterialTheme.shapes.large),
    )
}
