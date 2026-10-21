package com.shashwat.streamio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.shashwat.streamio.file.CupertinoFileStreamScreen
import com.shashwat.streamio.file.FileStreamingActivity
import com.shashwat.streamio.file.VideoQueueItem
import com.shashwat.streamio.settings.StreamSettingsActivity

class FileStreamActivity : AppCompatActivity() {

    private var playlist by mutableStateOf<List<VideoQueueItem>>(emptyList())
    private var isLoopEnabled by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openFilePicker()
        } else {
            Toast.makeText(this, "Storage permission required to select video files", Toast.LENGTH_LONG).show()
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val newItems = uris.map { uri ->
                VideoQueueItem(uri = uri, name = getFileName(uri))
            }
            playlist = playlist + newItems
            Toast.makeText(this, "Added ${newItems.size} video(s) to sequence", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        setContent {
            CupertinoFileStreamScreen(
                videos = playlist,
                isLoopEnabled = isLoopEnabled,
                onAddVideos = { checkPermissionAndPickFile() },
                onMoveUp = { moveUp(it) },
                onMoveDown = { moveDown(it) },
                onRemoveVideo = { removeVideo(it) },
                onClearAll = { clearAll() },
                onToggleLoop = { isLoopEnabled = it },
                onStartStream = { startFileStreaming() },
                onOpenSettings = {
                    val intent = Intent(this, StreamSettingsActivity::class.java)
                    startActivity(intent)
                },
                onBack = { finish() }
            )
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (_: Exception) {}
        if (name.isEmpty()) {
            name = uri.lastPathSegment?.substringAfterLast('/') ?: "Video_${System.currentTimeMillis()}"
        }
        return name
    }

    private fun moveUp(index: Int) {
        if (index > 0 && index < playlist.size) {
            val mutable = playlist.toMutableList()
            val item = mutable.removeAt(index)
            mutable.add(index - 1, item)
            playlist = mutable
        }
    }

    private fun moveDown(index: Int) {
        if (index >= 0 && index < playlist.size - 1) {
            val mutable = playlist.toMutableList()
            val item = mutable.removeAt(index)
            mutable.add(index + 1, item)
            playlist = mutable
        }
    }

    private fun removeVideo(index: Int) {
        if (index in playlist.indices) {
            val mutable = playlist.toMutableList()
            mutable.removeAt(index)
            playlist = mutable
        }
    }

    private fun clearAll() {
        playlist = emptyList()
    }

    private fun checkPermissionAndPickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openFilePicker()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openFilePicker()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openFilePicker()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(this, "Storage permission is needed to access video files", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch("video/*")
    }

    private fun startFileStreaming() {
        if (playlist.isEmpty()) {
            Toast.makeText(this, "Please select at least one video file", Toast.LENGTH_SHORT).show()
            return
        }

        val uriStrings = ArrayList(playlist.map { it.uri.toString() })
        val intent = Intent(this, FileStreamingActivity::class.java).apply {
            putStringArrayListExtra(FileStreamingActivity.EXTRA_PLAYLIST_URIS, uriStrings)
            putExtra(FileStreamingActivity.EXTRA_VIDEO_URI, uriStrings.first())
            putExtra(FileStreamingActivity.EXTRA_LOOP_ENABLED, isLoopEnabled)
        }
        startActivity(intent)
    }
} 