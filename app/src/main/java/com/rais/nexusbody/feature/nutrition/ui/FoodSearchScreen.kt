package com.rais.nexusbody.feature.nutrition.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.theme.textprimary
import com.rais.nexusbody.core.ui.theme.textsecondary

@Composable
fun FoodSearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔍 food database", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("modul pencarian makanan terpisah", color = textsecondary, fontSize = 14.sp)
    }
}