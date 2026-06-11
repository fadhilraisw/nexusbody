package com.rais.nexusbody.feature.ai_report.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*
import com.rais.nexusbody.feature.ai_report.AiReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiClinicalReportScreen(viewModel: AiReportViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showGenerateSheet by remember { mutableStateOf(false) }
    var selectedReportId by remember { mutableStateOf<String?>(null) }
    var showModelSelector by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("✦ ai clinical synthesis", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("analisa lintas-disiplin (medis, gizi, olahraga)", color = textsecondary, fontSize = 12.sp)
                    }
                    IconButton(onClick = { showModelSelector = true }) {
                        Icon(Icons.Default.Settings, "Pilih Model AI", tint = premiumaccent)
                    }
                }
            }

            item {
                PremiumGlassCard {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖 Active Model:", color = textmuted, fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(state.selectedModel, color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (state.reports.isEmpty()) {
                item {
                    Text("belum ada report ai. klik tombol bintang di bawah untuk generate.", color = textmuted, fontSize = 12.sp)
                }
            } else {
                items(state.reports) { report ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { selectedReportId = report.id }) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(report.reportType.uppercase(), color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(sdf.format(Date(report.timestamp)), color = textmuted, fontSize = 11.sp)
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = { viewModel.deleteReport(report.id) }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Text(report.summary, color = textprimary, fontSize = 13.sp, lineHeight = 20.sp, maxLines = 3)
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
                GenerateReportForm(
                    onGenerate = { prompt, target ->
                        viewModel.generateSynthesis(prompt, target)
                        showGenerateSheet = false
                    },
                    onDismiss = { showGenerateSheet = false }
                )
            }
        }

        if (selectedReportId != null) {
            val report = state.reports.find { it.id == selectedReportId }
            if (report != null) {
                ModalBottomSheet(onDismissRequest = { selectedReportId = null }, containerColor = Color(0xFF0D0F12)) {
                    ReportDetailSheet(report) { selectedReportId = null }
                }
            }
        }

        if (showModelSelector) {
            ModalBottomSheet(onDismissRequest = { showModelSelector = false }, containerColor = Color(0xFF0D0F12)) {
                AiModelSelector(
                    currentModel = state.selectedModel,
                    onModelSelected = { 
                        viewModel.setAiModel(it)
                        showModelSelector = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenerateReportForm(onGenerate: (String, String) -> Unit, onDismiss: () -> Unit) {
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
            Button(
                onClick = { onGenerate(customPrompt, selectedTarget) }, 
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("generate report kelas medis", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun AiModelSelector(currentModel: String, onModelSelected: (String) -> Unit) {
    val modelGroups = mapOf(
        "Google: Generation 3 (Preview)" to listOf(
            "gemini-3.1-pro-preview" to "Gemini 3.1 Pro Preview",
            "gemini-3.1-flash-lite" to "Gemini 3.1 Flash Lite",
            "gemini-3-pro-preview" to "Gemini 3 Pro Preview",
            "deep-research-max-preview-04-2026" to "Deep Research Max"
        ),
        "Google: Generation 2.5 & 2.0" to listOf(
            "gemini-2.5-pro" to "Gemini 2.5 Pro",
            "gemini-2.5-flash" to "Gemini 2.5 Flash",
            "gemini-2.0-flash" to "Gemini 2.0 Flash"
        ),
        "Groq: Llama & Qwen" to listOf(
            "llama-3.3-70b-versatile" to "Llama 3.3 70B",
            "meta-llama/llama-4-scout-17b-16e-instruct" to "Llama 4 Scout 17B",
            "qwen/qwen3-32b" to "Qwen 3 32B"
        )
    )

    LazyColumn(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Pilih Intelligence Engine", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        modelGroups.forEach { (group, models) ->
            item {
                Text(group, color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
            items(models) { (id, name) ->
                val isSel = id == currentModel
                PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { onModelSelected(id) }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isSel, onClick = { onModelSelected(id) }, colors = RadioButtonDefaults.colors(selectedColor = premiumaccent))
                        Spacer(Modifier.width(12.dp))
                        Text(name, color = if(isSel) textprimary else textsecondary, fontWeight = if(isSel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun ReportDetailSheet(report: com.rais.nexusbody.data.local.entity.AiReportEntity, onDismiss: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Column {
                Text(report.reportType.uppercase(), color = premiumaccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(sdf.format(Date(report.timestamp)), color = textsecondary, fontSize = 12.sp)
            }
        }
        item { HorizontalDivider(color = Color.White.copy(0.1f)) }
        item {
            Text(report.summary, color = textprimary, fontSize = 15.sp, lineHeight = 24.sp)
        }
        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))) {
                Text("Tutup", color = textprimary)
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}
