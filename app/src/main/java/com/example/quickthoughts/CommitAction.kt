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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommitAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val appWidgetId = glanceId.toString().substringAfter("AppWidgetId(").substringBefore(")").toIntOrNull() ?: return
        val vaultManager = VaultManager(context)

        withContext(Dispatchers.IO) {
            val vaultUriStr = vaultManager.vaultUriFlow.first() ?: return@withContext
            val filename = vaultManager.getFilenameFlow(appWidgetId).first() ?: return@withContext

            val vaultUri = Uri.parse(vaultUriStr)
            val tree = DocumentFile.fromTreeUri(context, vaultUri) ?: return@withContext
            val file = tree.findFile(filename) ?: return@withContext

            val fullText = context.contentResolver.openInputStream(file.uri)?.use { 
                it.bufferedReader().readText()
            } ?: return@withContext

            val timestamp = SimpleDateFormat("\n[yyyy-MM-dd HH:mm]\n", Locale.getDefault()).format(Date())
            val updatedText = FileHelper.commitDraft(fullText, timestamp)

            if (updatedText != null) {
                context.contentResolver.openOutputStream(file.uri)?.use { output ->
                    output.write(updatedText.toByteArray())
                }
                DraftWidget().updateAll(context)
            }
        }
    }
}
