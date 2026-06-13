Prompt 1: Project Setup & Cloud Build Pipeline

    Objective: Set up a headless native Android repository that compiles via GitHub Actions.

    "I am building a native Android app using Kotlin and Jetpack Compose. I do not have a local Android SDK or Android Studio. I will build entirely via GitHub Actions.

    Please create the base file structure for a standard, modern Android project. I need:

        A project-level build.gradle.kts

        An app-module level build.gradle.kts with dependencies for Jetpack Compose, Jetpack Glance (widgets), and Preferences DataStore.

        settings.gradle.kts and a minimal AndroidManifest.xml.

        A boilerplate MainActivity.kt that displays a 'Hello World' Compose layout.

        A .github/workflows/build-android.yml file that triggers on a push to main. It should set up Java 21, cache Gradle, run ./gradlew assembleDebug, and upload the resulting debug .apk file as a downloadable build artifact."

Prompt 2: Core Storage & Folder Access

    Objective: Teach the app how to pick and remember a local storage directory.

    "Modify MainActivity.kt. Implement Android's Storage Access Framework (SAF) using Intent.ACTION_OPEN_DOCUMENT_TREE to let the user select a local directory/folder on their device (like an Obsidian vault).

    Once selected, persistently save the folder's folder tree URI and take persistent read/write permissions (contentResolver.takePersistableUriPermission). Store this URI string using Android Preferences DataStore. Update the MainActivity UI to display whether a vault folder is currently linked or not."

Prompt 3: Widget Structure & Instance Mapping

    Objective: Deploy a home screen widget and map multiple instances to different files.

    "Using Jetpack Glance, create a basic home screen widget (DraftWidget.kt) and its corresponding GlanceAppWidgetReceiver. The widget layout should feature a thin top navigation bar showing a filename, an 'Open app' icon button, and a 'Commit' checkmark icon button. The rest of the widget body should be a text container.

    Create a widget configuration Activity (WidgetConfigActivity.kt) that fires whenever a user drags a new widget instance to their home screen. This activity should read the folder URI from DataStore, prompt the user to type a new or existing filename (e.g., journal.md), and save the mapping of that specific appWidgetId to the filename in Preferences DataStore."

Prompt 4: The Input Overlay Sheet

    Objective: Create the seamless full-body edit experience using a translucent overlay.

    "Create a new Activity named WidgetEditActivity.kt. In AndroidManifest.xml, configure this activity with a translucent theme (@android:style/Theme.Translucent.NoTitleBar) and set android:windowSoftInputMode="adjustResize".

    Update the Jetpack Glance widget body container so that clicking anywhere on it launches WidgetEditActivity via a PendingIntent, passing the current appWidgetId.

    Inside WidgetEditActivity, use Jetpack Compose to build a full-screen layout:

        The upper portion of the screen is a semi-transparent, dimmed background that closes the activity when clicked.

        The lower portion contains a card-styled multi-line TextField that sits cleanly right above the keyboard.

        Use a FocusRequester to automatically pull up the virtual keyboard and focus this text field immediately upon launch.

        Load any existing uncommitted draft text from DataStore for this appWidgetId. As the user types, save their live input back to DataStore in real-time."

Prompt 5: Commit & Markdown Append Logic

    Objective: Execute the background file modification when hitting "Commit".

    "Implement the 'Commit' functionality for the widget. When the checkmark button in the widget's top nav bar is clicked, trigger an Android BroadcastReceiver or background worker.

    The background logic must:

        Retrieve the appWidgetId from the click intent.

        Fetch the corresponding uncommitted draft text and file name from DataStore.

        Resolve the target file using DocumentFile.fromTreeUri targeting the saved vault folder URI. If the file doesn't exist, create it.

        Format a current timestamp string using a customizable template (defaulting to \n[YYYY-MM-DD HH:mm]\n).

        Safely open a file output stream, append the timestamp, append the uncommitted text draft, and append a trailing newline.

        Clear the uncommitted draft from DataStore for that widget ID and trigger a widget UI refresh so the widget canvas returns to blank."

Prompt 6: Full App Editor View

    Objective: Connect the "Open" button to a full history editor in the main app.

    "Update the 'Open' button in the Jetpack Glance widget top navigation bar. When clicked, it should launch MainActivity, passing the file path or name associated with that widget instance.

    Update MainActivity.kt to handle this incoming intent. If a filename is passed, read the entire contents of that Markdown file from the linked storage tree and display it in a beautiful, full-screen scrollable text editor so the user can review or manually edit the whole file history."

    💡 Workflow Reminder: Put these prompts into a .txt file locally. Push your code to GitHub, grab the .apk from the Actions tab when it turns green, and deploy it to your phone using adb install app-debug.apk to test each step.