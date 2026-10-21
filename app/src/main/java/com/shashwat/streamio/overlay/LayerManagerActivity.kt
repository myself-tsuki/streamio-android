package com.shashwat.streamio.overlay

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shashwat.streamio.screen.ScreenService

/** Overlay library: create compositions, choose the live one, then edit it in Studio. */
class LayerManagerActivity : AppCompatActivity() {
  private var projectsState by mutableStateOf<List<OverlayProject>>(emptyList())
  private var activeIdState by mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.statusBarColor = android.graphics.Color.BLACK
    window.navigationBarColor = android.graphics.Color.BLACK

    refresh()

    setContent {
      CupertinoLayerManagerScreen(
        projects = projectsState,
        activeId = activeIdState,
        onCreateProject = {
          openStudio(OverlayProjectManager.create(this).id)
        },
        onEditProject = { project ->
          openStudio(project.id)
        },
        onToggleEnabled = { project, enabled ->
          OverlayProjectManager.setEnabled(this, project.id, enabled)
          applyLiveOverlay()
          refresh()
          Toast.makeText(
            this,
            if (enabled) "${project.name} is live" else "Overlay turned off",
            Toast.LENGTH_SHORT
          ).show()
        },
        onDeleteProject = { project ->
          OverlayProjectManager.delete(this, project.id)
          applyLiveOverlay()
          refresh()
        },
        onBack = { finish() }
      )
    }
  }

  override fun onResume() {
    super.onResume()
    refresh()
  }

  private fun refresh() {
    projectsState = OverlayProjectManager.load(this)
    activeIdState = OverlayProjectManager.activeId(this)
  }

  private fun openStudio(id: String) {
    startActivity(Intent(this, OverlayStudioActivity::class.java).putExtra(OverlayStudioActivity.EXTRA_PROJECT_ID, id))
  }

  private fun applyLiveOverlay() {
    runCatching {
      ScreenService.INSTANCE?.applyConfiguredOverlay()
    }
  }
}
