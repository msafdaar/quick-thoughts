package com.example.quickthoughts

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class CommitAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val appWidgetId = glanceId.toString().substringAfter("AppWidgetId(").substringBefore(")").toIntOrNull() ?: return
        val vaultManager = VaultManager(context)

        val vaultUriStr = vaultManager.vaultUriFlow.first() ?: return
        val filename = vaultManager.getFilenameFlow(appWidgetId).first() ?: return
        val draftText = vaultManager.getDraftFlow(appWidgetId).first() ?: ""

        if (draftText.isBlank()) return

        val vaultUri = Uri.parse(vaultUriStr)
        val tree = DocumentFile.fromTreeUri(context, vaultUri) ?: return

        // Find or create the file
        var file = tree.findFile(filename)
        if (file == null) {
            file = tree.createFile("text/markdown", filename)
        }

        if (file != null) {
            val timestamp = SimpleDateFormat("\n[yyyy-MM-dd HH:mm]\n", Locale.getDefault()).format(Date())
            val contentToAppend = timestamp + draftText + "\n"

            context.contentResolver.openOutputStream(file.uri, "wa")?.use { outputStream ->
                outputStream.write(contentToAppend.toByteArray())
            }

            // Clear draft and refresh widget
            vaultManager.saveDraft(appWidgetId, "")
            DraftWidget().updateAll(context)
        }
    }
}
