package com.example.quickthoughts

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class VaultManager(private val context: Context) {

    companion object {
        val VAULT_URI_KEY = stringPreferencesKey("vault_uri")
    }

    val vaultUriFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[VAULT_URI_KEY]
        }

    suspend fun saveVaultUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[VAULT_URI_KEY] = uri
        }
    }

    fun getFilenameFlow(appWidgetId: Int): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("widget_$appWidgetId")]
        }

    suspend fun saveWidgetFilename(appWidgetId: Int, filename: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("widget_$appWidgetId")] = filename
        }
    }

    fun getDraftFlow(appWidgetId: Int): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("draft_$appWidgetId")]
        }

    suspend fun saveDraft(appWidgetId: Int, text: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("draft_$appWidgetId")] = text
        }
    }
}
