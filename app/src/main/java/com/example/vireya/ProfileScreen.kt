package com.example.vireya

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("guest_health_log", Context.MODE_PRIVATE) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val isGuest = uid == null

    var dataLoaded by remember { mutableStateOf(false) }

    var waterCount by rememberSaveable { mutableStateOf(0) }
    var moodLevel by rememberSaveable { mutableStateOf(-1) }
    var steps by rememberSaveable { mutableStateOf(0) }
    var screenTime by rememberSaveable { mutableStateOf(0) }
    var revealTip by rememberSaveable { mutableStateOf(false) }
    var badgeEarned by rememberSaveable { mutableStateOf(false) }

    val stepGoal = 8000
    val screenLimit = 2

    val infiniteTransition = rememberInfiniteTransition(label = "badgeTransition")
    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "badgeScaleAnim"
    )

    val userData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val quizFeedbacks = remember { mutableStateMapOf<String, String>() }
    val prescriptions = remember { mutableStateListOf<Map<String, Any>>() }

    LaunchedEffect(true) {
        if (!dataLoaded) {
            if (isGuest) {
                val storedDate = sharedPrefs.getString("date", null)
                if (storedDate == today) {
                    waterCount = sharedPrefs.getInt("water", 0)
                    moodLevel = sharedPrefs.getInt("mood", -1)
                    steps = sharedPrefs.getInt("steps", 0)
                    screenTime = sharedPrefs.getInt("screenTime", 0)
                    badgeEarned = sharedPrefs.getBoolean("badgeEarned", false)
                } else {
                    sharedPrefs.edit().clear().apply()
                }
                dataLoaded = true
            } else {
                val db = FirebaseFirestore.getInstance()

                db.collection("users").document(uid!!).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) userData.value = document.data
                    }

                db.collection("quiz_responses").document(uid).collection("genQuiz")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { result ->
                        result.documents.firstOrNull()?.getString("feedback")?.let { feedback ->
                            quizFeedbacks["General Wellness"] = feedback
                        }
                    }

                db.collection("prescriptions").document(uid).collection("data")
                    .get()
                    .addOnSuccessListener { result ->
                        prescriptions.clear()
                        prescriptions.addAll(result.documents.mapNotNull { it.data })
                    }

                db.collection("health_logs").document(uid).get()
                    .addOnSuccessListener { doc ->
                        val storedDate = doc.getString("date")
                        if (storedDate == today) {
                            waterCount = doc.getLong("water")?.toInt() ?: 0
                            moodLevel = doc.getLong("mood")?.toInt() ?: -1
                            steps = doc.getLong("steps")?.toInt() ?: 0
                            screenTime = doc.getLong("screenTime")?.toInt() ?: 0
                            badgeEarned = doc.getBoolean("healthMaster") ?: false
                        }
                        dataLoaded = true
                    }
            }
        }
    }

    LaunchedEffect(waterCount, moodLevel, steps, screenTime) {
        if (!dataLoaded) return@LaunchedEffect

        val earnedNow = waterCount == 8 && moodLevel != -1 && steps >= stepGoal && screenTime <= screenLimit
        badgeEarned = earnedNow

        if (isGuest) {
            with(sharedPrefs.edit()) {
                putInt("water", waterCount)
                putInt("mood", moodLevel)
                putInt("steps", steps)
                putInt("screenTime", screenTime)
                putString("date", today)
                putBoolean("badgeEarned", earnedNow)
                apply()
            }
        } else if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("health_logs").document(uid).set(
                mapOf(
                    "date" to today,
                    "water" to waterCount,
                    "mood" to moodLevel,
                    "steps" to steps,
                    "screenTime" to screenTime,
                    "healthMaster" to earnedNow
                )
            )
            if (earnedNow) {
                db.collection("badges").document(uid).set(mapOf("healthMaster" to true))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(userData.value)

        Section(title = "Past Prescriptions") {
            if (prescriptions.isEmpty()) Text("No prescriptions found.")
            else prescriptions.forEach { Text("• ${it["name"]} - ${it["time"]} (${it["duration"]})") }
        }

        Section(title = "Quiz Summary") {
            if (quizFeedbacks.isEmpty()) Text("No quiz feedback available yet.")
            else quizFeedbacks.forEach { (type, feedback) ->
                Text("$type: $feedback", color = Color.DarkGray)
            }
        }

        Section(title = "Today's Health Log", colorful = true) {
            Text("Water Tracker")
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                repeat(8) { index ->
                    val filled = index < waterCount
                    val fillColor by animateColorAsState(if (filled) Color(0xFF42A5F5) else Color.LightGray, label = "waterColor")
                    Box(modifier = Modifier.size(30.dp).background(fillColor, CircleShape).clickable {
                        waterCount = index + 1
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Mood Tracker")
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                val moodColors = listOf(
                    Color(0xFFD32F2F), Color(0xFFF57C00), Color(0xFFFFEB3B),
                    Color(0xFF81C784), Color(0xFF388E3C)
                )
                moodColors.forEachIndexed { index, color ->
                    val selected = moodLevel == index
                    val fillColor by animateColorAsState(if (selected) color else color.copy(alpha = 0.3f), label = "moodColor")
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(fillColor).clickable {
                        moodLevel = index
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Steps Today")
            Slider(value = steps.toFloat(), onValueChange = { steps = it.toInt() }, valueRange = 0f..10000f)
            Text("$steps / $stepGoal steps")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Screen Time (hrs)")
            Slider(value = screenTime.toFloat(), onValueChange = { screenTime = it.toInt() }, valueRange = 0f..10f)
            Text("$screenTime hrs (Limit: $screenLimit hrs)")
        }

        Section(title = "Badge") {
            if (badgeEarned) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.badge_congrats),
                        contentDescription = "Badge Earned",
                        modifier = Modifier.size(80.dp).clip(CircleShape).scale(badgeScale)
                    )
                    Text(
                        "Congratulations! You earned the Health Master badge.",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                Text("Complete all tasks above to earn your badge!")
            }
        }

        Section(title = "Badge Collection") {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Image(painter = painterResource(id = R.drawable.badge_1), contentDescription = "Badge 1", modifier = Modifier.size(60.dp).clip(CircleShape))
                Image(painter = painterResource(id = R.drawable.badge_2), contentDescription = "Badge 2", modifier = Modifier.size(60.dp).clip(CircleShape))
                Image(painter = painterResource(id = R.drawable.badge_3), contentDescription = "Badge 3", modifier = Modifier.size(60.dp).clip(CircleShape))
            }
        }

        Section(title = "Tip of the Day") {
            if (!revealTip) {
                Button(onClick = { revealTip = true }) {
                    Text("Reveal Tip", color = Color.White)
                }
            } else {
                Text("Drink a glass of water first thing in the morning to jumpstart your metabolism.")
            }
        }
    }
}

@Composable
fun ProfileHeader(userData: Map<String, Any>?) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF00332E)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Pic",
                modifier = Modifier.size(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = userData?.get("fullName")?.toString() ?: "Guest", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = userData?.get("email")?.toString() ?: "guest@vireya.com", fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
fun Section(title: String, colorful: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(if (colorful) Color(0xFFF1F8E9) else Color.White)
            .padding(16.dp)
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
