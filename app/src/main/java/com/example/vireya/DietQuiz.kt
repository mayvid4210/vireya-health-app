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
fun DietQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How many meals do you eat per day?" to listOf("1-2", "3", "4-5", "More than 5"),
        "How often do you consume processed/junk food?" to listOf("Daily", "Few times/week", "Occasionally", "Never"),
        "Do you include fruits and vegetables in your meals?" to listOf("Never", "Rarely", "Sometimes", "Daily"),
        "How often do you eat out or order food?" to listOf("Daily", "Few times/week", "Few times/month", "Rarely/Never"),
        "Do you consume breakfast regularly?" to listOf("Never", "Sometimes", "Often", "Daily"),
        "Do you follow portion control in your meals?" to listOf("Never", "Rarely", "Sometimes", "Always"),
        "How often do you consume sugary drinks (soda, sweet tea, etc)?" to listOf("Daily", "Few times/week", "Occasionally", "Never"),
        "How frequently do you cook your own meals?" to listOf("Never", "Rarely", "Sometimes", "Daily")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Diet & Nutrition Quiz", fontSize = 20.sp, fontFamily = pixelFont, color = Color.White)
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
                            avg <= 1.5 -> "Your diet habits may need improvement. Try reducing processed foods and eating more whole foods."
                            avg <= 2.5 -> "Your eating habits are moderate. Focus on improving consistency and reducing sugar intake."
                            else -> "Great job maintaining healthy eating habits. Keep fueling your body wisely!"
                        }

                        val resultData = hashMapOf(
                            "quizType" to "Diet & Nutrition",
                            "averageScore" to avg,
                            "feedback" to feedback,
                            "answers" to answers.map { it ?: -1 },
                            "timestamp" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("quiz_responses")
                            .document(uid)
                            .collection("dietQuiz")
                            .add(resultData)
                            .addOnSuccessListener {
                                println("✅ Diet quiz result saved")
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
                    avg <= 1.5 -> "Your diet habits may need improvement. Try reducing processed foods and eating more whole foods."
                    avg <= 2.5 -> "Your eating habits are moderate. Focus on improving consistency and reducing sugar intake."
                    else -> "Great job maintaining healthy eating habits. Keep fueling your body wisely!"
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
                                • Eat 3-5 well-balanced meals a day with whole foods.
                                • Minimize consumption of ultra-processed and fried items.
                                • Include a variety of colorful vegetables and fruits daily.
                                • Don’t skip breakfast — it jumpstarts your metabolism.
                                • Drink water instead of sugary beverages.
                                • Practice portion control and mindful eating.
                                • Prepare more home-cooked meals to control ingredients and freshness.
                                • Plan your meals ahead to avoid unhealthy choices.
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
