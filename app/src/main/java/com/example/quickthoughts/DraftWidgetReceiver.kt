package com.example.quickthoughts

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DraftWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DraftWidget()
}
