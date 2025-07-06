package com.example.vireya

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vireya.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


data class PrescriptionData(
    val name: String,
    val time: String,
    val tips: String,
    val days: String,
    val duration: String
)

val pixelFontHome = FontFamily(Font(R.font.vt323))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuScreen(
    navController: NavController,
    onUploadClick: () -> Unit
) {
    val greenColor = Color(0xFF2E7D32)

    val prescriptions = remember { mutableStateListOf<PrescriptionData>() }
    var isLoading by remember { mutableStateOf(false) }
    var startProcessing by remember { mutableStateOf(false) }

    // This runs **only** when startProcessing changes to true
    LaunchedEffect(startProcessing) {
        if (startProcessing) {
            isLoading = true
            prescriptions.clear()
            delay(2500) // simulate OCR or network delay
            prescriptions.addAll(
                listOf(
                    PrescriptionData(
                        "Paracetamol",
                        "8:00 AM",
                        "Take after meals",
                        "Mon, Wed, Fri",
                        "1 week"
                    ),
                    PrescriptionData(
                        "Vitamin D",
                        "9:00 AM",
                        "Sunlight advised",
                        "Daily",
                        "1 month"
                    ),
                    PrescriptionData("Ibuprofen", "10:00 AM", "Drink water", "Daily", "5 days"),
                    PrescriptionData("Aspirin", "12:00 PM", "Take with food", "Daily", "2 weeks"),
                    PrescriptionData(
                        "Amoxicillin",
                        "2:00 PM",
                        "Complete full course",
                        "Daily",
                        "10 days"
                    ),
                    PrescriptionData(
                        "Cetirizine",
                        "4:00 PM",
                        "Avoid driving",
                        "Tue, Thu",
                        "2 weeks"
                    ),
                    PrescriptionData(
                        "Metformin",
                        "6:00 PM",
                        "Monitor blood sugar",
                        "Daily",
                        "3 weeks"
                    ),
                    PrescriptionData("Omeprazole", "8:00 PM", "Before bedtime", "Daily", "1 week"),
                    PrescriptionData("Calcium", "10:00 PM", "Take with milk", "Sat, Sun", "1 month")
                )
            )

            isLoading = false
            startProcessing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Hi there!",
                            fontFamily = pixelFontHome,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Take charge of your health today!",
                            fontFamily = pixelFontHome,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    try {
                        startProcessing = true
                        onUploadClick()
                    } catch (e: Exception) {
                        // Just in case, log or handle error so app doesn't crash
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("📷 Upload Prescription", color = Color.White, fontFamily = pixelFontHome)
            }

            Text(
                text = "Your Medications",
                fontFamily = pixelFontHome,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 8.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(color = greenColor)
                    Text(
                        "Processing...",
                        fontFamily = pixelFontHome,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                prescriptions.isEmpty() -> {
                    Text(
                        "No prescriptions uploaded yet.",
                        fontFamily = pixelFontHome,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(prescriptions) { item ->
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        item.name,
                                        fontFamily = pixelFontHome,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(item.time, fontFamily = pixelFontHome)
                                }

                                Text(
                                    item.tips,
                                    fontFamily = pixelFontHome,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Days: ${item.days}",
                                        fontFamily = pixelFontHome,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "Duration: ${item.duration}",
                                        fontFamily = pixelFontHome,
                                        color = Color.Gray
                                    )
                                }

                                Divider(modifier = Modifier.padding(top = 8.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    navController.navigate("calendar")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text("Sync with Calendar", fontFamily = pixelFontHome, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

