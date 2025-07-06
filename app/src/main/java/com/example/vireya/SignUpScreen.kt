package com.example.vireya

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val green = Color(0xFF2E7D32)
    val background = Color(0xFFF5FFF5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Account",
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
                    contentDescription = "Sign Up Icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g. Alice Fernandes") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("Pick a unique name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("e.g. you@vireya.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Create Password") },
                    placeholder = { Text("Minimum 6 characters") },
                    visualTransformation = if (password.isEmpty()) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Age",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Decrease age",
                        modifier = Modifier.size(32.dp).clickable {
                            val currentAge = age.toIntOrNull() ?: 18
                            if (currentAge > 1) age = (currentAge - 1).toString()
                        },
                        tint = green
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (age.isEmpty()) "18" else age,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Increase age",
                        modifier = Modifier.size(32.dp).clickable {
                            val currentAge = age.toIntOrNull() ?: 18
                            age = (currentAge + 1).toString()
                        },
                        tint = green
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        Log.d("SIGNUP", "Sign-up button clicked")

                        if (email.isNotEmpty() && password.length >= 6 && name.isNotEmpty() && username.isNotEmpty()) {
                            val parsedAge = age.toIntOrNull() ?: 18
                            registerUserAndSaveDetails(
                                fullName = name,
                                userName = username,
                                email = email,
                                password = password,
                                age = parsedAge,
                                onSuccess = {
                                    Log.d("SIGNUP", "User registration + save successful")
                                    navController.navigate("upload") // ✅ Update to actual next screen
                                },
                                onFailure = { error ->
                                    Log.e("SIGNUP", "❌ Error: ${error.message}")
                                    // TODO: Show error to user via Snackbar/Toast
                                }
                            )
                        } else {
                            Log.e("SIGNUP", "⚠️ Invalid input. Please fill all fields correctly.")
                            // TODO: Show validation message
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Sign Up", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Already have an account?", fontSize = 16.sp, color = Color.DarkGray)

                TextButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Text("Login instead", fontSize = 16.sp, color = green)
                }
            }
        }
    }
}
