package com.rais.nexusbody.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.theme.premiumaccent
import com.rais.nexusbody.core.ui.theme.textmuted
import com.rais.nexusbody.core.ui.theme.textprimary

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isMultiline: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = textmuted, fontSize = 14.sp) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        minLines = if (isMultiline) 3 else 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedIndicatorColor = premiumaccent,
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.1f),
            focusedTextColor = textprimary,
            unfocusedTextColor = textprimary,
            cursorColor = premiumaccent
        )
    )
}