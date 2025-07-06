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
fun GenQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How many hours of sleep do you get on average?" to listOf("Less than 4", "4-6", "6-8", "8+"),
        "How many glasses of water do you drink daily?" to listOf("1-2", "3-4", "5-6", "7+"),
        "How many steps do you walk per day?" to listOf("<2000", "2000-5000", "5000-8000", "8000+"),
        "How many servings of fruits/veggies do you eat daily?" to listOf("None", "1-2", "3-4", "5+"),
        "How often do you experience fatigue?" to listOf("Always", "Often", "Sometimes", "Rarely"),
        "How often do you feel mentally refreshed?" to listOf("Never", "Rarely", "Often", "Daily"),
        "How frequently do you exercise?" to listOf("Never", "1-2 times/week", "3-4 times/week", "Daily"),
        "How many hours of screen time (non-work) do you get daily?" to listOf("5+ hrs", "3-5 hrs", "1-2 hrs", "<1 hr")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wellness Quiz",
                        fontSize = 20.sp,
                        fontFamily = pixelFont,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
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
                    Text(text = question, fontSize = 16.sp, fontFamily = pixelFont, color = Color(0xFF2E7D32))
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
                            avg <= 1.5 -> "Your overall wellness needs attention. Prioritize sleep, hydration, and physical activity. Small steps make big changes."
                            avg <= 2.5 -> "You're doing okay, but there's room for improvement. Try incorporating more veggies and reducing screen time."
                            else -> "You're maintaining great wellness habits. Keep it up and continue listening to your body!"
                        }

                        val resultData = hashMapOf(
                            "quizType" to "General Wellness",
                            "averageScore" to avg,
                            "feedback" to feedback,
                            "answers" to answers.map { it ?: -1 },
                            "timestamp" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("quiz_responses")
                            .document(uid)
                            .collection("genQuiz")
                            .add(resultData)
                            .addOnSuccessListener {
                                println("✅ General wellness quiz result saved")
                            }
                            .addOnFailureListener {
                                println("❌ Failed to save: ${it.message}")
                            }
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
                    avg <= 1.5 -> "Your overall wellness needs attention. Prioritize sleep, hydration, and physical activity. Small steps make big changes."
                    avg <= 2.5 -> "You're doing okay, but there's room for improvement. Try incorporating more veggies and reducing screen time."
                    else -> "You're maintaining great wellness habits. Keep it up and continue listening to your body!"
                }

                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Result:", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = pixelFont, color = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(feedback, fontSize = 16.sp, fontFamily = pixelFont, color = Color(0xFF2E7D32))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tips:", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = pixelFont, color = Color(0xFF2E7D32))
                        Text(
                            text = """
                                • Aim for at least 7-8 hours of quality sleep every night.
                                • Drink 6-8 glasses of water daily to stay hydrated.
                                • Walk or move around for at least 30 minutes each day.
                                • Include at least 3 servings of fruits and vegetables in your diet.
                                • Take breaks from screens every hour to reduce eye strain and fatigue.
                                • Practice mindfulness, journaling, or relaxation techniques to refresh your mind.
                                • Try to maintain a consistent daily routine to support your biological clock.
                                • Get sunlight exposure in the morning to improve energy and sleep cycles.
                            """.trimIndent(),
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
