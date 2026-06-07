package com.rais.nexusbody.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*
import com.rais.nexusbody.feature.auth.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Memantau jika status isSuccess berubah menjadi true, otomatis navigasi ke Dashboard
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090A))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("nexusbody", color = textprimary, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("v2.0 medical grade", color = premiumaccent, fontSize = 12.sp, letterSpacing = 4.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(statusdanger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = uiState.error!!, color = statusdanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isLoginMode) "Sign In" else "Create Account",
                        color = textprimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    PremiumTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.clearError()
                        },
                        label = "Email Address"
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.clearError()
                        },
                        label = { Text("Password", color = textmuted) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = premiumaccent,
                            unfocusedBorderColor = Color.White.copy(0.1f),
                            focusedTextColor = textprimary,
                            unfocusedTextColor = textprimary,
                            cursorColor = premiumaccent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                viewModel.clearError()
                            },
                            label = { Text("Confirm Password", color = textmuted) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = premiumaccent,
                                unfocusedBorderColor = Color.White.copy(0.1f),
                                focusedTextColor = textprimary,
                                unfocusedTextColor = textprimary,
                                cursorColor = premiumaccent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(email, password)
                            } else {
                                viewModel.register(email, password, confirmPassword)
                            }
                        },
                        enabled = !uiState.isLoading && uiState.rateLimitCooldown <= 0,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = statusgood,
                            disabledContainerColor = statusgood.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (uiState.rateLimitCooldown > 0) "Tunggu ${uiState.rateLimitCooldown}s" 
                                       else if (isLoginMode) "Log In" else "Register",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Toggle Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ",
                    color = textsecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = if (isLoginMode) "Daftar di sini" else "Masuk sekarang",
                    color = premiumaccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            isLoginMode = !isLoginMode
                            viewModel.clearError()
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}