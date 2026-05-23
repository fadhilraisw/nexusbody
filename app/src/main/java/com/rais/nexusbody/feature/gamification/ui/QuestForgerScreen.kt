package com.rais.nexusbody.feature.gamification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun QuestForgerScreen() {
    var showForgeSheet by remember { mutableStateOf(false) }

    // data dikosongkan
    val quests = emptyList<Map<String, String>>()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Text("🎯 quest log", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("nexus gamification engine", color = textsecondary, fontSize = 12.sp, letterSpacing = 2.sp)
            }

            item {
                PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("current tier", color = textmuted, fontSize = 12.sp)
                            Text("elite vanguard", color = premiumaccent, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("total xp", color = textmuted, fontSize = 12.sp)
                            Text("0", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
            }

            item { Text("active custom quests", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }

            if (quests.isEmpty()) {
                item { Text("belum ada quest aktif.", color = textmuted, fontSize = 12.sp) }
            } else {
                items(quests) { quest ->
                    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(quest["title"]!!, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("trigger: ${quest["cond"]}", color = statuswarning, fontSize = 12.sp)
                            }
                            Text("+${quest["xp"]} xp", color = statusgood, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showForgeSheet = true },
            containerColor = premiumaccent,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "forge quest", tint = Color.White)
        }

        if (showForgeSheet) {
            ModalBottomSheet(onDismissRequest = { showForgeSheet = false }, containerColor = Color(0xFF0D0F12)) {
                ForgeQuestForm { showForgeSheet = false }
            }
        }
    }
}

@Composable
private fun ForgeQuestForm(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var module by remember { mutableStateOf("nutrition") }
    var variable by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf(">=") }
    var target by remember { mutableStateOf("") }
    var xp by remember { mutableStateOf("100") }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("forge custom quest", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = title, onValueChange = { title = it }, label = "nama quest") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("nutrition", "workout", "health").forEach { mod ->
                    val isSel = module == mod
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(isSel) premiumaccent else Color.White.copy(0.05f)).clickable { module = mod }.padding(12.dp, 6.dp)) {
                        Text(mod, color = if(isSel) Color.White else textsecondary, fontSize = 12.sp)
                    }
                }
            }
        }
        item { PremiumTextField(value = variable, onValueChange = { variable = it }, label = "variabel (ex: protein, chest volume, sleep)") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = operator, onValueChange = { operator = it }, label = "logika (>, <, =, >=)", modifier = Modifier.weight(1f))
                PremiumTextField(value = target, onValueChange = { target = it }, label = "target angka", modifier = Modifier.weight(1f))
            }
        }
        item { PremiumTextField(value = xp, onValueChange = { xp = it }, label = "xp reward") }
        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = statusgood)) {
                Text("aktifkan quest", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}