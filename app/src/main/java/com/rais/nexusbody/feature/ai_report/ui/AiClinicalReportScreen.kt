package com.rais.nexusbody.feature.ai_report.ui

import androidx.compose.foundation.background // UI: Latar belakang
import androidx.compose.foundation.clickable // UX: Navigasi via klik
import androidx.compose.foundation.layout.* // UI: Tata letak modular
import androidx.compose.foundation.lazy.LazyColumn // UI: List vertikal reaktif
import androidx.compose.foundation.lazy.items // UI: Iterasi data riwayat
import androidx.compose.foundation.shape.RoundedCornerShape // UI: Estetika sudut melengkung
import androidx.compose.material.icons.Icons // UI: Koleksi ikon
import androidx.compose.material.icons.filled.Delete // UI: Ikon hapus
import androidx.compose.material.icons.filled.Settings // UI: Ikon pengaturan
import androidx.compose.material.icons.filled.Star // UI: Ikon generate
import androidx.compose.material3.* // UI Framework: Material 3 (Standard Google)
import androidx.compose.runtime.* // UI State: Manajemen variabel reaktif (remember, mutableStateOf)
import androidx.compose.ui.Alignment // UI: Perataan konten
import androidx.compose.ui.Modifier // UI: Pengatur properti visual
import androidx.compose.ui.draw.clip // UI: Pemotong elemen sesuai bentuk
import androidx.compose.ui.graphics.Color // UI: Warna Hex
import androidx.compose.ui.text.font.FontWeight // UI: Ketebalan font
import androidx.compose.ui.unit.dp // UI: Satuan pixel (jarak)
import androidx.compose.ui.unit.sp // UI: Satuan font (ukuran)
import androidx.hilt.navigation.compose.hiltViewModel // DI: Injeksi otomatis ViewModel via Hilt
import com.rais.nexusbody.core.ui.components.PremiumGlassCard // UI Shared: Kartu kaca premium
import com.rais.nexusbody.core.ui.components.PremiumTextField // UI Shared: Input teks kustom
import com.rais.nexusbody.core.ui.theme.* // UI Theme: Warna identitas (DeepSpace, NeonViolet)
import com.rais.nexusbody.feature.ai_report.AiReportViewModel // Logic: Otak analisa AI
import java.text.SimpleDateFormat // Utils: Format tanggal penulisan
import java.util.* // Utils: Objek tanggal & bahasa

