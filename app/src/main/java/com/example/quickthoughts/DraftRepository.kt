package com.example.quickthoughts

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

val Context.dataStore by preferencesDataStore(name = "settings")

class DraftRepository(private val context: Context) {
    private val FILE_URI_KEY = stringPreferencesKey("selected_file_uri")

    private val START_MARKER = "<!-- QUICK_THOUGHTS_DRAFT_START -->"
    private val END_MARKER = "<!-- QUICK_THOUGHTS_DRAFT_END -->"

    val selectedFileUri: Flow<Uri?> = context.dataStore.data.map { preferences ->
        preferences[FILE_URI_KEY]?.let { Uri.parse(it) }
    }

    suspend fun saveFileUri(uri: Uri) {
        context.dataStore.edit { preferences ->
            preferences[FILE_URI_KEY] = uri.toString()
        }
    }

    fun readDraft(uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val content = BufferedReader(InputStreamReader(inputStream)).readText()
                val startIndex = content.indexOf(START_MARKER)
                val endIndex = content.indexOf(END_MARKER)

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    content.substring(startIndex + START_MARKER.length, endIndex).trim()
                } else {
                    ""
                }
            } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun writeDraft(uri: Uri, draftText: String) {
        try {
            val fullContent = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: ""

            val startIndex = fullContent.indexOf(START_MARKER)
            val endIndex = fullContent.indexOf(END_MARKER)

            val newContent = if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                val before = fullContent.substring(0, startIndex + START_MARKER.length)
                val after = fullContent.substring(endIndex)
                "$before\n$draftText\n$after"
            } else {
                // If markers don't exist, append to the end
                val separator = if (fullContent.isNotEmpty() && !fullContent.endsWith("\n")) "\n\n" else if (fullContent.isNotEmpty()) "\n" else ""
                "$fullContent$separator$START_MARKER\n$draftText\n$END_MARKER"
            }

            context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(newContent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
