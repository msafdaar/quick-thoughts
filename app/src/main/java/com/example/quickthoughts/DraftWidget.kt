package com.example.quickthoughts

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.Button
import androidx.glance.action.actionStartActivity
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.color.ColorProvider
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider

import androidx.glance.state.GlanceStateDefinition
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentGlanceId
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.flow.first

import androidx.glance.action.actionParametersOf
import androidx.glance.action.ActionParameters
import android.appwidget.AppWidgetManager
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity

import androidx.glance.appwidget.action.actionRunCallback

class DraftWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideContent(context: Context, id: GlanceId) {
        val vaultManager = VaultManager(context)
        // Extract the actual widget ID from GlanceId
        val appWidgetId = id.toString().substringAfter("AppWidgetId(").substringBefore(")").toIntOrNull() ?: -1
        val filename = vaultManager.getFilenameFlow(appWidgetId).first() ?: "Unknown"
        val draftText = vaultManager.getDraftFlow(appWidgetId).first() ?: ""

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(Color.White)
                    .cornerRadius(16.dp)
                    .padding(8.dp)
            ) {
                // Top Nav Bar
                Row(
                    modifier = GlanceModifier.fillMaxWidth().height(40.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = filename,
                        modifier = GlanceModifier.defaultWeight(),
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Button(
                        text = "O",
                        onClick = actionStartActivity<MainActivity>(
                            actionParametersOf(
                                ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID) to appWidgetId
                            )
                        )
                    )
                    Button(text = "C", onClick = actionRunCallback<CommitAction>())
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Widget Body
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color(0xFFF0F0F0))
                        .padding(8.dp)
                        .clickable(
                            actionStartActivity<WidgetEditActivity>(
                                actionParametersOf(
                                    ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID) to appWidgetId
                                )
                            )
                        )
                ) {
                    Text(text = if (draftText.isEmpty()) "Tap to write..." else draftText)
                }
            }
        }
    }
}
