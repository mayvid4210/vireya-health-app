package com.example.vireya

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth


@Composable
fun WelcomeScreen(navController: NavController) {
    var startAnim by remember { mutableStateOf(false) }

    val fadeIn = { delay: Int ->
        fadeInTween(delay)
    }

    val textOffset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 40f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 700)
    )

    val logoOffset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 50f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic)
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    val button1Offset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 80f,
        animationSpec = tween(500, delayMillis = 200)
    )
    val button2Offset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 80f,
        animationSpec = tween(500, delayMillis = 300)
    )
    val guestOffset by animateFloatAsState(
        targetValue = if (startAnim) 0f else 80f,
        animationSpec = tween(500, delayMillis = 400)
    )

    val alphaButtons by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 700)
    )

    LaunchedEffect(Unit) {
        delay(150)
        startAnim = true
    }

    val backgroundColor = Color(0xFFE8F5E9)
    val buttonGreen = Color(0xFF2E7D32)

    Scaffold(containerColor = backgroundColor) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Vireya",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = buttonGreen,
                    modifier = Modifier
                        .offset(y = textOffset.dp)
                        .alpha(textAlpha)
                )

                Text(
                    text = "Your everyday health companion",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = textOffset.dp)
                        .alpha(textAlpha)
                )

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Vireya Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .offset(y = logoOffset.dp)
                        .alpha(logoAlpha)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = button1Offset.dp)
                        .alpha(alphaButtons)
                ) {
                    Text("Login", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.navigate("signup") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = button2Offset.dp)
                        .alpha(alphaButtons)
                ) {
                    Text("Sign Up", fontSize = 18.sp, color = buttonGreen, fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()  // Ensure no user is logged in
                        navController.navigate("upload")      // or "profile" if that's your guest destination
                    },
                    modifier = Modifier
                        .offset(y = guestOffset.dp)
                        .alpha(alphaButtons)
                ) {
                    Text(
                        "Continue as Guest",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun fadeInTween(delay: Int) = tween<Float>(durationMillis = 500, delayMillis = delay, easing = LinearOutSlowInEasing)
