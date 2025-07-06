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
fun VitQuiz(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val questions = listOf(
        "How often do you consume a balanced diet with vegetables, fruits, grains, and proteins?" to listOf("Rarely", "Sometimes", "Often", "Daily"),
        "Do you frequently experience fatigue, even after adequate sleep?" to listOf("Always", "Often", "Rarely", "Never"),
        "How often do you get exposed to sunlight (for Vitamin D)?" to listOf("Almost never", "1-2 times/week", "3-5 times/week", "Daily"),
        "Do you include dairy or fortified alternatives in your diet (for Calcium, B12)?" to listOf("Never", "Occasionally", "Often", "Daily"),
        "Do you eat iron-rich foods (like leafy greens, lentils, or red meat)?" to listOf("Never", "Rarely", "Sometimes", "Regularly"),
        "How frequently do you include nuts, seeds, and whole grains in your meals?" to listOf("Never", "Rarely", "Sometimes", "Daily"),
        "Do you currently take any multivitamin or dietary supplements?" to listOf("No, never", "Sometimes", "Yes, regularly", "Only when prescribed"),
        "Have you had any symptoms like brittle nails, hair loss, or poor wound healing recently?" to listOf("Often", "Sometimes", "Rarely", "Never")
    )

    val answers = remember { mutableStateListOf<Int?>().apply { repeat(questions.size) { add(null) } } }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Vitamins & Minerals Quiz", fontSize = 20.sp, fontFamily = pixelFont, color = Color.White)
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
                            avg <= 1.5 -> "You might be lacking essential vitamins and minerals. It's a good idea to evaluate your nutrition or consult a healthcare provider."
                            avg <= 2.5 -> "You have a moderate intake of vitamins, but some key nutrients may still be insufficient."
                            else -> "Your vitamin and mineral intake appears balanced. Keep up the mindful eating habits!"
                        }

                        val resultData = hashMapOf(
                            "quizType" to "Vitamins & Minerals",
                            "averageScore" to avg,
                            "feedback" to feedback,
                            "answers" to answers.map { it ?: -1 },
                            "timestamp" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("quiz_responses")
                            .document(uid)
                            .collection("vitQuiz")
                            .add(resultData)
                            .addOnSuccessListener {
                                println("✅ Vitamin quiz result saved")
                            }
                            .addOnFailureListener {
                                println("❌ Failed to save vitamin quiz: ${it.message}")
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
                    avg <= 1.5 -> "You might be lacking essential vitamins and minerals. It's a good idea to evaluate your nutrition or consult a healthcare provider."
                    avg <= 2.5 -> "You have a moderate intake of vitamins, but some key nutrients may still be insufficient."
                    else -> "Your vitamin and mineral intake appears balanced. Keep up the mindful eating habits!"
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
                        Text(text = feedback, fontSize = 16.sp, fontFamily = pixelFont, color = Color(0xFF2E7D32))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tips:", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = pixelFont, color = Color(0xFF2E7D32))
                        Text(
                            text = "• Include a wide variety of colorful fruits and vegetables in your meals.\n" +
                                    "• Get 15-30 minutes of sun exposure daily for Vitamin D.\n" +
                                    "• Eat calcium-rich foods like dairy, almonds, and leafy greens.\n" +
                                    "• Add iron sources like spinach, legumes, or lean meats to your meals.\n" +
                                    "• Consider omega-3-rich foods like flaxseeds, walnuts, and fatty fish.\n" +
                                    "• Avoid over-reliance on supplements — food is the best source.\n" +
                                    "• Monitor symptoms like fatigue, brittle nails, or hair fall — they may signal deficiencies.",
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
