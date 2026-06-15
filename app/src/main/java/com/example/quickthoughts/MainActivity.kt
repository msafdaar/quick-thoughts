package com.example.quickthoughts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = DraftRepository(applicationContext)
        
        setContent {
            mainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(repository) as T
                    }
                }
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                QuickThoughtsApp(mainViewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mainViewModel.isInitialized) {
            mainViewModel.saveDraft()
        }
    }
}

@Composable
fun QuickThoughtsApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedUri = viewModel.selectedUri
    val draftText = viewModel.draftText

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, flags)
            viewModel.onFileSelected(it)
        }
    }

    if (selectedUri == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No file selected", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { filePickerLauncher.launch(arrayOf("text/*", "application/octet-stream")) }) {
                    Text("Select Markdown File")
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editing Draft",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { filePickerLauncher.launch(arrayOf("text/*", "application/octet-stream")) }) {
                    Text("Change File")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = draftText,
                onValueChange = { viewModel.onDraftChange(it) },
                modifier = Modifier.fillMaxSize().weight(1f),
                placeholder = { Text("Type your thoughts here...") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        }
    }
}
