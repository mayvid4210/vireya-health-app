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
fun MentalQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How often do you feel overwhelmed or anxious?" to listOf("Always", "Often", "Sometimes", "Rarely"),
        "How many hours of quality sleep do you get?" to listOf("Less than 4", "4-6", "6-8", "8+"),
        "Do you talk to friends or family when you're feeling low?" to listOf("Never", "Rarely", "Sometimes", "Often"),
        "How frequently do you practice mindfulness (meditation, breathing, etc.)?" to listOf("Never", "Rarely", "Sometimes", "Daily"),
        "How often do you engage in hobbies or activities you enjoy?" to listOf("Never", "Rarely", "Sometimes", "Often"),
        "Do you feel motivated to complete daily tasks?" to listOf("Never", "Rarely", "Sometimes", "Usually"),
        "How often do you take breaks or rest during your work/study hours?" to listOf("Never", "Rarely", "Sometimes", "Often"),
        "Have you consulted a mental health professional in the last year?" to listOf("No", "Thought about it", "Yes, once", "Yes, regularly")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mental Health Quiz",
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
                        val quizData = hashMapOf(
                            "answers" to answers.map { it ?: -1 },
                            "score" to answers.filterNotNull().average(),
                            "timestamp" to System.currentTimeMillis()
                        )
                        FirebaseFirestore.getInstance().collection("quiz_responses")
                            .document(uid)
                            .collection("mentalHealth")
                            .add(quizData)
                            .addOnSuccessListener {
                                println("✅ Mental health quiz response saved")
                            }
                            .addOnFailureListener {
                                println("❌ Failed to save mental quiz response: ${it.message}")
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
                    avg <= 1.5 -> "You may be struggling with your mental health. It might help to seek support and prioritize self-care."
                    avg <= 2.5 -> "Your mental health is moderate. Building positive routines can make a big difference."
                    else -> "You're managing well. Keep up the healthy mental habits."
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
                            text = "• Prioritize regular sleep, meals, and hydration.\n" +
                                    "• Talk to someone you trust about how you're feeling.\n" +
                                    "• Practice mindfulness or deep breathing exercises daily.\n" +
                                    "• Make time for hobbies and breaks from work.\n" +
                                    "• Avoid isolating yourself — even short conversations help.\n" +
                                    "• Seek help from a mental health professional if needed.\n" +
                                    "• Limit digital overstimulation and news exposure.\n" +
                                    "• Keep a gratitude or mood journal.",
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
