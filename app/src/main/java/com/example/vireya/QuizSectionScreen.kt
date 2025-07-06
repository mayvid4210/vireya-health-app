package com.example.vireya

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vireya.R

data class QuizCategory(val title: String, val iconRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSectionScreen(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))

    val sections = listOf(
        QuizCategory("General Wellness", R.drawable.ic_wellness),
        QuizCategory("Vitamins & Minerals", R.drawable.ic_vitamins),
        QuizCategory("Hair Health", R.drawable.ic_hair),
        QuizCategory("Diet & Nutrition", R.drawable.ic_diet),
        QuizCategory("Immunity", R.drawable.ic_immunity),
        QuizCategory("Mental Health", R.drawable.ic_mental_health)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quiz",
                        fontFamily = pixelFont,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20) // Dark green
                )
            )
        },
        containerColor = Color(0xFFE8F5E9) // Very light green
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Let's discover more about your health!",
                fontSize = 18.sp,
                fontFamily = pixelFont,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E7D32) // Strong dark green
            )

            sections.forEach { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)) // Very light cream
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = category.iconRes),
                            contentDescription = category.title,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    when (category.title) {
                                        "General Wellness" -> navController.navigate("GenQuiz")
                                        "Vitamins & Minerals" -> navController.navigate("VitQuiz")
                                        "Hair Health" -> navController.navigate("HairQuiz")
                                        "Diet & Nutrition" -> navController.navigate("DietQuiz")
                                        "Immunity" -> navController.navigate("ImmQuiz")
                                        "Mental Health" -> navController.navigate("MentalQuiz")
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = category.title,
                            fontSize = 18.sp,
                            fontFamily = pixelFont,
                            color = Color(0xFF2E7D32) // Matching text color with theme
                        )
                    }
                }
            }
        }
    }
}
