package com.example.vireya

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.vireya.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer

val pixelFont = FontFamily(Font(R.font.vt323))

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(30f) }

    val backgroundColor by rememberInfiniteTransition().animateValue(
        initialValue = Color(0xFF1D3B2A),
        targetValue = Color(0xFF254D38),
        typeConverter = TwoWayConverter(
            convertToVector = { color -> AnimationVector4D(color.red, color.green, color.blue, color.alpha) },
            convertFromVector = { vector -> Color(vector.v1, vector.v2, vector.v3, vector.v4) }
        ),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )

        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 300)
            )
        }

        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 300)
            )
        }

        delay(3500)
        navController.navigate("welcome") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Vireya Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale.value) // initial scale, no pulsing anymore
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Vireya",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pixelFont,
                color = Color.White,
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = alpha.value
                        translationY = offsetY.value
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Remind. Recommend. Reassure.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = pixelFont,
                color = Color(0xFFB9D8C2),
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = alpha.value
                        translationY = offsetY.value
                    }
            )
        }
    }
}
