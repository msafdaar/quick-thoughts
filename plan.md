the build is failing with this error. please note that i dont have any dev tools installed on this machine. all the build commands are run on github actions. i need you to find out how to fix these errors
Run gradle assembleDebug
Welcome to Gradle 8.9!
Here are the highlights of this release:
 - Enhanced Error and Warning Messages
 - IDE Integration Improvements
 - Daemon JVM Information
For more details see https://docs.gradle.org/8.9/release-notes.html
Starting a Gradle Daemon (subsequent builds will be faster)
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors SKIPPED
> Task :app:generateDebugResValues
> Task :app:checkDebugAarMetadata
> Task :app:mapDebugSourceSetPaths
> Task :app:generateDebugResources
> Task :app:packageDebugResources
> Task :app:mergeDebugResources
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:parseDebugLocalResources
> Task :app:processDebugMainManifest
> Task :app:processDebugManifest
> Task :app:javaPreCompileDebug
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders NO-SOURCE
> Task :app:generateDebugAssets UP-TO-DATE
> Task :app:mergeDebugAssets
> Task :app:processDebugManifestForPackage
> Task :app:compressDebugAssets
> Task :app:desugarDebugFileDependencies
> Task :app:processDebugResources
> Task :app:checkDebugDuplicateClasses
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/MainActivity.kt:20:43 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/MainActivity.kt:37:13 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/MainActivity.kt:63:35 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/MainActivity.kt:65:31 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetConfigActivity.kt:5:43 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetConfigActivity.kt:20:31 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetConfigActivity.kt:34:54 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetConfigActivity.kt:39:28 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetEditActivity.kt:4:43 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetEditActivity.kt:37:13 Unresolved reference 'INVALID_APP_WIDGET_ID'.
e: file:///home/runner/work/quick-thoughts/quick-thoughts/app/src/main/java/com/example/quickthoughts/WidgetEditActivity.kt:40:28 Unresolved reference 'INVALID_APP_WIDGET_ID'.
> Task :app:compileDebugKotlin
> Task :app:mergeExtDexDebug
> Task :app:compileDebugKotlin FAILED
21 actionable tasks: 21 executed
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details
* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
BUILD FAILED in 2m 21s
Error: Process completed with exit code 1.