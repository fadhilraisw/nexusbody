package com.rais.nexusbody

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rais.nexusbody.core.ui.theme.NexusBodyTheme
import com.rais.nexusbody.feature.dashboard.ui.SpatialDashboardScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusBodyTheme {
                SpatialDashboardScreen()
            }
        }
    }
}