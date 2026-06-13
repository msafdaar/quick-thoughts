package com.example.quickthoughts

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManagerimport android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = INVALID_APP_WIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APP_WIDGET_ID
            )
        }

        // If this activity was started with an invalid widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APP_WIDGET_ID) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WidgetConfigScreen(appWidgetId = appWidgetId) {
                        val resultValue = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetConfigScreen(appWidgetId: Int, onFinished: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val vaultManager = remember { VaultManager(context) }
    var filename by remember { mutableStateOf("journal.md") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Configure Widget", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = filename,
            onValueChange = { filename = it },
            label = { Text("Filename (e.g., journal.md)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                scope.launch {
                    vaultManager.saveWidgetFilename(appWidgetId, filename)
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Add Widget")
        }
    }
}
