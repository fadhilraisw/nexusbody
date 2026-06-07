package com.rais.nexusbody.feature.dashboard.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.feature.dashboard.DashboardViewModel
import com.rais.nexusbody.feature.dashboard.DashboardUiState
import com.rais.nexusbody.feature.workout.ui.WorkoutLogScreen
import com.rais.nexusbody.feature.nutrition.ui.NutritionLogScreen
import com.rais.nexusbody.feature.health.ui.HealthAssessmentScreen
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import com.rais.nexusbody.data.local.entity.MedicationEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.launch

val textprimary = Color(0xFFFFFFFF)
val textsecondary = Color(0xB3FFFFFF)
val textmuted = Color(0x80FFFFFF)
val premiumaccent = Color(0xFFE65100)
val statusgood = Color(0xFF34C759)
val statuswarning = Color(0xFFFFCC00)
val statusdanger = Color(0xFFFF3B30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpatialDashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("dashboard") }
    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0D0F12),
                drawerShape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxHeight().width(280.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp)) {
                    Text("nexusbody", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("v2.0 medical grade", color = textmuted, fontSize = 10.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(48.dp))
                    val items = listOf(
                        "dashboard" to "◈",
                        "health" to "⚲",
                        "nutrition" to "▦",
                        "workout" to "⚡",
                        "quest" to "🎯",
                        "ai report" to "✦"
                    )
                    items.forEach { (id, icon) ->
                        val isSelected = currentScreen == id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color.White.copy(0.05f) else Color.Transparent)
                                .clickable {
                                    currentScreen = id
                                    scope.launch { drawerState.close() }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(icon, color = if (isSelected) premiumaccent else textmuted, fontSize = 20.sp)
                            Spacer(Modifier.width(16.dp))
                            Text(id, color = if (isSelected) textprimary else textsecondary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF08090A)).drawBehind {
                drawCircle(Brush.radialGradient(listOf(Color(0xFF1A2233), Color.Transparent)), radius = size.width, center = Offset(size.width * 0.2f, 0f))
                drawCircle(Brush.radialGradient(listOf(Color(0xFF2E1E18), Color.Transparent)), radius = size.width, center = Offset(size.width, size.height))
            })

            Column(modifier = Modifier.fillMaxSize()) {
                SpatialTopBar(
                    title = currentScreen,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = premiumaccent)
                    }
                } else {
                    Crossfade(targetState = currentScreen, label = "screen_routing") { screen ->
                        when (screen) {
                            "dashboard" -> DashboardContent(uiState)
                            "health" -> HealthAssessmentScreen()
                            "nutrition" -> NutritionLogScreen()
                            "workout" -> WorkoutLogScreen()
                            "quest" -> com.rais.nexusbody.feature.gamification.ui.QuestForgerScreen()
                            "ai report" -> com.rais.nexusbody.feature.ai_report.ui.AiClinicalReportScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(uiState: DashboardUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { OverallLevelHeader(uiState.rank, uiState.streak) }

        // Medication Reminder Section
        item {
            val activeMeds = uiState.activeMedications
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("medication reminder", color = textmuted, fontSize = 11.sp)
                        if (activeMeds.isEmpty()) {
                            Text("belum ada jadwal obat aktif", color = textprimary, fontSize = 14.sp)
                        } else {
                            activeMeds.forEach { med ->
                                Text("${med.name} (${med.scheduledTimes.joinToString(", ")})", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (activeMeds.isNotEmpty()) {
                        Icon(Icons.Default.Notifications, null, tint = premiumaccent, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        item { HealthBriefSection(uiState.latestHealth) }
        item { NutritionBriefSection(uiState.totalCalories) }
        item { WorkoutBriefSection(uiState.latestWorkout) }
        item { AiReportBriefSection() }
    }
}

@Composable
private fun SpatialTopBar(title: String, onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(20.dp, 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Text("≡", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraLight)
        }
        Text(title, color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.1f)).border(1.dp, Color.White.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Text("R", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OverallLevelHeader(rank: String, streak: Int) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("overall rank", color = textmuted, fontSize = 12.sp, letterSpacing = 1.sp)
                Text("unranked", color = premiumaccent, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                Text("0 day streak", color = textsecondary, fontSize = 12.sp)
            }
            Box(modifier = Modifier.size(64.dp).background(Color.White.copy(0.05f), CircleShape).border(1.dp, premiumaccent.copy(0.5f), CircleShape), contentAlignment = Alignment.Center) {
                Text("🏆", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun HealthBriefSection(latest: HealthAssessmentEntity?) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚲", color = premiumaccent, fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Text("medical & health brief", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("vital status", color = textmuted, fontSize = 11.sp)
                    val systolic = latest?.systolicBp
                    val diastolic = latest?.diastolicBp
                    Text(if(systolic != null) "$systolic/$diastolic" else "--", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Text("weight", color = textmuted, fontSize = 11.sp)
                    Text(latest?.weightKg?.let { "${it}kg" } ?: "--kg", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Text("body fat", color = textmuted, fontSize = 11.sp)
                    Text(latest?.bodyFatPercentage?.let { "${it}%" } ?: "--%", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("visceral fat", color = textmuted, fontSize = 11.sp)
                    Text(latest?.visceralFatLevel?.let { "Lvl $it" } ?: "Lvl --", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun NutritionBriefSection(totalCalories: Int) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("▦", color = statusgood, fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("nutrition brief", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("Daily Goal", color = statusgood, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$totalCalories kcal", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("total intake today", color = textmuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun WorkoutBriefSection(latest: WorkoutSessionEntity?) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡", color = premiumaccent, fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Text("training brief", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("last routine", color = textmuted, fontSize = 11.sp)
                    Text(latest?.routineName ?: "--", color = textprimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
                Box(modifier = Modifier.background(Color.White.copy(0.05f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("duration", color = textmuted, fontSize = 10.sp)
                        Text(latest?.totalDurationMinutes?.let { "${it}m" } ?: "--m", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiReportBriefSection() {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✦", color = Color.White, fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("ai clinical synthesis", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Text("belum ada data report ai. lakukan aktivitas untuk memancing ai menganalisa.", color = textmuted, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}