/**
 * AI CLINICAL REPORT SCREEN (FEATURE LAYER)
 * Peran: Layar utama untuk melakukan sintesis medis menggunakan kecerdasan buatan (Gemini/Groq).
 * UX: Menampilkan riwayat analisa dan menyediakan pilihan persona ahli (Doctor, Nutritionist, dll).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiClinicalReportScreen(viewModel: AiReportViewModel = hiltViewModel()) {
    // Observasi state data report dari database cloud secara real-time
    val state by viewModel.state.collectAsState()
    
    // State internal untuk kontrol tampilan Modal (Pop-up)
    var showGenerateSheet by remember { mutableStateOf(false) } // Trigger form baru
    var selectedReportId by remember { mutableStateOf<String?>(null) } // Trigger tampilan detail
    var showModelSelector by remember { mutableStateOf(false) } // Trigger ganti model AI

    Box(modifier = Modifier.fillMaxSize()) {
        // Daftar Riwayat Analisa AI (Scrollable)
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp), 
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // JUDUL HALAMAN & BUTTON SETTINGS
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("✦ ai clinical synthesis", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("analisa lintas-disiplin (medis, gizi, olahraga)", color = textsecondary, fontSize = 12.sp)
                    }
                    // Tombol ganti model AI (Gemini vs Groq)
                    IconButton(onClick = { showModelSelector = true }) {
                        Icon(Icons.Default.Settings, "Pilih Model AI", tint = premiumaccent)
                    }
                }
            }

            // INDIKATOR MODEL AKTIF (Feedback UX)
            item {
                PremiumGlassCard {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖 Active Intelligence:", color = textmuted, fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(state.selectedModel.uppercase(), color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // LOGIKA TAMPILAN KONTEN (Kosong vs Berisi)
            if (state.reports.isEmpty()) {
                item {
                    Text("belum ada report ai. klik tombol bintang di bawah untuk mulai analisa.", color = textmuted, fontSize = 12.sp)
                }
            } else {
                // Tampilkan daftar kartu report tersimpan
                items(state.reports) { report ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { selectedReportId = report.id }) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                // Tipe Persona (Holistic, Doctor, dll)
                                Text(report.reportType.uppercase(), color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(sdf.format(Date(report.timestamp)), color = textmuted, fontSize = 11.sp)
                                    Spacer(Modifier.width(8.dp))
                                    // Tombol hapus rekam analisa
                                    IconButton(onClick = { viewModel.deleteReport(report.id) }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            // Ringkasan singkat analisa (Max 3 baris di list)
                            Text(report.summary, color = textprimary, fontSize = 13.sp, lineHeight = 20.sp, maxLines = 3)
                        }
                    }
                }
            }
        }

        // --- FLOATING ACTION BUTTON (TRIGGER ANALISA BARU) ---
        FloatingActionButton(
            onClick = { showGenerateSheet = true },
            containerColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Default.Star, "generate report", tint = Color.Black)
        }

        // --- MODAL SHEETS ROUTER (NAVIAGASI POP-UP) ---

        // 1. Form Pembuatan Analisa (Input Persona & Keluhan)
        if (showGenerateSheet) {
            ModalBottomSheet(onDismissRequest = { showGenerateSheet = false }, containerColor = Color(0xFF0D0F12)) {
                GenerateReportForm(
                    onGenerate = { prompt, target ->
                        viewModel.generateSynthesis(prompt, target) // Kirim input ke Otak AI
                        showGenerateSheet = false
                    },
                    onDismiss = { showGenerateSheet = false }
                )
            }
        }

        // 2. Tampilan Detail Analisa Penuh (Read Mode)
        if (selectedReportId != null) {
            val report = state.reports.find { it.id == selectedReportId }
            if (report != null) {
                ModalBottomSheet(onDismissRequest = { selectedReportId = null }, containerColor = Color(0xFF0D0F12)) {
                    ReportDetailSheet(report) { selectedReportId = null }
                }
            }
        }

        // 3. Panel Pemilih Model AI (Engine Selection)
        if (showModelSelector) {
            ModalBottomSheet(onDismissRequest = { showModelSelector = false }, containerColor = Color(0xFF0D0F12)) {
                AiModelSelector(
                    currentModel = state.selectedModel,
                    onModelSelected = { 
                        viewModel.setAiModel(it) // Ganti "Otak" AI secara global
                        showModelSelector = false
                    }
                )
            }
        }
    }
}

/**
 * GENERATE REPORT FORM (UI COMPONENT)
 * Peran: Form input untuk memilih target audience (Dokter/Pelatih) dan mengetik keluhan fisik.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenerateReportForm(onGenerate: (String, String) -> Unit, onDismiss: () -> Unit) {
    var customPrompt by remember { mutableStateOf("") } // Teks keluhan user
    var selectedTarget by remember { mutableStateOf("holistic") } // Pilihan persona
    val targets = listOf("holistic", "sports doctor", "clinical nutritionist", "hypertrophy pt")

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("generate ai synthesis", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }

        // PEMILIH TARGET PERSONA (FlowRow agar responsif)
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

        // BANNER INFORMASI SYSTEM PROMPT
        item {
            PremiumGlassCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("clinical intelligence core", color = statusgood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("ai akan mengekstrak data biomarker, nutrisi harian, dan beban latihan untuk mencari korelasi anomali klinis.", color = textsecondary, fontSize = 11.sp)
                }
            }
        }

        // INPUT TEKS KELUHAN
        item {
            PremiumTextField(value = customPrompt, onValueChange = { customPrompt = it }, label = "keluhan spesifik, target performa, dll", isMultiline = true)
        }

        // TOMBOL EKSEKUSI ANALISA
        item {
            Button(
                onClick = { onGenerate(customPrompt, selectedTarget) }, 
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("mulai analisa binaraga medis", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

/**
 * AI MODEL SELECTOR (UI COMPONENT)
 * Peran: Menampilkan daftar engine AI yang tersedia (Gemini 3.1 & Groq Llama 4).
 */
@Composable
private fun AiModelSelector(currentModel: String, onModelSelected: (String) -> Unit) {
    // Definisi grup model AI terbaru (Update 2026)
    val modelGroups = mapOf(
        "Google: Generation 3 (Preview)" to listOf(
            "gemini-3.1-pro-preview" to "Gemini 3.1 Pro Preview",
            "gemini-3.1-flash-lite" to "Gemini 3.1 Flash Lite",
            "deep-research-max-preview-04-2026" to "Deep Research Max"
        ),
        "Google: Generation 2.5 & 2.0" to listOf(
            "gemini-2.5-pro" to "Gemini 2.5 Pro",
            "gemini-2.0-flash" to "Gemini 2.0 Flash"
        ),
        "Groq: Llama & Qwen" to listOf(
            "llama-3.3-70b-versatile" to "Llama 3.3 70B",
            "meta-llama/llama-4-scout-17b-16e-instruct" to "Llama 4 Scout 17B",
            "qwen/qwen3-32b" to "Qwen 3 32B"
        )
    )

    LazyColumn(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("pilih intelligence engine", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        modelGroups.forEach { (group, models) ->
            // Label Kelompok Model
            item {
                Text(group, color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
            // List Model dalam kelompok
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

/**
 * REPORT DETAIL SHEET (UI COMPONENT)
 * Peran: Menampilkan hasil analisa AI penuh dalam format dokumen yang mudah dibaca.
 */
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
        // AREA TEKS ANALISA PENUH
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
