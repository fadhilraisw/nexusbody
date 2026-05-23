package com.rais.nexusbody.feature.ai_report.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiClinicalReportScreen() {
    var showGenerateSheet by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<String?>(null) }

    // data dikosongkan
    val reports = emptyList<Map<String, String>>()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Text("✦ ai clinical synthesis", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("analisa lintas-disiplin (medis, gizi, olahraga)", color = textsecondary, fontSize = 12.sp)
            }

            if (reports.isEmpty()) {
                item {
                    Text("belum ada report ai. klik tombol bintang di bawah untuk generate.", color = textmuted, fontSize = 12.sp)
                }
            } else {
                items(reports) { report ->
                    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { selectedReport = report["summary"] }) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(report["type"]!!, color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(report["date"]!!, color = textmuted, fontSize = 11.sp)
                            }
                            Text(report["summary"]!!, color = textprimary, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showGenerateSheet = true },
            containerColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Default.Star, "generate report", tint = Color.Black)
        }

        if (showGenerateSheet) {
            ModalBottomSheet(onDismissRequest = { showGenerateSheet = false }, containerColor = Color(0xFF0D0F12)) {
                GenerateReportForm { showGenerateSheet = false }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenerateReportForm(onDismiss: () -> Unit) {
    var customPrompt by remember { mutableStateOf("") }
    var selectedTarget by remember { mutableStateOf("holistic") }
    val targets = listOf("holistic", "sports doctor", "clinical nutritionist", "hypertrophy pt")

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("generate ai synthesis", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }

        item {
            Text("target pembaca report", color = textsecondary, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                targets.forEach { target ->
                    val isSel = selectedTarget == target
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(isSel) premiumaccent else Color.White.copy(0.05f)).clickable { selectedTarget = target }.padding(12.dp, 6.dp)) {
                        Text(target, color = if(isSel) Color.White else textsecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            PremiumGlassCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("system prompt (tersembunyi)", color = statusgood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("ai akan mengekstrak: 1. data pagd & obat 2. makro & mikro gizi 3. volume & otot spesifik. mencari korelasi anomali.", color = textsecondary, fontSize = 11.sp)
                }
            }
        }

        item {
            PremiumTextField(value = customPrompt, onValueChange = { customPrompt = it }, label = "custom prompt (keluhan spesifik, target, dll)", isMultiline = true)
        }

        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text("generate report kelas medis", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}