package com.rais.nexusbody.feature.onboarding.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.theme.premiumaccent
import com.rais.nexusbody.core.ui.theme.statusgood
import com.rais.nexusbody.core.ui.theme.textprimary

@Composable
fun OnboardingScreen(onNextClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("nexusbody", color = textprimary, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text("v2.0 medical grade", color = premiumaccent, fontSize = 14.sp, letterSpacing = 2.sp)

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = statusgood),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("mulai inisialisasi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}