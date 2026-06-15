const DRAFT_START = '--Draft Start';
const DRAFT_END = 'Draft End--';
const WRAPPER_REGEX = /--Draft Start([\s\S]*)Draft End--/;

let currentFileUri = null;
let fileContent = '';
let saveTimeout = null;

document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);
    
    document.getElementById('btn-pick').addEventListener('click', pickFile);
    document.getElementById('draft-area').addEventListener('input', onDraftInput);

    // Load saved URI
    currentFileUri = localStorage.getItem('selectedFileUri');
    if (currentFileUri) {
        loadFile(currentFileUri);
    } else {
        setStatus('No file selected. Please select a .md file.');
    }
}

async function pickFile() {
    try {
        const uri = await window.cordova.plugins.safMediastore.selectFile();
        console.log('Selected URI:', uri);
        currentFileUri = uri;
        localStorage.setItem('selectedFileUri', uri);
        loadFile(uri);
    } catch (err) {
        console.error('Error picking file:', err);
        setStatus('Error selecting file: ' + err);
    }
}

async function loadFile(uri) {
    setStatus('Loading file...');
    try {
        const arrayBuffer = await window.cordova.plugins.safMediastore.readFile(uri);
        const decoder = new TextDecoder('utf-8');
        fileContent = decoder.decode(new Uint8Array(arrayBuffer));
        
        displayDraft(fileContent);
        setStatus('File loaded.');
        
        document.getElementById('file-info').classList.remove('hidden');
        document.getElementById('file-name').textContent = 'File: ' + (uri.split('/').pop() || 'Selected Markdown');
    } catch (err) {
        console.error('Error loading file:', err);
        setStatus('Error loading file. It might have been moved or deleted.');
    }
}

function displayDraft(content) {
    const match = content.match(WRAPPER_REGEX);
    if (match) {
        document.getElementById('draft-area').value = match[1].trim();
    } else {
        document.getElementById('draft-area').value = '';
    }
}

function onDraftInput() {
    if (!currentFileUri) return;
    
    setStatus('Draft modified (auto-saving soon...)');
    
    if (saveTimeout) clearTimeout(saveTimeout);
    saveTimeout = setTimeout(saveFile, 1500); // Save after 1.5s of inactivity
}

async function saveFile() {
    if (!currentFileUri) return;

    const draftText = document.getElementById('draft-area').value;
    const newDraftBlock = `\n${DRAFT_START}\n${draftText}\n${DRAFT_END}`;
    
    let newContent = '';
    if (WRAPPER_REGEX.test(fileContent)) {
        newContent = fileContent.replace(WRAPPER_REGEX, `${DRAFT_START}\n${draftText}\n${DRAFT_END}`);
    } else {
        newContent = fileContent.trim() + '\n' + newDraftBlock;
    }

    try {
        const base64Data = bufferToBase64(new TextEncoder().encode(newContent));
        const params = {
            uri: currentFileUri,
            data: base64Data
        };
        
        await window.cordova.plugins.safMediastore.overwriteFile(params);
        fileContent = newContent; // Update local copy
        setStatus('Changes saved.');
    } catch (err) {
        console.error('Error saving file:', err);
        setStatus('Error saving changes: ' + err);
    }
}

function bufferToBase64(buffer) {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

function setStatus(msg) {
    document.getElementById('status-bar').textContent = msg;
}
