package com.example.quickthoughts

import android.appwidget.AppWidgetManager
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

class WidgetEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APP_WIDGET_ID
        )

        if (appWidgetId == AppWidgetManager.INVALID_APP_WIDGET_ID) {
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
    
    // Initial draft text from DataStore
    val initialDraft by vaultManager.getDraftFlow(appWidgetId).collectAsState(initial = "")
    var text by remember(initialDraft) { mutableStateOf(initialDraft ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding() // Adjust for keyboard
                .padding(16.dp)
                .clickable(enabled = false) { /* Prevent dismissal when clicking card */ },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = text,
                    onValueChange = { 
                        text = it
                        scope.launch {
                            vaultManager.saveDraft(appWidgetId, it)
                        }
                    },
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
