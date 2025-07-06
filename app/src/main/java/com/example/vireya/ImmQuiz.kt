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
fun ImmQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How often do you fall sick (cold, cough, flu, etc.)?" to listOf("Very frequently", "Every few months", "Once a year", "Rarely/Never"),
        "Do you eat immune-boosting foods (like garlic, turmeric, citrus, etc.)?" to listOf("Never", "Rarely", "Sometimes", "Daily"),
        "How much quality sleep do you get on average?" to listOf("Less than 5 hours", "5–6 hours", "6–7 hours", "7+ hours"),
        "How often do you exercise or engage in physical activity?" to listOf("Never", "Occasionally", "Few times/week", "Daily"),
        "How often are you stressed or anxious?" to listOf("Always", "Often", "Sometimes", "Rarely"),
        "Do you smoke or consume alcohol regularly?" to listOf("Yes, daily", "Yes, occasionally", "Rarely", "Never"),
        "How often do you consume probiotic or fermented foods (curd, yogurt, etc.)?" to listOf("Never", "Rarely", "Sometimes", "Daily"),
        "Do you stay hydrated and drink at least 6–8 glasses of water per day?" to listOf("Rarely", "Sometimes", "Often", "Always")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Immunity Quiz", fontSize = 20.sp, fontFamily = pixelFont, color = Color.White)
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
                            Text(text = option, fontFamily = pixelFont)
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
                            avg <= 1.5 -> "Your immunity seems to be weak. Consider lifestyle and dietary changes to boost your immune defenses."
                            avg <= 2.5 -> "Your immune health is average. With a few improvements, it can become more resilient."
                            else -> "You maintain a strong immune system. Continue your healthy habits."
                        }

                        val resultData = hashMapOf(
                            "quizType" to "Immunity",
                            "averageScore" to avg,
                            "feedback" to feedback,
                            "answers" to answers.map { it ?: -1 },
                            "timestamp" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("quiz_responses")
                            .document(uid)
                            .collection("immQuiz")
                            .add(resultData)
                            .addOnSuccessListener {
                                println("✅ Immunity quiz result saved")
                            }
                            .addOnFailureListener {
                                println("❌ Failed to save immunity quiz: ${it.message}")
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
                    avg <= 1.5 -> "Your immunity seems to be weak. Consider lifestyle and dietary changes to boost your immune defenses."
                    avg <= 2.5 -> "Your immune health is average. With a few improvements, it can become more resilient."
                    else -> "You maintain a strong immune system. Continue your healthy habits."
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Result:", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = pixelFont, color = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = feedback, fontSize = 16.sp, fontFamily = pixelFont, color = Color(0xFF2E7D32))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tips:", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = pixelFont, color = Color(0xFF2E7D32))
                        Text(
                            text = """
                                • Prioritize at least 7–8 hours of sleep per night.
                                • Eat immune-supportive foods like citrus, ginger, turmeric, and leafy greens.
                                • Reduce stress through meditation, journaling, or leisure activities.
                                • Avoid smoking and limit alcohol intake.
                                • Exercise regularly to improve circulation and immunity.
                                • Stay well-hydrated every day.
                                • Include probiotics and fermented foods to support gut health.
                                • Practice good hygiene and regular handwashing to reduce infections.
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
