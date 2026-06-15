package com.example.quickthoughts

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val repository: DraftRepository) : ViewModel() {
    var selectedUri by mutableStateOf<Uri?>(null)
        private set

    var draftText by mutableStateOf("")
    
    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            repository.selectedFileUri.collectLatest { uri ->
                selectedUri = uri
                if (uri != null) {
                    loadDraft(uri)
                }
            }
        }
    }

    fun onFileSelected(uri: Uri) {
        viewModelScope.launch {
            repository.saveFileUri(uri)
        }
    }

    private fun loadDraft(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val text = repository.readDraft(uri)
            launch(Dispatchers.Main) {
                draftText = text
            }
        }
    }

    fun onDraftChange(newText: String) {
        draftText = newText
        
        // Debounce saving to disk
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1000) // Wait 1 second after last change
            saveDraft()
        }
    }

    fun saveDraft() {
        val uri = selectedUri ?: return
        repository.writeDraft(uri, draftText)
    }
}
