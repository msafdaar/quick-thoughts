package com.example.quickthoughts

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APP_WIDGET_ID
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.glance.appwidget.updateAll

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            INVALID_APP_WIDGET_ID
        )

        if (appWidgetId == INVALID_APP_WIDGET_ID) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                EditOverlayScreen(appWidgetId = appWidgetId, onDismiss = { finish() })
            }
        }
    }
}

@Composable
fun EditOverlayScreen(appWidgetId: Int, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vaultManager = remember { VaultManager(context) }
    val focusRequester = remember { FocusRequester() }
    
    var text by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(appWidgetId) {
        withContext(Dispatchers.IO) {
            val vaultUriStr = vaultManager.vaultUriFlow.first()
            val filename = vaultManager.getFilenameFlow(appWidgetId).first()
            
            if (vaultUriStr != null && filename != null) {
                val vaultUri = Uri.parse(vaultUriStr)
                val tree = DocumentFile.fromTreeUri(context, vaultUri)
                val file = tree?.findFile(filename)
                
                if (file != null) {
                    context.contentResolver.openInputStream(file.uri)?.use { input ->
                        val fullText = input.bufferedReader().readText()
                        text = FileHelper.extractDraftFromFile(fullText)
                    }
                }
            }
            isLoaded = true
        }
    }

    val saveAndDismiss = {
        scope.launch {
            withContext(Dispatchers.IO) {
                val vaultUriStr = vaultManager.vaultUriFlow.first()
                val filename = vaultManager.getFilenameFlow(appWidgetId).first()
                
                if (vaultUriStr != null && filename != null) {
                    val vaultUri = Uri.parse(vaultUriStr)
                    val tree = DocumentFile.fromTreeUri(context, vaultUri)
                    var file = tree?.findFile(filename)
                    
                    if (file == null && tree != null) {
                        file = tree.createFile("text/markdown", filename)
                    }
                    
                    if (file != null) {
                        val fullText = context.contentResolver.openInputStream(file.uri)?.use { 
                            it.bufferedReader().readText()
                        } ?: ""
                        
                        val updatedText = FileHelper.updateFileWithDraft(fullText, text)
                        
                        context.contentResolver.openOutputStream(file.uri)?.use { output ->
                            output.write(updatedText.toByteArray())
                        }
                        DraftWidget().updateAll(context)
                    }
                }
            }
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { saveAndDismiss() }
    ) {
        if (isLoaded) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp)
                    .clickable(enabled = false) { },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .focusRequester(focusRequester),
                        placeholder = { Text("What's on your mind?") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                    
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }
}
