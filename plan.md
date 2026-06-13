Context: > We have a working Android app built with Kotlin, Jetpack Compose, Jetpack Glance, and Preferences DataStore. Currently, when a user types a draft in WidgetEditActivity, it is saved as a string in Preferences DataStore mapped to the appWidgetId. When they click 'Commit', the app reads that string from DataStore, appends it to the mapped .md file with a timestamp, and clears the DataStore entry. The home screen widget displays the draft string fetched from DataStore.

Objective:
Refactor the application so that the uncommitted draft text is stored directly inside the target .md file itself, rather than in Preferences DataStore. We will use HTML comments as delimiters: and. DataStore should only be used to remember the mapping of appWidgetId to the file path/URI.

Please modify the codebase according to the following requirements:
1. Create File Helper Utilities

Implement helper functions to read and write text to a file via its URI using Android's Storage Access Framework:

    extractDraftFromFile(fileText: String): String: Extracts and returns all text sitting between and. Return an empty string if tags aren't found or if the space between them is empty.

    updateFileWithDraft(fileText: String, newDraft: String): String: If the draft tags exist, replace everything between them with newDraft. If they do not exist, append \n\n\nnewDraft\n to the very end of the file text.

2. Refactor WidgetEditActivity.kt (The Input Overlay)

    On Launch: Stop reading the live draft from DataStore. Instead, resolve the target .md file using the saved folder URI and filename, read its full text content, and use the helper to extract the draft string. Load this string into the Compose TextField.

    On Close/Exit (onStop or background dim click): Take the current value of the TextField. In a background thread, read the target file, apply the updateFileWithDraft helper with the new text, overwrite the file via the file output stream, and trigger a Jetpack Glance widget refresh. Remove any local draft writing to DataStore.

3. Refactor the Widget View (DraftWidget.kt)

    Modify the widget's content provider layout logic. Instead of observing or pulling the draft string from DataStore to display it in the main widget text area, read the target .md file in the background, extract the text between the draft HTML tags, and display that text. If it's empty, display a subtle placeholder like "Tap to sketch a thought...".

4. Refactor the "Commit" Logic (BroadcastReceiver / Worker)

When the checkmark button in the widget nav bar is clicked:

    Read the target .md file text.

    Extract the uncommitted draft string from between the tags. If the draft is completely blank, abort the operation.

    Perform a permanent append/transform:

        Remove the draft tags and the text inside them from their current position.

        Format a current timestamp string (e.g., \n[YYYY-MM-DD HH:mm]\n).

        Append the timestamp and the extracted draft text into the main permanent history area of the file.

        Re-append clean, empty draft boundaries (\n) to the very end of the file.

    Overwrite the file with this finalized text layout and force-refresh the widget view so it goes blank.

Ensure all file system operations run safely on a background dispatcher (like Dispatchers.IO) to avoid UI stuttering. Let's make these updates step-by-step. Let me know which file you want to refactor first.