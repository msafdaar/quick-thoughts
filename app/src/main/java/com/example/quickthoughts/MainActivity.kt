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
import kotlinx.coroutines.launch

import android.appwidget.AppWidgetManager
import androidx.documentfile.provider.DocumentFile
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.flow.first

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APP_WIDGET_ID
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(appWidgetId)
                }
            }
        }
    }
}

@Composable
fun MainNavigation(targetWidgetId: Int) {
    val context = LocalContext.current
    val vaultManager = remember { VaultManager(context) }
    val vaultUri by vaultManager.vaultUriFlow.collectAsState(initial = null)
    
    var currentWidgetId by remember { mutableIntStateOf(targetWidgetId) }

    if (vaultUri == null) {
        VaultSetupScreen(vaultManager, vaultUri)
    } else if (currentWidgetId != AppWidgetManager.INVALID_APP_WIDGET_ID) {
        FileEditorScreen(vaultManager, vaultUri!!, currentWidgetId) {
            currentWidgetId = AppWidgetManager.INVALID_APP_WIDGET_ID
        }
    } else {
        VaultStatusScreen(vaultManager, vaultUri)
    }
}

@Composable
fun VaultSetupScreen(vaultManager: VaultManager, vaultUri: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(it, takeFlags)

            scope.launch {
                vaultManager.saveVaultUri(it.toString())
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Quick Thoughts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { launcher.launch(null) }) {
            Text("Link Vault Folder")
        }
    }
}

@Composable
fun VaultStatusScreen(vaultManager: VaultManager, vaultUri: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(it, takeFlags)
            scope.launch { vaultManager.saveVaultUri(it.toString()) }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Vault Linked!", style = MaterialTheme.typography.headlineMedium)
        Text(vaultUri ?: "", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { launcher.launch(null) }) {
            Text("Change Vault Folder")
        }
    }
}

@Composable
fun FileEditorScreen(
    vaultManager: VaultManager,
    vaultUriStr: String,
    appWidgetId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var filename by remember { mutableStateOf("Loading...") }
    var fileContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(appWidgetId) {
        val name = vaultManager.getFilenameFlow(appWidgetId).first() ?: "Unknown"
        filename = name
        
        val vaultUri = Uri.parse(vaultUriStr)
        val tree = DocumentFile.fromTreeUri(context, vaultUri)
        val file = tree?.findFile(name)
        
        if (file != null) {
            context.contentResolver.openInputStream(file.uri)?.use { input ->
                fileContent = input.bufferedReader().readText()
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filename) },
                navigationIcon = {
                    Button(onClick = onBack) { Text("<") }
                },
                actions = {
                    Button(onClick = {
                        scope.launch {
                            val vaultUri = Uri.parse(vaultUriStr)
                            val tree = DocumentFile.fromTreeUri(context, vaultUri)
                            val file = tree?.findFile(filename)
                            if (file != null) {
                                context.contentResolver.openOutputStream(file.uri, "w")?.use { output ->
                                    output.write(fileContent.toByteArray())
                                }
                            }
                        }
                    }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            TextField(
                value = fileContent,
                onValueChange = { fileContent = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}
