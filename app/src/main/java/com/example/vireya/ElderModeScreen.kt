package com.example.vireya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElderModeScreen(navController: NavController) {
    var fontSize by remember { mutableStateOf(18f) }
    var highContrast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Elder Mode Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Font Size: ${fontSize.toInt()}sp")
            Slider(
                value = fontSize,
                onValueChange = { fontSize = it },
                valueRange = 14f..30f
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("High Contrast Mode")
                Spacer(modifier = Modifier.width(16.dp))
                Switch(checked = highContrast, onCheckedChange = { highContrast = it })
            }

            Divider()

            Text("Preview:", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (highContrast) Color.Black else Color.White),
                colors = CardDefaults.cardColors(
                    containerColor = if (highContrast) Color.Black else Color(0xFFF0F0F0)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💊 Take your Zincovit after lunch",
                        fontSize = fontSize.sp,
                        color = if (highContrast) Color.White else Color.Black
                    )
                    Text(
                        text = "💧 Don’t forget 2PM hydration!",
                        fontSize = fontSize.sp,
                        color = if (highContrast) Color.LightGray else Color.DarkGray
                    )
                }
            }
        }
    }
}
