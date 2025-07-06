package com.example.vireya

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val green = Color(0xFF2E7D32)
    val background = Color(0xFFF5FFF5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = green
                    )
                }
            )
        },
        containerColor = background
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.login_icon),
                    contentDescription = "Login Icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("e.g. alice@vireya.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter password") },
                    visualTransformation = if (password.isEmpty()) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { /* TODO: Add forgot password logic */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", fontSize = 14.sp, color = green)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ⚠️ Show error if login failed
                errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.length >= 6) {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    Log.d("LOGIN", "Sign-in successful")
                                    errorMessage = null
                                    navController.navigate("upload") // ✅ Navigate on success
                                }
                                .addOnFailureListener { e ->
                                    Log.e("LOGIN", "Login failed: ${e.message}")
                                    errorMessage = "Login failed: ${e.message}"
                                }
                        } else {
                            errorMessage = "Please enter valid email and password"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Login", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("New here?", fontSize = 16.sp, color = Color.DarkGray)

                TextButton(
                    onClick = { navController.navigate("signup") }
                ) {
                    Text("Create an account", fontSize = 16.sp, color = green)
                }
            }
        }
    }
}
