### 📋 Copy-and-Paste Prompt for Your Coding Agent

> We have successfully fixed the build pipeline infrastructure, but the Kotlin compilation is failing due to some import mismatches, unresolved references, and legacy Jetpack Glance API usage.
> Please scan the codebase and apply these 4 specific structural fixes immediately:
> #### 1. Fix `DocumentFile` Imports & Dependencies
> 
> 
> * In **`app/build.gradle.kts`**, make sure this dependency is explicitly added:
> `implementation("androidx.documentfile:documentfile:1.0.1")`
> * In `CommitAction.kt`, `DraftWidget.kt`, `MainActivity.kt`, and `WidgetEditActivity.kt`, fix the broken `documentfile` imports. The correct, case-sensitive import is:
> `import androidx.documentfile.provider.DocumentFile`
> * Ensure any calls trying to resolve `.uri` on a `DocumentFile` object are using the proper getter method: `.uri` (and verify the variable itself is not shadowed or un-scoped).
> 
> 
> #### 2. Fix Missing Widget ID Constants
> 
> 
> * In `MainActivity.kt`, `WidgetConfigActivity.kt`, and `WidgetEditActivity.kt`, you are using `INVALID_APP_WIDGET_ID` without importing it. Add this explicit import to the top of those files:
> `import android.appwidget.AppWidgetManager.INVALID_APP_WIDGET_ID`
> 
> 
> #### 3. Fix `ColorProvider` Conflicts in `DraftWidget.kt`
> 
> 
> * You have duplicate/ambiguous imports for `ColorProvider`. Remove any standard Compose color imports from `DraftWidget.kt` that conflict, and strictly use the Glance unit provider:
> `import androidx.glance.unit.ColorProvider`
> 
> 
> #### 4. Update `DraftWidget.kt` to Modern Jetpack Glance API (v1.1.1)
> 
> 
> * The widget class is failing because it's mixing up old Glance syntax with the new version. Rewrite the core of `DraftWidget` to implement `GlanceAppWidget()` properly using `provideGlance`:
> ```kotlin
> class DraftWidget : GlanceAppWidget() {
>     override suspend fun provideGlance(context: Context, id: GlanceId) {
>         provideContent {
>             // Your Glance Widget Composable layout code goes here
>         }
>     }
> }
> 
> ```
> 
> 
> * Remove any legacy `override fun provideContent()` or raw `Content()` overrides that do not match this modern template.
> 
> 
> Go ahead and update these files sequentially, ensure the imports match perfectly, and let me know when you are done so I can re-run the build.

---
