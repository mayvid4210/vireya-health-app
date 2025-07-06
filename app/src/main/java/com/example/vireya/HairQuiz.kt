package com.example.vireya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vireya.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HairQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How often do you wash your hair?" to listOf("Daily", "Every 2-3 days", "Weekly", "Rarely"),
        "Do you use a conditioner after shampooing?" to listOf("Always", "Sometimes", "Rarely", "Never"),
        "How often do you experience dandruff or dry scalp?" to listOf("Always", "Often", "Rarely", "Never"),
        "How frequently do you oil your hair?" to listOf("Weekly", "Bi-weekly", "Monthly", "Never"),
        "Do you use heat styling tools?" to listOf("Daily", "Sometimes", "Rarely", "Never"),
        "How often do you trim your hair?" to listOf("Every month", "Every 2-3 months", "Twice a year", "Rarely/Never"),
        "What is your hair type?" to listOf("Oily", "Dry", "Normal", "Combination"),
        "How often do you consume protein-rich food (eggs, legumes, nuts)?" to listOf("Daily", "Few times/week", "Rarely", "Never")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hair Health Quiz",
                        fontSize = 20.sp,
                        fontFamily = pixelFont,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                )
            )
        },
        containerColor = Color(0xFFFFFDE7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            questions.forEachIndexed { index, (question, options) ->
                Column {
                    Text(
                        text = question,
                        fontSize = 16.sp,
                        fontFamily = pixelFont,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    options.forEachIndexed { optIndex, option ->
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = answers[index] == optIndex,
                                onClick = { answers[index] = optIndex }
                            )
                            Text(option, fontFamily = pixelFont)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    showResult = true

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        val avg = answers.filterNotNull().average()
                        val feedback = when {
                            avg <= 1.5 -> "Your hair care routine may need improvement. Consider nourishing practices and regular maintenance."
                            avg <= 2.5 -> "Your routine is okay, but consistency and better nutrition could help."
                            else -> "Your hair care habits seem strong. Keep up the healthy practices!"
                        }

                        val resultData = hashMapOf(
                            "quizType" to "Hair Health",
                            "averageScore" to avg,
                            "feedback" to feedback,
                            "answers" to answers.map { it ?: -1 },
                            "timestamp" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("quiz_responses")
                            .document(uid)
                            .collection("hairQuiz")
                            .add(resultData)
                            .addOnSuccessListener {
                                println("✅ Hair quiz result saved to Firestore")
                            }
                            .addOnFailureListener {
                                println("❌ Failed to save hair quiz: ${it.message}")
                            }
                    } else {
                        println("❌ User not logged in")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                val buttonText = if (showResult) "Scroll for Result" else "Submit"
                Text(text = buttonText, fontFamily = pixelFont, color = Color.White)
            }

            if (showResult) {
                val avg = answers.filterNotNull().average()
                val feedback = when {
                    avg <= 1.5 -> "Your hair care routine may need improvement. Consider nourishing practices and regular maintenance."
                    avg <= 2.5 -> "Your routine is okay, but consistency and better nutrition could help."
                    else -> "Your hair care habits seem strong. Keep up the healthy practices!"
                }

                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Result:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pixelFont,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedback,
                            fontSize = 16.sp,
                            fontFamily = pixelFont,
                            color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tips:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pixelFont,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "• Wash your hair 2-3 times a week to maintain natural oils.\n" +
                                    "• Use conditioner regularly to prevent dryness.\n" +
                                    "• Oil your scalp weekly to improve strength and shine.\n" +
                                    "• Trim your hair every 2-3 months to avoid split ends.\n" +
                                    "• Avoid excessive heat styling to prevent breakage.\n" +
                                    "• Include protein-rich foods in your diet to support hair growth.\n" +
                                    "• Stay hydrated and manage stress for healthier hair.",
                            fontSize = 14.sp,
                            fontFamily = pixelFont,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}
