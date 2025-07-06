package com.example.vireya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vireya.R
import androidx.compose.material3.TextFieldDefaults

data class Message(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(navController: NavController) {
    val pixelFont = FontFamily(Font(R.font.vt323))
    val messages = remember { mutableStateListOf<Message>() }
    var userInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wellness Chatbot 🤖",
                        fontFamily = pixelFont,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Type your message...", fontFamily = pixelFont) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = Color(0xFF2E7D32)
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            messages.add(Message(userInput.trim(), true))

                            val reply = when {
                                userInput.contains("headache", ignoreCase = true) ->
                                    "Try taking Paracetamol after meals and rest in a dark, quiet room."

                                userInput.contains("fever", ignoreCase = true) ->
                                    "Stay hydrated, rest well, and monitor your temperature. If it exceeds 102°F, consult a doctor."

                                userInput.contains("cold", ignoreCase = true) ->
                                    "Drink warm fluids, rest, and consider steam inhalation. If symptoms persist, seek medical help."

                                userInput.contains("cough", ignoreCase = true) ->
                                    "Try warm water with honey, avoid cold drinks, and use a cough syrup if prescribed."

                                userInput.contains("sore throat", ignoreCase = true) ->
                                    "Gargle with warm salt water and avoid spicy or cold foods."

                                userInput.contains("vomiting", ignoreCase = true) ->
                                    "Drink ORS or electrolyte water. Avoid solid food for a while and rest."

                                userInput.contains("diarrhea", ignoreCase = true) ->
                                    "Hydrate with ORS, avoid dairy, and eat light, bland food like khichdi or toast."

                                userInput.contains("stomach pain", ignoreCase = true) ->
                                    "Use a hot water bag and eat easily digestible foods. If pain is severe or sharp, consult a doctor."

                                userInput.contains("period", ignoreCase = true) ->
                                    "Cramps can be relieved with a heating pad and rest. Gentle stretching may help too."

                                userInput.contains("tired", ignoreCase = true) ||
                                        userInput.contains("fatigue", ignoreCase = true) ->
                                    "Ensure you're getting enough sleep, drinking water, and eating balanced meals."

                                userInput.contains("vitamin", ignoreCase = true) ->
                                    "Include fruits, vegetables, nuts, and dairy in your diet for natural vitamins."

                                userInput.contains("mental health", ignoreCase = true) ||
                                        userInput.contains("anxiety", ignoreCase = true) ->
                                    "Take deep breaths, journal your thoughts, and reach out to someone you trust."

                                userInput.contains("thank", ignoreCase = true) ||
                                        userInput.contains("thanks", ignoreCase = true) ->
                                    "You're welcome! Always here for your health queries 😊"

                                userInput.contains("hi", ignoreCase = true) ||
                                        userInput.contains("hello", ignoreCase = true) ->
                                    "Hi there! How can I support your wellness today?"

                                else -> "I'm still learning! Please try rephrasing or ask me a health-related question."
                            }

                            messages.add(Message(reply, false))
                            userInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Send", fontFamily = pixelFont, color = Color.White)
                }
            }
        },
        containerColor = Color(0xFFF9FFF7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (messages.isEmpty()) {
                Text(
                    text = "Say hi to your wellness buddy!",
                    fontFamily = pixelFont,
                    color = Color.DarkGray,
                    fontSize = 18.sp
                )
            } else {
                messages.forEach { msg ->
                    val bubbleColor = if (msg.isUser) Color(0xFFC8E6C9) else Color(0xFFEEEEEE)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            modifier = Modifier
                                .padding(4.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(10.dp),
                                fontFamily = pixelFont,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
